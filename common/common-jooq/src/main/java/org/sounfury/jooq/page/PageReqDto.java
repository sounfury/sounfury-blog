package org.sounfury.jooq.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jooq.SortField;
import org.jooq.SortOrder;
import org.sounfury.core.convention.exception.ClientException;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.name;
import static org.sounfury.core.convention.errorcode.BaseErrorCode.USER_PAGE_ERROR;
import static org.sounfury.core.convention.errorcode.BaseErrorCode.USER_PAGE_SIZE_ERROR;

@Data
@NoArgsConstructor
public class PageReqDto {

  public static final String REGEX = "^[a-zA-Z][a-zA-Z0-9_]*$";

  public static final String SPACE = " ";

  private int page;
  private int size;

  private Map<String, Direction> sortBy = new HashMap<>();

  public PageReqDto(int page, int size) {
    checkPageAndSize(page, size);
    this.page = page;
    this.size = size;
  }

  public PageReqDto(int page, int size, Map<String, Direction> sortBy) {
    checkPageAndSize(page, size);
    this.page = page;
    this.size = size;
    this.sortBy = sortBy;
  }

  @AllArgsConstructor
  @Getter
  public enum Direction {
    ASC("ASC"),
    DESC("DESC");

    private final String keyword;

    public static Direction fromString(String value) {
      try {
        return Direction.valueOf(value.toUpperCase(Locale.US));
      } catch (Exception e) {
        throw new IllegalArgumentException(
            String.format(
                "Invalid value '%s' for orders given; Has to be either 'desc' or 'asc' (case"
                    + " insensitive)",
                value),
            e);
      }
    }
  }

  public static PageReqDto of(int page, int size) {
    return new PageReqDto(page, size);
  }

  public static PageReqDto of(int page, int size, Map<String, Direction> sortBy) {
    return new PageReqDto(page, size, sortBy);
  }

  public List<SortField<Object>> getSortFields() {
    return sortBy.entrySet().stream()
        .map(
            (entry) ->
                field(name(entry.getKey())).sort(SortOrder.valueOf(entry.getValue().getKeyword())))
        .toList();
  }

  private void checkPageAndSize(int page, int size) {
    if (page <=0) {
      throw new ClientException(USER_PAGE_ERROR);
    }

    if (size < 1) {
      throw new ClientException(USER_PAGE_SIZE_ERROR);
    }
  }

  public long getOffset() {
    checkPageAndSize(page, size);
    return (long) (page-1) * (long) size;
  }

  public void setSortBy(String sortBy) {
    this.sortBy = convertSortBy(sortBy);
  }

  private Map<String, Direction> convertSortBy(String sortBy) {
    Map<String, Direction> result = new HashMap<>();
    if (StringUtils.isEmpty(sortBy)) {
      return result;
    }
    for (String fieldSpaceDirection : sortBy.split(",")) {
      String[] fieldDirectionArray = fieldSpaceDirection.split(SPACE);
      if (fieldDirectionArray.length != 2) {
        throw new IllegalArgumentException(
            String.format(
                "Invalid sortBy field format %s. The expect format is [col1 asc,col2 desc]",
                sortBy));
      }
      String field = fieldDirectionArray[0];
      if (!verifySortField(field)) {
        throw new IllegalArgumentException(
            String.format("Invalid Sort field %s. Sort field must match %s", sortBy, REGEX));
      }
      String direction = fieldDirectionArray[1];
      result.put(field, Direction.fromString(direction));
    }
    return result;
  }

  private static boolean verifySortField(String sortField) {
    Pattern pattern = Pattern.compile(REGEX);
    Matcher matcher = pattern.matcher(sortField);
    return matcher.matches();
  }
}
