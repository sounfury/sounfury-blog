package org.sounfury.admin.service;

import org.sounfury.admin.dto.req.CategoryAddReq;
import org.sounfury.admin.dto.req.SortCategoryReq;
import org.sounfury.jooq.tables.pojos.Category;
import org.sounfury.portal.dto.rep.CategoryTreeNode;

import java.util.List;
import java.util.Map;

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
    void updateCategory(Category category);

    /**
     * 删除分类
     */
    void deleteCategory(Long id);

    /**
     * 根据categoryId查询分类是否存在
     */
    boolean isExist(Long categoryId);


    void BatchUpdateCategorySort(List<SortCategoryReq> sortCategoryReq);

    Map<Long, String> categoryDict();
}
