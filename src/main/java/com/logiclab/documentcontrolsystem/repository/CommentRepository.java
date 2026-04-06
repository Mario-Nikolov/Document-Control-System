package com.logiclab.documentcontrolsystem.repository;

import com.logiclab.documentcontrolsystem.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findByDocumentVersionId(Integer documentVersionId);

    List<Comment> findByCommentedById(Integer userId);

    List<Comment> findByDocumentVersionIdOrderByCreatedAtAsc(Integer documentVersionId);
}