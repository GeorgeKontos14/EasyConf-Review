package nl.tudelft.sem.template.example.domain.phases;

import lombok.Getter;
/**
 * A DDD domain event that indicated the discussion phase has started.
 */
@Getter
public class DiscussionPhaseStarted {
    private final int trackId;

    public DiscussionPhaseStarted(int trackId) {
        this.trackId = trackId;
    }
}
