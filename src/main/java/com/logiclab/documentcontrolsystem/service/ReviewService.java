package com.logiclab.documentcontrolsystem.service;

import com.logiclab.documentcontrolsystem.domain.Review;
import com.logiclab.documentcontrolsystem.domain.User;

public interface ReviewService {
    Review createReview(User currentUser);
    void editReview(Review review, User currentUser);

}
