package org.sounfury.admin.service.impl;

import lombok.RequiredArgsConstructor;
import org.sounfury.admin.service.CategoryService;
import org.sounfury.portal.dto.rep.CategoryTreeNode;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

    @Override
    public List<CategoryTreeNode> getAllCategory() {
        return List.of();
    }
}
