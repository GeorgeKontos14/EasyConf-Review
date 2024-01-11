package nl.tudelft.sem.template.example.domain.models;

import lombok.Getter;
import lombok.Setter;
import nl.tudelft.sem.template.model.ReviewerPreferences;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@Entity
public class Reviewer {
    @Id
    private Integer id;


    /**
     * This is a list of the reviews a single reviewer is involved in
     */
    @ElementCollection
    @CollectionTable(name = "listOfReviews")
    @Valid
    private List<Integer> reviews;

    /**
     * This is a map of papers Ids to the reviewers preferences
     */
    @ElementCollection
    @CollectionTable(name = "listOfPreferences")
    @Valid
    private HashMap<Integer, ReviewerPreferences.ReviewerPreferenceEnum> preferences;

    public Reviewer(Integer id) {
        this.id = id;
        this.reviews = new ArrayList<>();
        this.preferences = new HashMap<>();
    }

    public Reviewer() {
        this.id = 0;
        this.reviews = new ArrayList<>();
        this.preferences = new HashMap<>();
    }

}
