package nl.tudelft.sem.template.example.domain.models;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ReviewerTest {

    @Test
    public void constructorTest() {
        Reviewer r = new Reviewer();
        assertThat(r.getReviews()).isEmpty();
        assertThat(r.getPreferences()).isEmpty();
        assertThat(r.getId()).isEqualTo(0);
    }

    @Test
    public void constructor2Test() {
        Reviewer r = new Reviewer(42);
        assertThat(r.getReviews()).isEmpty();
        assertThat(r.getPreferences()).isEmpty();
        assertThat(r.getId()).isEqualTo(42);
    }
}
