package nl.tudelft.sem.template.example.domain.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nl.tudelft.sem.template.example.domain.models.PreferenceEntity;
import nl.tudelft.sem.template.example.domain.repositories.ReviewerPreferencesRepository;
import nl.tudelft.sem.template.model.ReviewerPreferences;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ReviewerPreferencesServiceTest {

    private ReviewerPreferencesRepository repo;

    private ReviewerPreferencesService sut;

    private List<ReviewerPreferences> preferences;
    private List<PreferenceEntity> entities;

    public ReviewerPreferences buildPreferences(int reviewerID, int paperID,
                                                ReviewerPreferences.ReviewerPreferenceEnum en) {
        ReviewerPreferences res = new ReviewerPreferences();
        res.setPaperId(paperID);
        res.setReviewerId(reviewerID);
        res.setReviewerPreference(en);
        return res;
    }


    /**
     * setup method.
     */
    @BeforeEach
    public void setup() {
        repo = Mockito.mock(ReviewerPreferencesRepository.class);
        sut = new ReviewerPreferencesService(repo);
        ReviewerPreferences pref1 = buildPreferences(1,1,
                ReviewerPreferences.ReviewerPreferenceEnum.CAN_REVIEW);
        ReviewerPreferences pref2 = buildPreferences(2, 2,
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

    @Test
    public void getPreferencesForTrack() {
        ReviewerPreferences pref3 = buildPreferences(3,2,
                ReviewerPreferences.ReviewerPreferenceEnum.CANNOT_REVIEW);
        ReviewerPreferences pref4 = buildPreferences(4,2,
                ReviewerPreferences.ReviewerPreferenceEnum.NEUTRAL);
        PreferenceEntity e3 = new PreferenceEntity(3,2,
                ReviewerPreferences.ReviewerPreferenceEnum.CANNOT_REVIEW);
        PreferenceEntity e4 = new PreferenceEntity(4,2,
                ReviewerPreferences.ReviewerPreferenceEnum.NEUTRAL);
        List<ReviewerPreferences> prefs = new ArrayList<>(preferences);
        prefs.add(pref3);
        prefs.add(pref4);
        Mockito.when(repo.findAllByPaperId(1)).thenReturn(entities);
        Mockito.when(repo.findAllByPaperId(2)).thenReturn(Arrays.asList(e3, e4));
        assertThat(sut.getPreferencesForTrack(Arrays.asList(1,2))).isEqualTo(prefs);
    }

}
