package org.sounfury.portal.service.impl;

import lombok.RequiredArgsConstructor;
import org.sounfury.portal.dto.rep.CategoryTreeNode;
import org.sounfury.portal.repository.CategoryPortalRepository;
import org.sounfury.portal.service.CategoryPortalService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CategoryPortalServiceImpl implements CategoryPortalService {
    private final CategoryPortalRepository categoryRepository;

    @Override
    public List<CategoryTreeNode> getAllCategory() {
        List<org.sounfury.blog.jooq.tables.records.CategoryRecord> allCategories = categoryRepository.getAllCategories();
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
}
