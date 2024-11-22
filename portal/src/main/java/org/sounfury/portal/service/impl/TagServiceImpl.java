package org.sounfury.portal.service.impl;

import lombok.RequiredArgsConstructor;
import org.sounfury.portal.dto.rep.TagsQueryRep;
import org.sounfury.portal.repository.TagRepository;
import org.sounfury.portal.service.TagService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private TagRepository tagRepository;

    @Override
    public List<TagsQueryRep> getAllTags() {
        return tagRepository.fetchAllTag();
    }
}
