package by.mosquitto.repository;

import by.mosquitto.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    @Query("SELECT n FROM News n WHERE LOWER(n.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(n.text) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<News> searchByTitleOrText(@Param("query") String query);
}
