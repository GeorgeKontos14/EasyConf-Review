package nl.tudelft.sem.template.example.domain.events;

import nl.tudelft.sem.template.example.domain.models.TrackPhase;
import nl.tudelft.sem.template.example.domain.repositories.PaperRepository;
import nl.tudelft.sem.template.example.domain.repositories.ReviewRepository;
import nl.tudelft.sem.template.example.domain.repositories.TrackPhaseRepository;
import nl.tudelft.sem.template.model.Paper;
import nl.tudelft.sem.template.model.Review;

import java.util.List;
import java.util.Optional;

public class PaperEvaluation {

    private final PaperRepository paperRepository;
    private final ReviewRepository reviewRepository;
    private final TrackPhaseRepository trackPhaseRepository;

    public PaperEvaluation(PaperRepository paperRepository, ReviewRepository reviewRepository, TrackPhaseRepository trackPhaseRepository) {
        this.paperRepository = paperRepository;
        this.reviewRepository = reviewRepository;
        this.trackPhaseRepository = trackPhaseRepository;
    }

    public boolean evaluatePapersWithTrackIds(List<Integer> trackIds) {
        boolean success = true;
        for (Integer n: trackIds) {
            success &= evaluatePapersWithTrackId(n);
        }
        return success;
    }

    public boolean evaluatePapersWithTrackId(int trackId) {
        Optional<TrackPhase> optional = trackPhaseRepository.findById(trackId);
        return optional.filter(trackPhase ->
                evaluatePapers(paperRepository.findAllById(trackPhase.getPapers()))).isPresent();
    }

    public boolean evaluatePaper(Paper paper) {
        // If a paper already has a final verdict don't evaluate it
        Paper.FinalVerdictEnum verdict = paper.getFinalVerdict();
        if (!(verdict == null || verdict.equals(Paper.FinalVerdictEnum.UNRESOLVED))) {
            return true;
        }

        List<Review> reviews = reviewRepository.findReviewsByPaperId(paper.getId());

        // If all reviews have a positive score immediately accept
        if (reviews.stream().allMatch(x -> x.getOverallScore().getValue() > 0)) {
            paper.finalVerdict(Paper.FinalVerdictEnum.ACCEPTED);

        // If all reviews have negative score immediately reject
        } else if (reviews.stream().allMatch(x -> x.getOverallScore().getValue() < 0)) {
            paper.finalVerdict(Paper.FinalVerdictEnum.REJECTED);

        // If reviews are mixed it remains unresolved. The pc chair wil have to manually resolve it.
        } else {
            paper.finalVerdict(Paper.FinalVerdictEnum.UNRESOLVED);
        }
        return paperRepository.save(paper).equals(paper);
    }

    public boolean evaluatePapers(List<Paper> papers) {
        boolean success = true;
        for (Paper p: papers) {
            success &= evaluatePaper(p);
        }
        return success;
    }

    public boolean evaluateAllPapers() {
        return evaluatePapers(paperRepository.findAll());
    }
}
