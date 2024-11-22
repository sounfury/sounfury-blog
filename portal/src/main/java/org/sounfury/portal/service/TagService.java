package org.sounfury.portal.service;

import org.sounfury.portal.dto.rep.TagsQueryRep;

import java.util.List;

public interface TagService {

    /**
     * 得到所有标签
     *
     */
    List<TagsQueryRep> getAllTags();
}
