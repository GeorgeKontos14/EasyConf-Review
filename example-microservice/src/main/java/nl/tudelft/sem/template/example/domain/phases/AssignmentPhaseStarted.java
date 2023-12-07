package nl.tudelft.sem.template.example.domain.phases;

import lombok.Getter;

/**
 *  A DDD domain event that indicates the assignment phase has started.
 */

@Getter
public class AssignmentPhaseStarted {
    private final int trackId;

    public AssignmentPhaseStarted(int trackId) {
        this.trackId = trackId;
    }
}
