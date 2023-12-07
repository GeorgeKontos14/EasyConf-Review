package nl.tudelft.sem.template.example.domain.phases;

import lombok.Getter;
/**
 * A DDD domain event that indicated the discussion phase has ended.
 */
@Getter
public class DiscussionPhaseEnded {
    private final int trackId;

    public DiscussionPhaseEnded(int trackId) {
        this.trackId = trackId;
    }
}
