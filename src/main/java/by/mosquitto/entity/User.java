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
    @Column(name = "id")
    private Long id;

    @Column(name = "username", length = 40, nullable = false, unique = true)
    private String username;

    @Column(name = "password", length = 80, nullable = false)
    private String password;

    @Column(name = "name", length = 20, nullable = false)
    private String name;

    @Column(name = "surname", length = 20, nullable = false)
    private String surname;

    @Column(name = "parentname", length = 20)
    private String parentName;

    @Column(name = "creationdate", nullable = true)
    private LocalDateTime creationDate;

    @Column(name = "lasteditdate", nullable = true)
    private LocalDateTime lastEditDate;

    @OneToMany(mappedBy = "createdByUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<News> createdNews;

    @OneToMany(mappedBy = "updatedByUser", cascade = CascadeType.ALL)
    private List<News> updatedNews;

    @OneToMany(mappedBy = "createdByUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
}
