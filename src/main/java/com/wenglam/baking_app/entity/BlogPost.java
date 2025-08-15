package com.wenglam.baking_app.entity;

import java.time.Instant;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "blog_post")
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // IDENTITY is a column property that makes column auto-increment its value for each new row
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false, length = 250)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "author", nullable = false, length = 50)
    private String author;

    @ColumnDefault("CURRENT_TIMESTAMP") // Default value for created_at column - Hibernate annotation
    // Database will automatically set this value to the current timestamp when a new row is inserted
    // only works when generating schema - not during runtime when persisting an entity
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
   
    @ColumnDefault("NULL")
    @Column(name = "updated_at")
    private Instant updatedAt;

    @ColumnDefault("NULL")
    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    // This method is called before the entity is persisted to the database
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }
}
