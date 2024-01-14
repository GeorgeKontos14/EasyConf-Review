package nl.tudelft.sem.template.example.domain.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import nl.tudelft.sem.template.example.domain.models.PreferenceEntity;
import nl.tudelft.sem.template.model.ReviewerPreferences;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class RevPrefsUtilsTest {

    private List<ReviewerPreferences> preferences;
    private List<PreferenceEntity> entities;

    /**
     * setup method.
     */
    @BeforeEach
    public void setup() {
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
    public void convertTest() {
        assertThat(RevPreftils.convert(entities)).isEqualTo(preferences);
    }

}
