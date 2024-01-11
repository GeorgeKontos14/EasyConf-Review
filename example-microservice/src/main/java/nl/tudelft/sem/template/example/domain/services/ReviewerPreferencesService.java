package nl.tudelft.sem.template.example.domain.services;

import nl.tudelft.sem.template.example.domain.repositories.ReviewerPreferencesRepository;
import nl.tudelft.sem.template.model.ReviewerPreferences;
import org.springframework.stereotype.Service;

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
     * @return the list of review preferences of a given reviewer.
     */
    public List<ReviewerPreferences> getPreferencesForReviewer(int reviewerId) {
        return reviewerPreferencesRepository.findAllByReviewerId(reviewerId);
    }
}
