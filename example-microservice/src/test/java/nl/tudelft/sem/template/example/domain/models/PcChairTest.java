package nl.tudelft.sem.template.example.domain.models;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

public class PcChairTest {
    private PcChair sut;

    /**
     * Test for the different constructors of the PcChair class
     */
    @Test
    public void constructorsTest() {
        sut = new PcChair(Arrays.asList(1,2));
        // Empty constructor
        assertThat(sut.getPapers().size()).isEqualTo(0);
        assertThat(sut.getComments().size()).isEqualTo(0);
        // Constructor only with papers
        sut = new PcChair(Arrays.asList(1,2), Arrays.asList(1,2));
        assertThat(sut.getPapers()).isEqualTo(Arrays.asList(1,2));
        assertThat(sut.getComments().size()).isEqualTo(0);
        // Constructor both with papers and comments
        sut = new PcChair(Arrays.asList(1,2), Arrays.asList(1,2,3), Arrays.asList(1,2,3));
        assertThat(sut.getPapers()).isEqualTo(Arrays.asList(1,2,3));
        assertThat(sut.getComments()).isEqualTo(Arrays.asList(1,2,3));
    }

    /**
     * Test for the addPaper method.
     * Examines adding a paper to a list with 0 or more elements.
     */
    @Test
    public void addPaperTest() {
        sut = new PcChair(Arrays.asList(1,2));
        sut.addPaper(1);
        assertThat(sut.getPapers()).isEqualTo(Collections.singletonList(1));
        sut.addPaper(2);
        assertThat(sut.getPapers()).isEqualTo(Arrays.asList(1,2));
    }

    /**
     * Test for the addComment method.
     * Examines adding a comment to a list with 0 or more elements.
     */
    @Test
    public void addCommentTest() {
        sut = new PcChair(Arrays.asList(1,2));
        sut.addComment(1);
        assertThat(sut.getComments()).isEqualTo(Collections.singletonList(1));
        sut.addComment(3);
        assertThat(sut.getComments()).isEqualTo(Arrays.asList(1,3));
    }

    /**
     * Tests the class' setters.
     */
    @Test
    public void settersTest() {
        sut = new PcChair(Arrays.asList(1,2));
        sut.setId(3);
        assertThat(sut.getId()).isEqualTo(3);
        sut.setId(1);
        assertThat(sut.getId()).isEqualTo(1);
        sut.setPapers(Arrays.asList(2,3));
        sut.setComments(Arrays.asList(1,3));
        assertThat(sut.getPapers()).isEqualTo(Arrays.asList(2,3));
        assertThat(sut.getComments()).isEqualTo(Arrays.asList(1, 3));
    }

    /**
     * Test for hasAccessMethod.
     */
    @Test
    public void hasAccessTest() {
        sut = new PcChair(Arrays.asList(1,2,3));
        assertThat(sut.hasAccess(2)).isTrue();
        assertThat(sut.hasAccess(33)).isFalse();
    }

}
