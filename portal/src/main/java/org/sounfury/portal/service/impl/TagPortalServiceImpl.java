package org.sounfury.portal.service.impl;

import lombok.RequiredArgsConstructor;
import org.sounfury.portal.dto.rep.TagsQueryRep;
import org.sounfury.portal.repository.TagPortalRepository;
import org.sounfury.portal.service.TagPortalService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagPortalServiceImpl implements TagPortalService {
    private TagPortalRepository tagRepository;

    @Override
    public List<TagsQueryRep> getAllTags() {
        return tagRepository.fetchAllTag();
    }
}
