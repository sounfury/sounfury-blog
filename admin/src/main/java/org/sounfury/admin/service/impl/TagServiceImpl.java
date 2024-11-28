package org.sounfury.admin.service.impl;

import lombok.RequiredArgsConstructor;
import org.sounfury.admin.service.TagService;
import org.sounfury.portal.dto.rep.TagsQueryRep;
import org.sounfury.portal.repository.TagPortalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagPortalRepository tagRepository;

    @Override
    public List<TagsQueryRep> getAllTags() {
        return tagRepository.fetchAllTag();
    }
}
