package nl.tudelft.sem.template.example.domain.repositories;

import nl.tudelft.sem.template.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findCommentByPaperId(Integer paperId);
}
