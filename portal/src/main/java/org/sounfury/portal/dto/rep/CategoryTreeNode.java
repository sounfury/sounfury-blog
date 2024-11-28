package org.sounfury.portal.dto.rep;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.sounfury.jooq.tables.records.CategoryRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class CategoryTreeNode {
    private Long id;
    private String name;
    private Long pid; // 父分类 ID
    private String description;
    private List<CategoryTreeNode> children = new ArrayList<>(); // 子分类列表

    public CategoryTreeNode(Long id, String name, Long pid, String description) {
        this.id = id;
        this.name = name;
        this.pid = pid;
        this.description = description;
    }




}
