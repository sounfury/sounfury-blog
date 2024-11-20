package org.sounfury.jooq.page;

import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class PageRepDto<T> {
  private long total;
  private T data;

  public PageRepDto(long total, @Nullable T data) {
    if (total < 0) {
      throw new IllegalArgumentException("total must not be less than zero");
    }
    this.total = total;
    this.data = data;
  }

  public static <T> PageRepDto<T> empty() {
    return new PageRepDto<>(0, null);
  }
}
