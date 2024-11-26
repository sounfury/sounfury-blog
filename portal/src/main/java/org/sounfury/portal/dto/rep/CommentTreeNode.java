package org.sounfury.portal.dto.rep;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jooq.types.UInteger;
import org.sounfury.core.convention.exception.ServiceException;
import org.sounfury.jooq.tables.records.CommentRecord;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentTreeNode {
    private UInteger id; // 评论 ID
    private UInteger userId; // 用户 ID
    private UInteger parentId; // 父评论 ID
    private UInteger topCommentId; // 顶层评论 ID
    private String content; // 评论内容
    private UInteger likeCount; // 点赞数
    private LocalDateTime createTime; // 创建时间
    private List<CommentTreeNode> children = new ArrayList<>(); // 子评论和子评论的子评论




}
