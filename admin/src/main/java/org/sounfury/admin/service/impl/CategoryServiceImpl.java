package org.sounfury.admin.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.sounfury.admin.dto.req.CategoryAddReq;
import org.sounfury.admin.dto.req.SortCategoryReq;
import org.sounfury.admin.repository.ArticleAdminRepository;
import org.sounfury.admin.repository.CategoryAdminRepository;
import org.sounfury.admin.service.CategoryService;
import org.sounfury.core.convention.exception.ClientException;
import org.sounfury.core.utils.MapstructUtils;
import org.sounfury.blog.jooq.tables.pojos.Category;
import org.sounfury.blog.jooq.tables.records.CategoryRecord;
import org.sounfury.portal.dto.rep.CategoryTreeNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryAdminRepository categoryAdminRepository;
    private final ArticleAdminRepository articleAdminRepository;

    @Override
    public List<CategoryTreeNode> getAllCategory() {
        List<CategoryRecord> allCategories = categoryAdminRepository.getAllCategories();
        return buildCategoryTree(allCategories);
    }

    private List<CategoryTreeNode> buildCategoryTree(
            List<org.sounfury.blog.jooq.tables.records.CategoryRecord> categoryRecords) {
        // 将所有分类转换为树节点
        Map<Long, CategoryTreeNode> nodeMap = categoryRecords.stream()
                .collect(Collectors.toMap(
                        org.sounfury.blog.jooq.tables.records.CategoryRecord::getId,
                        record -> new CategoryTreeNode(
                                record.getId(),
                                record.getName(),
                                record.getPid(),
                                record.getDescription(),
                                record.getOrder()
                        )
                ));
        List<CategoryTreeNode> tree = new ArrayList<>();
        for (CategoryTreeNode node : nodeMap.values()) {
            if (node.getPid() == null) {
                // 没有父分类，加入根节点
                tree.add(node);
            } else {
                CategoryTreeNode parent = nodeMap.get(node.getPid());
                if (parent != null) {
                    parent.getChildren()
                            .add(node);
                }
            }
        }

        return tree;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCategory(CategoryAddReq categoryAddReq) {
        Category convert = MapstructUtils.convert(categoryAddReq, Category.class);
        long userId = StpUtil.getLoginIdAsLong();
        convert.setCreateBy(userId);
        convert.setUpdateBy(userId);
        //检查是否重名
        if (categoryAdminRepository.isExistByName(convert.getName())) {
            throw new ClientException("分类名称已存在");
        }
        categoryAdminRepository.insertCategory(convert);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(Category category) {
        long userId = StpUtil.getLoginIdAsLong();
        category.setUpdateBy(userId);
        categoryAdminRepository.updateCategory(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id) {
        //级联删除，把所有子分类也删除
        //先查询出所有子分类
        List<Long> allChildIds = buildChildrenTree(id);
        if(allChildIds.isEmpty()) {
            allChildIds=new ArrayList<>();
        }
        //删除分类和文章的关联关系,即更新文章的分类id为默认分类
        allChildIds.add(id);
        categoryAdminRepository.deleteBatchByIds(allChildIds);
    }

    @Override
    public boolean isExist(Long categoryId) {
        return categoryAdminRepository.fetchById(categoryId) != null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void BatchUpdateCategorySort(List<SortCategoryReq> sortCategoryReq) {
        //批量更新分类排序
        List<Category> convert = MapstructUtils.convert(sortCategoryReq, Category.class);
        if (convert != null) {
            categoryAdminRepository.batchUpdateCategorySort(convert);
        }
    }

    @Override
    public Map<Long, String> categoryDict() {
        List<CategoryRecord> allCategories = categoryAdminRepository.getAllCategories();
        return allCategories.stream()
                .collect(Collectors.toMap(CategoryRecord::getId, CategoryRecord::getName));
    }


    private List<Long> buildChildrenTree(Long pid) {
        List<Category> directChildren = categoryAdminRepository.fetchByPid(pid);

        // 空值和空列表检查
        if (directChildren == null || directChildren.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> directChildIds = directChildren.stream()
                .map(Category::getId)
                .toList();

        Set<Long> allChildIds = new HashSet<>(directChildIds);

        for (Long childId : directChildIds) {
            allChildIds.addAll(buildChildrenTree(childId));
        }
        return new ArrayList<>(allChildIds);
    }
}
