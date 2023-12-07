package nl.tudelft.sem.template.example.domain.phases;

import lombok.Getter;
/**
 * A DDD domain event that indicated the final phase has ended.
 */

@Getter
public class FinalPhaseEnded {
    private final int trackId;

    public FinalPhaseEnded(int trackId) {
        this.trackId = trackId;
    }
}
