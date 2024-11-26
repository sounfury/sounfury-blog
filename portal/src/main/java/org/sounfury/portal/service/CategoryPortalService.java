package org.sounfury.portal.service;

import org.sounfury.portal.dto.rep.CategoryTreeNode;

import java.util.List;

public interface CategoryPortalService {
    /**
     * 查询所有的分类
     */
    List<CategoryTreeNode> getAllCategory();
}
