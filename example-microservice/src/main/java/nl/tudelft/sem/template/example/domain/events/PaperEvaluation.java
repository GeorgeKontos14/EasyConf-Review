package nl.tudelft.sem.template.example.domain.events;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.example.domain.models.TrackPhase;
import nl.tudelft.sem.template.example.domain.repositories.PaperRepository;
import nl.tudelft.sem.template.example.domain.repositories.ReviewRepository;
import nl.tudelft.sem.template.example.domain.repositories.TrackPhaseRepository;
import nl.tudelft.sem.template.model.Paper;
import nl.tudelft.sem.template.model.Review;

public class PaperEvaluation {

    private final PaperRepository paperRepository;
    private final ReviewRepository reviewRepository;
    private final TrackPhaseRepository trackPhaseRepository;

    /**
     * Constructor for PaperEvaluation.
     *
     * @param paperRepository that will be queried
     * @param reviewRepository that will be queried
     * @param trackPhaseRepository that will be queried
     */
    public PaperEvaluation(PaperRepository paperRepository, ReviewRepository reviewRepository,
                           TrackPhaseRepository trackPhaseRepository) {
        this.paperRepository = paperRepository;
        this.reviewRepository = reviewRepository;
        this.trackPhaseRepository = trackPhaseRepository;
    }

    /**
     * Assigns a score to all the papers in the list of trackIds.
     *
     * @param trackIds of the papers to evaluate
     * @return boolean of success or failure
     */
    public boolean evaluatePapersWithTrackIds(List<Integer> trackIds) {
        boolean success = true;
        for (Integer n : trackIds) {
            success &= evaluatePapersWithTrackId(n);
        }
        return success;
    }

    /**
     * Evaluate all the papers on a single track.
     *
     * @param trackId of the papers to evaluate the scores for
     * @return boolean of success or failure
     */
    public boolean evaluatePapersWithTrackId(int trackId) {
        Optional<TrackPhase> optional = trackPhaseRepository.findById(trackId);
        return optional.filter(trackPhase ->
                evaluatePapers(paperRepository.findAllById(trackPhase.getPapers()))).isPresent();
    }

    /**
     * Evaluate the score of a given paper.
     *
     * @param paper to evaluate
     * @return boolean of success or failure
     */
    public boolean evaluatePaper(Paper paper) {
        // If a paper already has a final verdict don't evaluate it
        if (paper.getFinalVerdict() != null) {
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
            paper.finalVerdict(null);
        }
        return paperRepository.save(paper).equals(paper);
    }

    /**
     * Evaluate the scores of a list of papers.
     *
     * @param papers to evaluate
     * @return boolean of success or failure
     */
    public boolean evaluatePapers(List<Paper> papers) {
        boolean success = true;
        for (Paper p : papers) {
            success &= evaluatePaper(p);
        }
        return success;
    }

    /**
     * Evaluates the scores of all papers in de database.
     *
     * @return boolean of success or failure
     */
    public boolean evaluateAllPapers() {
        return evaluatePapers(paperRepository.findAll());
    }
}
