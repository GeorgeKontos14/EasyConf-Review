package nl.tudelft.sem.template.example.domain.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class PcChair {
    @Setter
    @Id
    private Integer id;

    @ElementCollection
    @CollectionTable(name = "listOfTracks")
    @Valid
    private List<Integer> tracks;

    @Setter
    @ElementCollection
    @CollectionTable(name = "listOfPapers")
    @Valid
    private List<Integer> papers;

    @Setter
    @ElementCollection
    @CollectionTable(name = "listOfComments")
    @Valid
    private List<Integer> comments;

    public PcChair() {
        this.papers = new ArrayList<>();
        this.comments = new ArrayList<>();
    }


    public PcChair(List<Integer> tracks, List<Integer> papers, List<Integer> comments) {
        this.tracks = tracks;
        this.papers = papers;
        this.comments = comments;
    }

    public PcChair(List<Integer> tracks, List<Integer> papers) {
        this.tracks = tracks;
        this.papers = papers;
        this.comments = new ArrayList<>();
    }

    public PcChair(List<Integer> tracks) {
        this.tracks = tracks;
        this.papers = new ArrayList<>();
        this.comments = new ArrayList<>();
    }

    /**
     * Method that adds a new comment to the PcChair.
     * @param commentID the comment to be added.
     */
    public void addComment(Integer commentID) {
        comments.add(commentID);
    }

    /**
     * Method that adds a new paper to the PcChair.
     * @param paperID the paper to be added.
     */
    public void addPaper(Integer paperID) {
        papers.add(paperID);
    }

    /**
     * Method that checks if the PC Chair has access to a specific track.
     * @param trackID the track in question.
     * @return true if-f the track is one of the tracks the chair is responsible for.
     */
    public boolean hasAccess(int trackID) {
        return this.tracks.contains(trackID);
    }

}
