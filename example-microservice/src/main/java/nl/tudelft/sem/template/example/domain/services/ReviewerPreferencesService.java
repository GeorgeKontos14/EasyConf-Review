package nl.tudelft.sem.template.example.domain.services;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.example.domain.models.PreferenceEntity;
import nl.tudelft.sem.template.example.domain.repositories.ReviewerPreferencesRepository;
import nl.tudelft.sem.template.example.domain.util.ReviewerPreferencesUtils;
import nl.tudelft.sem.template.model.ReviewerPreferences;
import org.springframework.stereotype.Service;

@Service
public class ReviewerPreferencesService {
    private final transient ReviewerPreferencesRepository reviewerPreferencesRepository;

    /**
     * Constructor for the service.
     *
     * @param repo the repository of reviewer preferences.
     */
    public ReviewerPreferencesService(ReviewerPreferencesRepository repo) {
        this.reviewerPreferencesRepository = repo;
    }

    /**
     * Finds all the preferences of a reviewer.
     *
     * @param reviewerId the id of the reviewer in question.
     * @return the list of reviewer preferences of a given reviewer.
     */
    public List<ReviewerPreferences> getPreferencesForReviewer(int reviewerId) {
        return ReviewerPreferencesUtils.convert(reviewerPreferencesRepository.findAllByReviewerId(reviewerId));
    }

    /**
     * Finds all the preferences for a paper.
     *
     * @param paperId the id of the paper in question.
     * @return the list of reviewer preferences for a given paper.
     */
    public List<ReviewerPreferences> getPreferencesForPaper(int paperId) {
        return ReviewerPreferencesUtils.convert(reviewerPreferencesRepository.findAllByPaperId(paperId));
    }

    /**
     * Saves the provided ReviewerPreference entity.
     *
     * @param preferenceEntity the entity which should be saved
     * @return the updated PreferenceEntity Object
     */

    public PreferenceEntity saveReviewerPreference(PreferenceEntity preferenceEntity) {
        return reviewerPreferencesRepository.save(preferenceEntity);
    }

    /**
     * Retrieves all the reviewer preferences for a specified track.
     *
     * @param paperIDs the IDs of the papers in the track
     * @return the reviewer preferences for those papers
     */
    public List<ReviewerPreferences> getPreferencesForTrack(List<Integer> paperIDs) {
        List<ReviewerPreferences> res = new ArrayList<>();
        for (Integer p: paperIDs)
            res.addAll(getPreferencesForPaper(p));
        return res;
    }

}
