package nl.tudelft.sem.template.example.domain.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import nl.tudelft.sem.template.model.ReviewerPreferences;

import java.util.Objects;

@Setter
@Getter
@Entity
public class PreferenceEntity {
    @Id
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
     * Changes the String description of an Enum value to the value itself
     * @param preference - the String with the enum description
     * @return corresponding ReviewerPreferences.ReviewerPreferenceEnum value
     */
    public static ReviewerPreferences.ReviewerPreferenceEnum changeStringToEnumValue(String preference) {
        if (Objects.equals(preference, "Can review")) {
            return ReviewerPreferences.ReviewerPreferenceEnum.CAN_REVIEW;
        }
        else if (Objects.equals(preference, "Cannot review")) {
            return ReviewerPreferences.ReviewerPreferenceEnum.CANNOT_REVIEW;
        }
        else return ReviewerPreferences.ReviewerPreferenceEnum.NEUTRAL;
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
