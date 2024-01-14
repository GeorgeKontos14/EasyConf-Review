package nl.tudelft.sem.template.example.domain.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import nl.tudelft.sem.template.model.ReviewerPreferences;

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
