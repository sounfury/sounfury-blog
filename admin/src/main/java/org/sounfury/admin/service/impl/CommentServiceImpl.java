package org.sounfury.admin.service.impl;

import lombok.RequiredArgsConstructor;
import org.sounfury.admin.dto.req.CommentPageReq;
import org.sounfury.admin.repository.CommentAdminRepository;
import org.sounfury.admin.service.CommentService;
import org.sounfury.jooq.page.PageRepDto;
import org.sounfury.jooq.tables.records.CommentRecord;
import org.sounfury.portal.dto.rep.CommentTreeNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.sounfury.core.constant.Constants.STATUS_DISABLE;
import static org.sounfury.core.constant.Constants.STATUS_ENABLE;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentAdminRepository commentRepository;

    @Override
    public PageRepDto<List<CommentTreeNode>> listComments(CommentPageReq commentPageReq) {
        PageRepDto<List<CommentRecord>> comments = commentRepository.getComments(commentPageReq);
        List<CommentTreeNode> commentTree = buildCommentTree(comments.getData());
        return new PageRepDto<>(comments.getTotal(), commentTree);
    }

    @Override
    public void auditComment(Long commentId, Boolean pass) {
        Byte status = pass ? STATUS_ENABLE : STATUS_DISABLE;
        commentRepository.updateCommentStatus(commentId, status);
    }

    @Override
    public void deleteComment(Long commentId) {
        commentRepository.deleteCommentById(commentId);
    }

    private List<CommentTreeNode> buildCommentTree(List<CommentRecord> allComments) {
        // 创建结果集
        List<CommentTreeNode> result = new ArrayList<>();

        // 将所有评论按 ID 映射
        Map<Long, CommentTreeNode> commentMap = allComments.stream()
                .collect(Collectors.toMap(CommentRecord::getId,
                        record -> new CommentTreeNode(record.getId(), record.getUserId(), record.getParentId(),
                                record.getTopCommentId(), record.getContent(), record.getLikeCount(),
                                record.getCreateTime(), record.getEnableStatus(), new ArrayList<>())));

        // 遍历所有评论，构造父子关系
        for (CommentRecord record : allComments) {
            Long parentId = record.getParentId();
            CommentTreeNode currentNode = commentMap.get(record.getId());
            if (parentId == null) {
                // 父评论，直接加入结果集
                result.add(currentNode);
            } else {
                // 子评论，加入其父评论的 children 列表
                CommentTreeNode topParentNode = commentMap.get(record.getTopCommentId());
                // 将当前子评论加入顶层父评论的 children 列表
                topParentNode.getChildren()
                        .add(currentNode);
            }
        }
        return result;
    }
}
