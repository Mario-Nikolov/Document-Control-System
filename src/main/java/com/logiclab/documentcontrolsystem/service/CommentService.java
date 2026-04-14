package com.logiclab.documentcontrolsystem.service;

import com.logiclab.documentcontrolsystem.domain.*;
import com.logiclab.documentcontrolsystem.dto.request.CreateCommentRequest;
import com.logiclab.documentcontrolsystem.dto.request.DeleteCommentRequest;
import com.logiclab.documentcontrolsystem.dto.request.EditCommentRequest;
import com.logiclab.documentcontrolsystem.exceptions.CommentBodyEmptyException;
import com.logiclab.documentcontrolsystem.exceptions.CommentNotFoundException;
import com.logiclab.documentcontrolsystem.exceptions.DocumentVersionNotFoundException;
import com.logiclab.documentcontrolsystem.exceptions.NoPermissionException;
import com.logiclab.documentcontrolsystem.repository.CommentRepository;
import com.logiclab.documentcontrolsystem.repository.DocumentVersionRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class CommentService {
    private final  DocumentVersionRepository documentVersionRepository;
    private final CommentRepository commentRepository;
    private final AuditLogService auditLogService;

    @Transactional
    public Comment createComment(CreateCommentRequest request, User currentUser){
        DocumentVersion version = documentVersionRepository.findById(request.getDocumentVersionId())
                .orElseThrow(DocumentVersionNotFoundException::new);

        if (request.getBody() == null || request.getBody().trim().isEmpty()) {
            throw new CommentBodyEmptyException();
        }

        Comment comment = new Comment();
        comment.setDocumentVersion(version);
        comment.setCommentedBy(currentUser);
        comment.setBody(request.getBody().trim());
        comment.setCreatedAt(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        auditLogService.log(
                currentUser,
                AuditAction.CREATE,
                AuditEntityType.COMMENT,
                savedComment.getId(),
                currentUser.getUsername() + " created comment to version with ID: " + version.getId()
        );

        return savedComment;
    }

    @Transactional
    public Comment editComment(EditCommentRequest request, User currentUser) {
        Comment comment = commentRepository.findById(request.getCommentId())
                .orElseThrow(CommentNotFoundException::new);

        if (request.getBody() == null || request.getBody().trim().isEmpty()) {
            throw new CommentBodyEmptyException();
        }

        if (comment.getCommentedBy().getId().equals(currentUser.getId())) {
            throw new NoPermissionException();
        }

        comment.setBody(request.getBody().trim());

        Comment savedComment = commentRepository.save(comment);

        auditLogService.log(
                currentUser,
                AuditAction.EDIT,
                AuditEntityType.COMMENT,
                savedComment.getId(),
                currentUser + " edited comment with ID: " + savedComment.getId() +
                        " to version with ID: " + savedComment.getDocumentVersion().getId()
        );

        return savedComment;
    }

    @Transactional
    public void deleteComment(DeleteCommentRequest request, User currentUser) {
        Comment comment = commentRepository.findById(request.getCommentId())
                .orElseThrow(CommentNotFoundException::new);

        if (comment.getCommentedBy().getId().equals(currentUser.getId())) {
            throw new NoPermissionException();
        }

        commentRepository.delete(comment);

        auditLogService.log(
                currentUser,
                AuditAction.DELETE,
                AuditEntityType.COMMENT,
                comment.getId(),
                currentUser + " deleted comment with ID: " + comment.getId() +
                        " to version with ID: " + comment.getDocumentVersion().getId()
        );
    }

    public List<Comment> getCommentsByVersion(Integer versionId) {
        documentVersionRepository.findById(versionId)
                .orElseThrow(DocumentVersionNotFoundException::new);

        return commentRepository.findByDocumentVersionIdOrderByCreatedAtAsc(versionId);
    }

}
