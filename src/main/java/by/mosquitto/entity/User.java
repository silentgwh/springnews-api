package by.mosquitto.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "app_user")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false, unique = true)
    private String username;

    @Column(length = 255, nullable = false)
    private String password;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 50, nullable = false)
    private String surname;

    @Column(name = "parent_name", length = 50, nullable = false)
    private String parentName;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "last_edit_date", nullable = false)
    private LocalDateTime lastEditDate;

    @OneToMany(mappedBy = "createdByUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<News> createdNews;

    @OneToMany(mappedBy = "updatedByUser", cascade = CascadeType.ALL)
    private List<News> updatedNews;

    @OneToMany(mappedBy = "createdByUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
}
