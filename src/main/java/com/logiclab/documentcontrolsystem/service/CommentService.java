package com.logiclab.documentcontrolsystem.service;

import com.logiclab.documentcontrolsystem.domain.*;
import com.logiclab.documentcontrolsystem.dto.request.CreateCommentRequest;
import com.logiclab.documentcontrolsystem.dto.request.DeleteCommentRequest;
import com.logiclab.documentcontrolsystem.dto.request.EditCommentRequest;
import com.logiclab.documentcontrolsystem.repository.CommentRepository;
import com.logiclab.documentcontrolsystem.repository.DocumentVersionRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class CommentService {
    private final  DocumentVersionRepository documentVersionRepository;
    private final CommentRepository commentRepository;
    private final AuditLogService auditLogService;

    @Transactional
    public Comment createComment(CreateCommentRequest request, User currentUser){
        DocumentVersion version = documentVersionRepository.findById(request.getDocumentVersionId())
                .orElseThrow(()-> new RuntimeException("Document version not found!"));

        if (request.getBody() == null || request.getBody().trim().isEmpty()) {
            throw new RuntimeException("Comment body cannot be empty!");
        }

        Comment comment = new Comment();
        comment.setDocumentVersion(version);
        comment.setCommentedBy(currentUser);
        comment.setBody(request.getBody());
        comment.setCreatedAt(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        auditLogService.log(
                currentUser,
                AuditAction.CREATE,
                AuditEntityType.COMMENT,
                savedComment.getId(),
                currentUser + " created comment to version with ID: " + version.getId()
        );

        return savedComment;
    }

    @Transactional
    public Comment editComment(EditCommentRequest request, User currentUser) {
        Comment comment = commentRepository.findById(request.getCommentId())
                .orElseThrow(() -> new RuntimeException("No comment found!"));

        if (request.getBody() == null || request.getBody().trim().isEmpty()) {
            throw new RuntimeException("Comment body cannot be empty!");
        }

        if (comment.getCommentedBy().getId() != currentUser.getId()) {
            throw new RuntimeException("You don't have permission to edit this comment!");
        }

        comment.setBody(request.getBody());

        Comment savedComment = commentRepository.save(comment);

        auditLogService.log(
                currentUser,
                AuditAction.EDIT,
                AuditEntityType.COMMENT,
                savedComment.getId(),
                currentUser + " edited comment with ID: " + savedComment.getId() +
                        " to version with ID: " + savedComment.getDocumentVersion()
        );

        return savedComment;
    }

    @Transactional
    public void deleteComment(DeleteCommentRequest request, User currentUser) {
        Comment comment = commentRepository.findById(request.getCommentId())
                .orElseThrow(() -> new RuntimeException("Comment not found!"));

        if (comment.getCommentedBy().getId() != currentUser.getId()) {
            throw new RuntimeException("You don't have permission to delete this comment!");
        }

        commentRepository.delete(comment);

        auditLogService.log(
                currentUser,
                AuditAction.DELETE,
                AuditEntityType.COMMENT,
                comment.getId(),
                currentUser + " deleted comment with ID: " + comment.getId() +
                        " to version with ID: " + comment.getDocumentVersion()
        );
    }

}
