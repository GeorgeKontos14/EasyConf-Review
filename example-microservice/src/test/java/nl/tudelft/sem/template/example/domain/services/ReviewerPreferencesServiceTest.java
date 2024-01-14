package nl.tudelft.sem.template.example.domain.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import nl.tudelft.sem.template.example.domain.models.PreferenceEntity;
import nl.tudelft.sem.template.example.domain.repositories.ReviewerPreferencesRepository;
import nl.tudelft.sem.template.example.domain.util.RPUtils;
import nl.tudelft.sem.template.model.ReviewerPreferences;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ReviewerPreferencesServiceTest {

    private ReviewerPreferencesRepository repo;

    private ReviewerPreferencesService sut;

    private List<ReviewerPreferences> preferences;
    private List<PreferenceEntity> entities;

    /**
     * setup method.
     */
    @BeforeEach
    public void setup() {
        repo = Mockito.mock(ReviewerPreferencesRepository.class);
        sut = new ReviewerPreferencesService(repo);
        ReviewerPreferences pref1 = new ReviewerPreferences();
        pref1.reviewerId(1);
        pref1.setPaperId(1);
        pref1.reviewerPreference(
                ReviewerPreferences.ReviewerPreferenceEnum.CAN_REVIEW);
        ReviewerPreferences pref2 = new ReviewerPreferences();
        pref2.reviewerId(2);
        pref2.setPaperId(2);
        pref2.reviewerPreference(
                ReviewerPreferences.ReviewerPreferenceEnum.CAN_REVIEW);
        PreferenceEntity e1 = new PreferenceEntity(1, 1,
                ReviewerPreferences.ReviewerPreferenceEnum.CAN_REVIEW);
        PreferenceEntity e2 = new PreferenceEntity(2, 2,
                ReviewerPreferences.ReviewerPreferenceEnum.CAN_REVIEW);
        preferences = Arrays.asList(pref1, pref2);
        entities = Arrays.asList(e1, e2);
    }

    @Test
    public void getPreferencesForPaperTest() {
        Mockito.when(repo.findAllByPaperId(1)).thenReturn(entities);
        assertThat(sut.getPreferencesForPaper(1)).isEqualTo(preferences);
    }

    @Test
    public void getPreferencesForReviewerTest() {
        Mockito.when(repo.findAllByReviewerId(1)).thenReturn(entities);
        assertThat(sut.getPreferencesForReviewer(1)).isEqualTo(preferences);
    }
}
