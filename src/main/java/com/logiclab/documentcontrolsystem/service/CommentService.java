package com.logiclab.documentcontrolsystem.service;

import com.logiclab.documentcontrolsystem.domain.User;

import javax.xml.stream.events.Comment;

public interface CommentService {
    Comment createComment(Comment comment, User user);
    void deleteComment(int commentId,User currentUser);
    void editComment(int commentId,User currentUser);
}
