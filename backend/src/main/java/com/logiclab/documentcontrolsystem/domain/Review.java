package com.logiclab.documentcontrolsystem.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "version_reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "version_id",nullable = false,unique = true)
    private DocumentVersion documentVersion;

    @ManyToOne
    @JoinColumn(name = "reviewer_id",nullable = false)
    private User reviewer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewDecision decision;

    private String comment;

    @Column(name = "reviewed_at",nullable = false)
    private LocalDateTime reviewedAt;
}