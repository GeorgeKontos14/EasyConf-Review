package nl.tudelft.sem.template.example.domain.repositories;

import nl.tudelft.sem.template.example.domain.models.PreferenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewerPreferencesRepository extends JpaRepository<PreferenceEntity, Integer> {
    List<PreferenceEntity> findAllByReviewerId(Integer reviewerId);
    List<PreferenceEntity> findAllByPaperId(Integer paperId);
}
