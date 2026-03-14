package com.logiclab.documentcontrolsystem.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "documents")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;                 //При създаване на документ неговата първа версия е активна

    @Column(nullable = false,length = 255)
    private String title;           //Съдържанието на документа е в неговата активна версия

    private String description;

    @OneToMany(mappedBy = "document")
    private List<DocumentVersion> versions;

    @ManyToOne
    @JoinColumn(name = "created_by",nullable = false)
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "active_version_id")
    private DocumentVersion activeVersion;

    @Column(name = "created_at",nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at",nullable = false)
    private LocalDateTime updatedAt;
}
