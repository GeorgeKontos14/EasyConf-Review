package nl.tudelft.sem.template.example.domain.models;



import java.util.ArrayList;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.Valid;
import lombok.Getter;
import lombok.Setter;

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


    /**
     * Constructor for the pc chair.
     *
     * @param tracks that the pc chair should supervise
     * @param papers that the pc chair should supervise
     * @param comments that the chair has given
     */
    public PcChair(List<Integer> tracks, List<Integer> papers, List<Integer> comments) {
        this.tracks = tracks;
        this.papers = papers;
        this.comments = comments;
    }

    /**
     * Constructor for the pc chair.
     *
     * @param tracks that the pc chair should supervise
     * @param papers that the pc chair should supervise
     */
    public PcChair(List<Integer> tracks, List<Integer> papers) {
        this.tracks = tracks;
        this.papers = papers;
        this.comments = new ArrayList<>();
    }

    /**
     * Constructor for the pc chair.
     *
     * @param tracks that the pc chair should supervise
     */
    public PcChair(List<Integer> tracks) {
        this.tracks = tracks;
        this.papers = new ArrayList<>();
        this.comments = new ArrayList<>();
    }

    /**
     * Method that adds a new comment to the PcChair.
     *
     * @param commentId the comment to be added.
     */
    public void addComment(Integer commentId) {
        comments.add(commentId);
    }

    /**
     * Method that adds a new paper to the PcChair.
     *
     * @param paperId the paper to be added.
     */
    public void addPaper(Integer paperId) {
        papers.add(paperId);
    }

    /**
     * Method that checks if the PC Chair has access to a specific track.
     *
     * @param trackId the track in question.
     * @return true if-f the track is one of the tracks the chair is responsible for.
     */
    public boolean hasAccess(int trackId) {
        return this.tracks.contains(trackId);
    }

}
