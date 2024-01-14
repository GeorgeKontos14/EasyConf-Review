package nl.tudelft.sem.template.example.domain.repositories;

import java.util.List;
import nl.tudelft.sem.template.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findCommentByPaperId(Integer paperId);
}
