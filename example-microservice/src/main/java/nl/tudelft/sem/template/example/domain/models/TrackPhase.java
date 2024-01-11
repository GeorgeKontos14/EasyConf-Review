package nl.tudelft.sem.template.example.domain.models;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class TrackPhase {
    @Id
    private Integer id;

    @ElementCollection
    @CollectionTable(name = "listOfPapers")
    @Valid
    private List<Integer> papers;

    public enum PhaseEnum {
        BIDDING("Bidding"),
        ASSIGNMENT("Assignment"),
        REVIEW("Review"),
        DISCUSSION("Discussion"),
        FINAL("Final");

        private final String value;
        PhaseEnum(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static PhaseEnum fromValue(String value) {
            for (PhaseEnum b : PhaseEnum.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }

    @Enumerated(EnumType.STRING)
    private PhaseEnum phase;

    public TrackPhase() {
        this.papers = new ArrayList<>();
        this.phase = PhaseEnum.BIDDING;
    }

    public TrackPhase(List<Integer> papers) {
        this.papers = papers;
        this.phase = PhaseEnum.BIDDING;
    }

    public TrackPhase(List<Integer> papers, PhaseEnum phase) {
        this.papers = papers;
        this.phase = phase;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setPapers(List<Integer> papers) {
        this.papers = papers;
    }

    public void setPhase(PhaseEnum phase) {
        this.phase = phase;
    }

    /**
     * Method that shifts the track to the next phase.
     * If there is no phase, the phase is set to bidding.
     * If the phase is already FINAL, nothing changes.
     */
    public void nextPhase() {
        switch (phase.value) {
            case "Bidding" -> phase = PhaseEnum.ASSIGNMENT;
            case "Assignment" -> phase = PhaseEnum.REVIEW;
            case "Review" -> phase = PhaseEnum.DISCUSSION;
            default -> phase = PhaseEnum.FINAL;
        }
    }
}
