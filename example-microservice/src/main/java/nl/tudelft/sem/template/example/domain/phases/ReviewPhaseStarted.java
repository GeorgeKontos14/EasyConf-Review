package nl.tudelft.sem.template.example.domain.phases;

import lombok.Getter;
/**
 * A DDD domain event that indicated the review phase has started.
 */

@Getter
public class ReviewPhaseStarted {
    private final int trackId;

    public ReviewPhaseStarted(int trackId) {
        this.trackId = trackId;
    }
}
