package nl.tudelft.sem.template.example.domain.models;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Reviewer {
    @Id
    private Integer id;


    /**
     * This is a list of the reviews a single reviewer is involved in.
     */
    @ElementCollection
    private List<Integer> reviews;

    /**
     * This is a list with the ids of all the reviewer preferences.
     */
    @ElementCollection
    private List<Integer> preferences;


    /**
     * Constructor for the reviewer class.
     *
     * @param id to set
     */
    public Reviewer(Integer id) {
        this.id = id;
        this.reviews = new ArrayList<>();
        this.preferences = new ArrayList<>();
    }

    /**
     * Constructor for the reviewer class.
     */
    public Reviewer() {
        this.id = 0;
        this.reviews = new ArrayList<>();
        this.preferences = new ArrayList<>();
    }

}
