package nl.tudelft.sem.template.example.domain.phases;

/**
 * A DDD domain event that indicated that a review has been published.
 */
public class ReviewFinished {
    private final int reviewId;
    public ReviewFinished(int reviewId)
    {
        this.reviewId = reviewId;
    }
}
