package nl.tudelft.sem.template.example.services;

import nl.tudelft.sem.template.example.repositories.ReviewRepository;
import nl.tudelft.sem.template.model.Paper;
import nl.tudelft.sem.template.model.Review;
import java.util.*;
import nl.tudelft.sem.template.model.ReviewerPreferences;

public class ReviewService {

    private final ReviewRepository reviewRepository;

    /**
     *  Constructor for the Review Service
     * @param reviewRepository the repositories where reviews are meant to be stored.
     */
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    /**
     * Method that assigns each paper automatically
     *                  to three reviewers and saves the reviews.
     * @param papers the list of papers to be assigned.
     * @param conflicts a map that for each reviewer id, contains all the ids
     *                  of the authors they have conflicts of interest with.
     * @param preferences the preferences of each reviewer on each paper.
     * @return a list of all reviews; each review has only a reviewer id and
     *                  a paper id.
     */
    public List<Review> assignReviewsAutomatically(List<Paper> papers, Map<Integer, List<Integer>> conflicts, List<ReviewerPreferences> preferences) {
        Map<List<Integer>, ReviewerPreferences.ReviewerPreferenceEnum> preferencesMap= new HashMap<>();
        for (ReviewerPreferences pref : preferences) {
            preferencesMap.put(Arrays.asList(pref.getReviewerId(), pref.getPaperId()), pref.getReviewerPreference());
        }
        List<Review> reviews = new ArrayList<>();
        int count;
        for (Paper paper: papers) {
            count = 0;
            List<Integer> cannot = new ArrayList<>();
            List<Integer> withConflict = new ArrayList<>();
            for (int reviewer: conflicts.keySet()) {
                if (!intersect(paper.getAuthors(), conflicts.get(reviewer))) {
                    if (preferencesMap.get(Arrays.asList(reviewer, paper.getId())) !=
                    ReviewerPreferences.ReviewerPreferenceEnum.CANNOT_REVIEW) {
                        count++;
                        Review review = new Review();
                        review.reviewerId(reviewer);
                        review.paperId(paper.getId());
                        reviews.add(review);
                    } else {
                        cannot.add(reviewer);
                    }
                } else {
                    withConflict.add(reviewer);
                }
                if (count == 3)
                    break;
            }
            int i = 0;
            while (count < 3 && i < cannot.size()) {
                count++;
                int reviewer = cannot.get(i);
                Review review = new Review();
                review.reviewerId(reviewer);
                review.paperId(paper.getId());
                reviews.add(review);
                i++;
            }
            i = 0;
            while (count < 3 && i < withConflict.size()) {
                count++;
                int reviewer = withConflict.get(i);
                Review review = new Review();
                review.reviewerId(reviewer);
                review.paperId(paper.getId());
                reviews.add(review);
                i++;
            }
        }
        return reviews;
    }

    /**
     * Method that saves reviews to the database.
     * @param reviews the list of reviews to be stored.
     */
    public void saveReviews(List<Review> reviews) {
        reviewRepository.saveAll(reviews);
    }

    /**
     * Method that checks if two lists have common elements.
     * @param a one list.
     * @param b the other list.
     * @return true if-f the two lists have some element in common.
     */
    private boolean intersect(List<Integer> a, List<Integer> b) {
        for (int i: a)
            if (b.contains(i))
                return true;
        for (int j: b)
            if (a.contains(j))
                return true;
        return false;
    }

}
