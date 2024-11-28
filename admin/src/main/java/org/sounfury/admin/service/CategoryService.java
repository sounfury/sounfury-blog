package org.sounfury.admin.service;

import org.sounfury.admin.dto.req.CategoryAddReq;
import org.sounfury.admin.dto.req.CategoryUpdateReq;
import org.sounfury.portal.dto.rep.CategoryTreeNode;

import java.util.List;

public interface CategoryService {
    /**
     * 查询所有的分类
     */
    List<CategoryTreeNode> getAllCategory();


    /**
     * 新增分类
     */
    void addCategory(CategoryAddReq categoryAddReq);

    /**
     * 修改分类
     */
    void updateCategory(CategoryUpdateReq categoryAddReq);

    /**
     * 删除分类
     */
    void deleteCategory(Long id);

    /**
     * 根据categoryId查询分类是否存在
     */
    boolean isExist(Long categoryId);

}
