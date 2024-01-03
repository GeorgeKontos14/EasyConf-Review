package nl.tudelft.sem.template.example.domain.models;

import lombok.Getter;

import javax.persistence.*;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class PcChair {
    @Id
    private Integer id;

    @ElementCollection
    @CollectionTable(name = "listOfTracks")
    @Valid
    private List<Integer> tracks;

    @ElementCollection
    @CollectionTable(name = "listOfPapers")
    @Valid
    private List<Integer> papers;

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
    public void setId(Integer id) {
        this.id = id;
    }

    public void setPapers(List<Integer> papers) {
        this.papers = papers;
    }

    public void setComments(List<Integer> comments) {
        this.comments = comments;
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
