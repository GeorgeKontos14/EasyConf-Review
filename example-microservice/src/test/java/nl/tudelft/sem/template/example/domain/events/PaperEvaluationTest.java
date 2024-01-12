package nl.tudelft.sem.template.example.domain.events;

import nl.tudelft.sem.template.example.domain.models.TrackPhase;
import nl.tudelft.sem.template.example.domain.repositories.PaperRepository;
import nl.tudelft.sem.template.example.domain.repositories.ReviewRepository;
import nl.tudelft.sem.template.example.domain.repositories.TrackPhaseRepository;
import nl.tudelft.sem.template.model.Paper;
import nl.tudelft.sem.template.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class PaperEvaluationTest {

    private Paper unresolved;
    private Paper accepted;
    private Paper rejected;
    private Review positive;
    private Review negative;

    private PaperRepository paperRepository;
    private ReviewRepository reviewRepository;
    private TrackPhaseRepository trackPhaseRepository;
    private PaperEvaluation evaluation;

    @BeforeEach
    void beforeEach() {
        unresolved = new Paper();
        unresolved.id(1);
        unresolved.finalVerdict(Paper.FinalVerdictEnum.UNRESOLVED);
        accepted = new Paper();
        accepted.id(2);
        accepted.finalVerdict(Paper.FinalVerdictEnum.ACCEPTED);
        rejected = new Paper();
        rejected.id(3);
        rejected.finalVerdict(Paper.FinalVerdictEnum.REJECTED);

        positive = new Review();
        positive.overallScore(Review.OverallScoreEnum.NUMBER_1);
        negative = new Review();
        negative.overallScore(Review.OverallScoreEnum.NUMBER_MINUS_1);

        paperRepository = Mockito.mock(PaperRepository.class);
        reviewRepository = Mockito.mock(ReviewRepository.class);
        trackPhaseRepository = Mockito.mock(TrackPhaseRepository.class);

        evaluation = new PaperEvaluation(paperRepository, reviewRepository, trackPhaseRepository);
    }

    @Test
    void evaluatePaperTest() {
        assertThat(evaluation.evaluatePaper(accepted)).isTrue();
        assertThat(evaluation.evaluatePaper(rejected)).isTrue();

        Mockito.when(reviewRepository.findReviewsByPaperId(1)).thenReturn(List.of(positive, positive, positive));
        Mockito.when(paperRepository.save(unresolved.finalVerdict(Paper.FinalVerdictEnum.ACCEPTED)))
                .thenReturn(unresolved.finalVerdict(Paper.FinalVerdictEnum.ACCEPTED));
        assertThat(evaluation.evaluatePaper(unresolved)).isTrue();

        Mockito.when(reviewRepository.findReviewsByPaperId(1)).thenReturn(List.of(negative, negative, negative));
        Mockito.when(paperRepository.save(unresolved.finalVerdict(Paper.FinalVerdictEnum.REJECTED)))
                .thenReturn(unresolved.finalVerdict(Paper.FinalVerdictEnum.REJECTED));
        assertThat(evaluation.evaluatePaper(unresolved)).isTrue();

        Mockito.when(reviewRepository.findReviewsByPaperId(1)).thenReturn(List.of(positive, negative, negative));
        Mockito.when(paperRepository.save(unresolved.finalVerdict(Paper.FinalVerdictEnum.UNRESOLVED)))
                .thenReturn(unresolved.finalVerdict(Paper.FinalVerdictEnum.UNRESOLVED));
        assertThat(evaluation.evaluatePaper(unresolved)).isTrue();

        Mockito.when(reviewRepository.findReviewsByPaperId(1)).thenReturn(List.of(positive, negative, negative));
        Mockito.when(paperRepository.save(unresolved.finalVerdict(Paper.FinalVerdictEnum.UNRESOLVED)))
                .thenReturn(unresolved.finalVerdict(Paper.FinalVerdictEnum.UNRESOLVED));
        assertThat(evaluation.evaluatePaper(unresolved.finalVerdict(null))).isTrue();

        Mockito.when(reviewRepository.findReviewsByPaperId(1))
                .thenReturn(List.of(positive, negative));
        Mockito.when(paperRepository.save(unresolved)).thenReturn(new Paper().id(6));

        assertThat(evaluation.evaluatePaper(unresolved)).isFalse();
    }

    @Test
    void evaluatePapersTest() {
        Mockito.when(reviewRepository.findReviewsByPaperId(1)).thenReturn(List.of(positive, positive, positive));
        Mockito.when(paperRepository.save(unresolved.finalVerdict(Paper.FinalVerdictEnum.ACCEPTED)))
                .thenReturn(unresolved.finalVerdict(Paper.FinalVerdictEnum.ACCEPTED));
        assertThat(evaluation.evaluatePapers(List.of(unresolved))).isTrue();
    }

    @Test
    void evaluateAllPapersTest() {
        Mockito.when(paperRepository.findAll()).thenReturn(List.of(unresolved));
        Mockito.when(reviewRepository.findReviewsByPaperId(1)).thenReturn(List.of(positive, positive, positive));
        Mockito.when(paperRepository.save(unresolved.finalVerdict(Paper.FinalVerdictEnum.ACCEPTED)))
                .thenReturn(unresolved.finalVerdict(Paper.FinalVerdictEnum.ACCEPTED));
        assertThat(evaluation.evaluateAllPapers()).isTrue();
    }

    @Test
    void evaluatePaperWithTrackIdTest() {
        TrackPhase t = new TrackPhase(List.of(1));
        t.setId(42);
        Mockito.when(trackPhaseRepository.findById(42)).thenReturn(Optional.of(t));
        Mockito.when(paperRepository.findAllById(List.of(1))).thenReturn(List.of(unresolved));
        Mockito.when(reviewRepository.findReviewsByPaperId(1)).thenReturn(List.of(positive, positive, positive));
        Mockito.when(paperRepository.save(unresolved.finalVerdict(Paper.FinalVerdictEnum.ACCEPTED)))
                .thenReturn(unresolved.finalVerdict(Paper.FinalVerdictEnum.ACCEPTED));
        assertThat(evaluation.evaluatePapersWithTrackId(42)).isTrue();
    }

    @Test
    void evaluatePaperWithTrackIdsTest() {
        TrackPhase t = new TrackPhase(List.of(1));
        t.setId(42);
        Mockito.when(trackPhaseRepository.findById(42)).thenReturn(Optional.of(t));
        Mockito.when(paperRepository.findAllById(List.of(1))).thenReturn(List.of(unresolved));
        Mockito.when(reviewRepository.findReviewsByPaperId(1)).thenReturn(List.of(positive, positive, positive));
        Mockito.when(paperRepository.save(unresolved.finalVerdict(Paper.FinalVerdictEnum.ACCEPTED)))
                .thenReturn(unresolved.finalVerdict(Paper.FinalVerdictEnum.ACCEPTED));
        assertThat(evaluation.evaluatePapersWithTrackIds(List.of(42))).isTrue();
    }
}
