package nl.tudelft.sem.template.example.domain.repositories;

import nl.tudelft.sem.template.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    List<Review> findReviewByReviewerId(Integer reviewerId);
    List<Review> findReviewsByPaperId(Integer paperId);
}
