package nl.tudelft.sem.template.example.domain.repositories;

import java.util.List;
import nl.tudelft.sem.template.example.domain.models.PreferenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewerPreferencesRepository extends JpaRepository<PreferenceEntity, Integer> {
    List<PreferenceEntity> findAllByReviewerId(Integer reviewerId);

    List<PreferenceEntity> findAllByPaperId(Integer paperId);
}
