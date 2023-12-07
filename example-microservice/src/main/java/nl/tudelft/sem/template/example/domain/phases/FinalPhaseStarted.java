package nl.tudelft.sem.template.example.domain.phases;

import lombok.Getter;
/**
 * A DDD domain event that indicated the final phase has started.
 */

@Getter
public class FinalPhaseStarted {
    private final int trackId;

    public FinalPhaseStarted(int trackId) {
        this.trackId = trackId;
    }
}
