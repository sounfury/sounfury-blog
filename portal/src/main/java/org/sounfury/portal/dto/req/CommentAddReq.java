package org.sounfury.portal.dto.req;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jooq.types.UInteger;
import org.sounfury.jooq.tables.pojos.Comment;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@AutoMapper(target = Comment.class)
public class CommentAddReq {
    /**
     * 评论的文章 ID
     */
    private Long articleId;
    /**
     * 评论的用户 ID
     */
    private Long userId;
    /**
     * 评论的直接父评论 ID
     */
    private Long parentId;
    /**
     * 评论的内容
     */
    @NotEmpty(message = "评论内容不能为空")
    private String content;
    /**
     * 评论的顶级评论 ID
     */
    private Long topCommentId;
}
