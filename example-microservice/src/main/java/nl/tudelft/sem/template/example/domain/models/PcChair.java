package nl.tudelft.sem.template.example.domain.models;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.Paper;

@Getter
@Entity
public class PcChair {
    @Id
    private Integer id;

    @javax.persistence.ElementCollection @javax.persistence.CollectionTable(name = "listOfPapers")
    @Valid
    private List<Integer> papers;

    @javax.persistence.ElementCollection @javax.persistence.CollectionTable(name = "listOfComments")
    @Valid
    private List<Integer> comments;

    public PcChair(List<Integer> papers, List<Integer> comments) {
        this.papers = papers;
        this.comments = comments;
    }

    public PcChair(List<Integer> papers) {
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
}
