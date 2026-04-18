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
@Table(name = "version_comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "version_id",nullable = false)
    private DocumentVersion documentVersion;

    @ManyToOne
    @JoinColumn(name = "author_id",nullable = false)
    private User commentedBy;

    @Column(nullable = false)
    private String body;

    @Column(name = "created_at",nullable = false)
    private LocalDateTime createdAt;
}
