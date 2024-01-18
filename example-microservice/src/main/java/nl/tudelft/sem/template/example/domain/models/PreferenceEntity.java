package nl.tudelft.sem.template.example.domain.models;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import nl.tudelft.sem.template.model.ReviewerPreferences;


@Setter
@Getter
@Entity
public class PreferenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column
    private Integer reviewerId;
    @Column
    private Integer paperId;

    @Enumerated(EnumType.STRING)
    private ReviewerPreferences.ReviewerPreferenceEnum preferenceEnum;

    public PreferenceEntity() {
    }

    /**
     * Constructor of PreferenceEntity.
     *
     * @param reviewerId to set the preference for
     * @param paperId to set the preference for
     * @param preferenceEnum the preference score
     */
    public PreferenceEntity(
            int reviewerId, int paperId,
            ReviewerPreferences.ReviewerPreferenceEnum preferenceEnum) {
        this.reviewerId = reviewerId;
        this.paperId = paperId;
        this.preferenceEnum = preferenceEnum;
    }

    /**
     * Changes the String description of an Enum value to the value itself.
     *
     * @param preference - the String with the enum description
     * @return corresponding ReviewerPreferences.ReviewerPreferenceEnum value
     */
    public static ReviewerPreferences.ReviewerPreferenceEnum changeStringToEnumValue(String preference) {
        if (Objects.equals(preference, "Can review")) {
            return ReviewerPreferences.ReviewerPreferenceEnum.CAN_REVIEW;
        } else if (Objects.equals(preference, "Cannot review")) {
            return ReviewerPreferences.ReviewerPreferenceEnum.CANNOT_REVIEW;
        } else {
            return ReviewerPreferences.ReviewerPreferenceEnum.NEUTRAL;
        }
    }

    public static String changeEnumValueToString(ReviewerPreferences.ReviewerPreferenceEnum preference) {
        if (Objects.equals(preference, ReviewerPreferences.ReviewerPreferenceEnum.CAN_REVIEW)) {
            return "Can review";
        } else if (Objects.equals(preference, ReviewerPreferences.ReviewerPreferenceEnum.CANNOT_REVIEW)) {
            return "Cannot review";
        } else {
            return "Neutral";
        }
    }

    /**
     * Converts the current object to a ReviewerPreferences object.
     *
     * @return the object in question.
     */
    public ReviewerPreferences toPreferences() {
        ReviewerPreferences pref = new ReviewerPreferences();
        pref.setReviewerId(this.reviewerId);
        pref.setReviewerPreference(this.preferenceEnum);
        pref.setPaperId(this.paperId);
        return pref;
    }
}
