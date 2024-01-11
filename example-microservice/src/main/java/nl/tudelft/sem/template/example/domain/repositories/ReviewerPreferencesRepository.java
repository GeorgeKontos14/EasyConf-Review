package nl.tudelft.sem.template.example.domain.repositories;

import nl.tudelft.sem.template.model.ReviewerPreferences;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewerPreferencesRepository extends JpaRepository<ReviewerPreferences, Integer> {
    List<ReviewerPreferences> findAllByReviewerId(Integer reviewerId);
    List<ReviewerPreferences> findAllByPaperId(Integer paperId);
}
