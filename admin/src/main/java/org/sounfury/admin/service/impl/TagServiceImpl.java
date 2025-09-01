package org.sounfury.admin.service.impl;

import lombok.RequiredArgsConstructor;
import org.sounfury.admin.service.TagService;
import org.sounfury.blog.jooq.tables.pojos.Tag;
import org.sounfury.portal.dto.rep.TagsQueryRep;
import org.sounfury.portal.repository.TagPortalRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagPortalRepository tagRepository;

    @Override
    public List<TagsQueryRep> getAllTags() {
        return tagRepository.fetchAllTag();
    }

    @Override
    public Map<Long, String> tagDict() {
        return tagRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Tag::getId,  // Map 的 key
                        Tag::getName // Map 的 value
                ));
    }

}
