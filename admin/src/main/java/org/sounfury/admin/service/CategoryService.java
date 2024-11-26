package org.sounfury.admin.service;

import org.sounfury.portal.dto.rep.CategoryTreeNode;

import java.util.List;

public interface CategoryService {
    /**
     * 查询所有的分类
     */
    List<CategoryTreeNode> getAllCategory();
}
