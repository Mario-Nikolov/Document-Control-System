package com.logiclab.documentcontrolsystem.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "document_versions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"document_id", "version_number"})
        }
)
public class DocumentVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "document_id",nullable = false)
    private Document document;

    @Column(name = "version_number",nullable = false)
    private int versionNumber;

    @ManyToOne
    @JoinColumn(name = "parent_version_id")
    private DocumentVersion parentVersion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VersionStatus status;

    @Column(name = "is_active",nullable = false)
    private boolean isActive;

    @ManyToOne
    @JoinColumn(name = "created_by",nullable = false)
    private User createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Lob
    @Column(name = "content", nullable = false)
    private byte[] content;

    private String extension;

    @Column(name = "change_summary")
    private String changeSummary;
}
