package org.sounfury.portal.dto.rep;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jooq.types.UInteger;
import org.sounfury.jooq.tables.records.CategoryRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class CategoryTreeNode {
    private UInteger id;
    private String name;
    private UInteger pid; // 父分类 ID
    private String description;
    private List<CategoryTreeNode> children = new ArrayList<>(); // 子分类列表

    public CategoryTreeNode(UInteger id, String name, UInteger pid, String description) {
        this.id = id;
        this.name = name;
        this.pid = pid;
        this.description = description;
    }




}
