package nl.tudelft.sem.template.example.domain.models;

import static org.assertj.core.api.Assertions.assertThat;


import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.Paper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

public class PcChairTest {
    /**private PcChair sut;
    private Paper p1;
    private Paper p2;
    private Paper p3;
    private Comment c1;
    private Comment c2;
    private Comment c3;

    @BeforeEach
    public void setup() {
        p1 = new Paper();
        p1.setId(1);
        p2 = new Paper();
        p2.setId(2);
        p3 = new Paper();
        p3.setId(3);
        c1 = new Comment();
        c1.setId(1);
        c2 = new Comment();
        c2.setId(2);
        c3 = new Comment();
        c3.setId(3);
        sut = new PcChair();
    }**/


    /**
     * Test for the different constructors of the PcChair class
     */
    /*@Test
    public void constructorsTest() {
        // Empty constructor
        assertThat(sut.getPapers().size()).isEqualTo(0);
        assertThat(sut.getComments().size()).isEqualTo(0);
        // Constructor only with papers
        sut = new PcChair(Arrays.asList(p1, p2));
        assertThat(sut.getPapers()).isEqualTo(Arrays.asList(p1, p2));
        assertThat(sut.getComments().size()).isEqualTo(0);
        // Constructor both with papers and comments
        sut = new PcChair(Arrays.asList(p1, p2, p3), Arrays.asList(c1, c2, c3));
        assertThat(sut.getPapers()).isEqualTo(Arrays.asList(p1, p2, p3));
        assertThat(sut.getComments()).isEqualTo(Arrays.asList(c1, c2, c3));
    }*/

    /**
     * Test for the addPaper method.
     * Examines adding a paper to a list with 0 or more elements.
     */
    /*@Test
    public void addPaperTest() {
        sut.addPaper(p1);
        assertThat(sut.getPapers()).isEqualTo(Collections.singletonList(p1));
        sut.addPaper(p2);
        assertThat(sut.getPapers()).isEqualTo(Arrays.asList(p1, p2));
    }*/

    /**
     * Test for the addComment method.
     * Examines adding a comment to a list with 0 or more elements.
     */
    /*@Test
    public void addCommentTest() {
        sut.addComment(c1);
        assertThat(sut.getComments()).isEqualTo(Collections.singletonList(c1));
        sut.addComment(c3);
        assertThat(sut.getComments()).isEqualTo(Arrays.asList(c1, c3));
    }*/

    /**
     * Tests the class' setters.
     */
    /*@Test
    public void settersTest() {
        sut.setId(3);
        assertThat(sut.getId()).isEqualTo(3);
        sut.setId(1);
        assertThat(sut.getId()).isEqualTo(1);
        sut.setPapers(Arrays.asList(p2, p3));
        sut.setComments(Arrays.asList(c1, c3));
        assertThat(sut.getPapers()).isEqualTo(Arrays.asList(p2,p3));
        assertThat(sut.getComments()).isEqualTo(Arrays.asList(c1, c3));
    }*/
}
