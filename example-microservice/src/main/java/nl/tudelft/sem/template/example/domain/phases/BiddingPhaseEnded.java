package nl.tudelft.sem.template.example.domain.phases;

import lombok.Getter;
/**
 * A DDD domain event that indicated the bidding phase has ended.
 */
@Getter
public class BiddingPhaseEnded {
    private final int trackId;

    public BiddingPhaseEnded(int trackId) {
        this.trackId = trackId;
    }
}
