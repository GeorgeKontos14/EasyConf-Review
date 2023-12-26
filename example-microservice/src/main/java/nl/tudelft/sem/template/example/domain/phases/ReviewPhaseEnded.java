package nl.tudelft.sem.template.example.domain.phases;

import lombok.Getter;
/**
 * A DDD domain event that indicated the review phase has ended.
 */

@Getter
public class ReviewPhaseEnded {
    private final int trackId;

    public ReviewPhaseEnded(int trackId) {
        this.trackId = trackId;
    }
}
