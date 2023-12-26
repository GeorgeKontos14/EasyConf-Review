package nl.tudelft.sem.template.example.domain.phases;

import lombok.Getter;
/**
 * A DDD domain event that indicates the assignment phase has ended.
 */

@Getter
public class AssignmentPhaseEnded {
    private final int trackId;

    public AssignmentPhaseEnded(int trackId) {
        this.trackId = trackId;
    }
}
