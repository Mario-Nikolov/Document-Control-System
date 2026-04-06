package com.logiclab.documentcontrolsystem.web;

import com.logiclab.documentcontrolsystem.domain.Comment;
import com.logiclab.documentcontrolsystem.domain.User;
import com.logiclab.documentcontrolsystem.dto.request.CreateCommentRequest;
import com.logiclab.documentcontrolsystem.dto.request.DeleteCommentRequest;
import com.logiclab.documentcontrolsystem.dto.request.EditCommentRequest;
import com.logiclab.documentcontrolsystem.dto.response.CommentResponse;
import com.logiclab.documentcontrolsystem.mapper.CommentMapper;
import com.logiclab.documentcontrolsystem.service.CommentService;
import com.logiclab.documentcontrolsystem.service.JWTService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final JWTService jwtService;

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @RequestBody CreateCommentRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        User currentUser = jwtService.extractUser(authHeader);
        Comment savedComment = commentService.createComment(request, currentUser);
        CommentResponse response = commentMapper.toResponse(savedComment);

        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<CommentResponse> editComment(
            @RequestBody EditCommentRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        User currentUser = jwtService.extractUser(authHeader);
        Comment updatedComment = commentService.editComment(request, currentUser);
        CommentResponse response = commentMapper.toResponse(updatedComment);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteComment(
            @RequestBody DeleteCommentRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        User currentUser = jwtService.extractUser(authHeader);
        commentService.deleteComment(request, currentUser);

        return ResponseEntity.ok("Comment deleted successfully!");
    }

    @GetMapping("/version/{versionId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByVersion(@PathVariable Integer versionId) {
        List<CommentResponse> responses = commentService.getCommentsByVersion(versionId)
                .stream()
                .map(commentMapper::toResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }
}