package nl.tudelft.sem.template.example.domain.models;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class PcChair {
    @Id
    private Integer id;

    @OneToMany
    private List<Paper> papers;

    @OneToMany
    private List<Comment> comments;

    public PcChair(List<Paper> papers, List<Comment> comments) {
        this.papers = papers;
        this.comments = comments;
    }

    public PcChair(List<Paper> papers) {
        this.papers = papers;
        this.comments = new ArrayList<>();
    }

    public PcChair() {
        this.papers = new ArrayList<>();
        this.comments = new ArrayList<>();
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public void setPapers(List<Paper> papers) {
        this.papers = papers;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    /**
     * Method that adds a new comment to the PcChair.
     * @param comment the comment to be added.
     */
    public void addComment(Comment comment) {
        comments.add(comment);
    }

    /**
     * Method that adds a new paper to the PcChair.
     * @param paper the paper to be added.
     */
    public void addPaper(Paper paper) {
        papers.add(paper);
    }
}
