package org.example.viralityengine.model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Data

public class Post {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private Long authorId;
        private String content;
        private LocalDateTime createdAt = LocalDateTime.now();
}

