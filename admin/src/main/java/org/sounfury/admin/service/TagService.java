package org.sounfury.admin.service;

import org.sounfury.portal.dto.rep.TagsQueryRep;

import java.util.List;
import java.util.Map;

public interface TagService {

    /**
     * 得到所有标签
     *
     */
    List<TagsQueryRep> getAllTags();

    Map<Long, String> tagDict();
}
