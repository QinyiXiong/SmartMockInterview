package com.sdumagicode.backend.mapper;

import com.sdumagicode.backend.core.mapper.Mapper;
import com.sdumagicode.backend.dto.Author;
import com.sdumagicode.backend.dto.CommentDTO;
import com.sdumagicode.backend.entity.Comment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ronger
 */
public interface CommentMapper extends Mapper<Comment> {
    /**
     * 获取文章评论列表
     *
     * @param idArticle
     * @return
     */
    List<CommentDTO> selectArticleComments(@Param("idArticle") Integer idArticle);

    /**
     * 查询评论作者
     *
     * @param commentAuthorId
     * @return
     */
    Author selectAuthor(@Param("commentAuthorId") Integer commentAuthorId);

    /**
     * 查询父评论作者
     *
     * @param commentOriginalCommentId
     * @return
     */
    Author selectCommentOriginalAuthor(@Param("commentOriginalCommentId") Integer commentOriginalCommentId);

    /**
     * 更新文章评论分享链接
     *
     * @param idComment
     * @param commentSharpUrl
     * @return
     */
    Integer updateCommentSharpUrl(@Param("idComment") Long idComment, @Param("commentSharpUrl") String commentSharpUrl);

    /**
     * 获取评论列表数据
     *
     * @return
     */
    List<CommentDTO> selectComments();
}
