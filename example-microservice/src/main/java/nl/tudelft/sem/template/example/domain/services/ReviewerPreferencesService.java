package nl.tudelft.sem.template.example.domain.services;

import nl.tudelft.sem.template.example.domain.models.PreferenceEntity;
import nl.tudelft.sem.template.example.domain.repositories.ReviewerPreferencesRepository;
import nl.tudelft.sem.template.model.ReviewerPreferences;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewerPreferencesService {
    private transient final ReviewerPreferencesRepository reviewerPreferencesRepository;

    /**
     * Constructor for the service.
     * @param repo the repository of reviewer preferences.
     */
    public ReviewerPreferencesService(ReviewerPreferencesRepository repo) {
        this.reviewerPreferencesRepository = repo;
    }

    /**
     * Finds all the preferences of a reviewer.
     * @param reviewerId the id of the reviewer in question.
     * @return the list of reviewer preferences of a given reviewer.
     */
    public List<ReviewerPreferences> getPreferencesForReviewer(int reviewerId) {
        return convert(reviewerPreferencesRepository.findAllByReviewerId(reviewerId));
    }

    /**
     * Converts a list of Preference Entities to ReviewerPreferences objects.
     * @param entities the list of entities.
     * @return the list of objects.
     */
    List<ReviewerPreferences> convert(List<PreferenceEntity> entities) {
        List<ReviewerPreferences> result = new ArrayList<>();
        for (PreferenceEntity e : entities)
            result.add(e.toPreferences());
        return result;
    }

    /**
     * Finds all the preferences for a paper.
     * @param paperId the id of the paper in question.
     * @return the list of reviewer preferences for a given paper.
     */
    public List<ReviewerPreferences> getPreferencesForPaper(int paperId) {
        return convert(reviewerPreferencesRepository.findAllByPaperId(paperId));
    }
}
