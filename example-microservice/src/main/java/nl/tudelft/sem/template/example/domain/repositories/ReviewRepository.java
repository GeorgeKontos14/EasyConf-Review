package nl.tudelft.sem.template.example.domain.repositories;

import java.util.List;
import nl.tudelft.sem.template.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    List<Review> findReviewByReviewerId(Integer reviewerId);

    List<Review> findReviewsByPaperId(Integer paperId);
}
