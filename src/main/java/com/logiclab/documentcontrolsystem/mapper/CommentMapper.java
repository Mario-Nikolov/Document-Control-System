package com.logiclab.documentcontrolsystem.mapper;

import com.logiclab.documentcontrolsystem.domain.Comment;
import com.logiclab.documentcontrolsystem.dto.response.CommentResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentMapper {

    public CommentResponse toResponse(Comment comment){
        if (comment ==null){
            return null;
        }

        CommentResponse response = new CommentResponse();

        response.setId(comment.getId());
        response.setBody(comment.getBody());
        response.setCreatedAt(comment.getCreatedAt());

        if (comment.getDocumentVersion() !=null) {
            response.setDocumentVersionId(comment.getDocumentVersion().getId());
        }

        if (comment.getCommentedBy() !=null) {
            response.setCommentedByUsername(comment.getCommentedBy().getUsername());
        }

        return response;
    }

    public List<CommentResponse> toResponseList(List<Comment> comments){
        return comments.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
