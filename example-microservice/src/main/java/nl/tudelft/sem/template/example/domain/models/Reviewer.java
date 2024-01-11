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
    private List<Integer> reviews;

    /**
     * This is a list with the ids of all the reviewer preferences
     */
    @ElementCollection
    private List<Integer> preferences;


    public Reviewer(Integer id) {
        this.id = id;
        this.reviews = new ArrayList<>();
        this.preferences = new ArrayList<>();
    }

    public Reviewer() {
        this.id = 0;
        this.reviews = new ArrayList<>();
        this.preferences = new ArrayList<>();
    }

}
