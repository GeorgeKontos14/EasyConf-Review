package nl.tudelft.sem.template.example.domain.models;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class TrackPhaseTest {
    private TrackPhase sut;

    /**
     * Test for the different constructors of the TrackPhaseClass.
     */
    @Test
    public void constructorsTest() {
        sut = new TrackPhase();
        assertThat(sut.getPapers().isEmpty()).isTrue();
        assertThat(sut.getPhase()).isEqualTo(TrackPhase.PhaseEnum.BIDDING);
        sut = new TrackPhase(Arrays.asList(1, 2, 3));
        assertThat(sut.getPapers()).isEqualTo(Arrays.asList(1, 2, 3));
        assertThat(sut.getPhase()).isEqualTo(TrackPhase.PhaseEnum.BIDDING);
        sut = new TrackPhase(Arrays.asList(1, 2), TrackPhase.PhaseEnum.FINAL);
        assertThat(sut.getPapers()).isEqualTo(Arrays.asList(1, 2));
        assertThat(sut.getPhase()).isEqualTo(TrackPhase.PhaseEnum.FINAL);
    }

    /**
     * Test for the next phase method.
     */
    @Test
    public void nextPhaseTest() {
        sut = new TrackPhase();
        sut.nextPhase();
        assertThat(sut.getPhase()).isEqualTo(TrackPhase.PhaseEnum.ASSIGNMENT);
        sut.nextPhase();
        assertThat(sut.getPhase()).isEqualTo(TrackPhase.PhaseEnum.REVIEW);
        sut.nextPhase();
        assertThat(sut.getPhase()).isEqualTo(TrackPhase.PhaseEnum.DISCUSSION);
        sut.nextPhase();
        assertThat(sut.getPhase()).isEqualTo(TrackPhase.PhaseEnum.FINAL);
        sut.nextPhase();
        assertThat(sut.getPhase()).isEqualTo(TrackPhase.PhaseEnum.FINAL);
    }

}
