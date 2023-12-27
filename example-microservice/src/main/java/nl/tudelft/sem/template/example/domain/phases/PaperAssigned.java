package nl.tudelft.sem.template.example.domain.phases;

import lombok.Getter;
/**
 * A DDD domain event that indicates that a specific paper was assigned to a specific reviewer.
 */

@Getter
public class PaperAssigned {
    private final int paperId, reviewerId;
    public PaperAssigned(int paperId, int reviewerId) {
        this.paperId = paperId;
        this.reviewerId = reviewerId;
    }
}
