package nl.tudelft.sem.template.example.domain.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.tudelft.sem.template.model.Paper;
import nl.tudelft.sem.template.model.Review;
import nl.tudelft.sem.template.model.ReviewerPreferences;

public class ReviewUtils {

    /**
     * Method that assigns each paper automatically to three reviewers and saves the reviews.
     *
     * @param papers      the list of papers to be assigned.
     * @param conflicts   a map that for each reviewer id, contains all the ids
     *                    of the authors they have conflicts of interest with.
     * @param preferences the preferences of each reviewer on each paper.
     * @return a list of all reviews; each review has only a reviewer id and
     * a paper id.
     */
    public static List<Review> assignReviewsAutomatically(List<Paper> papers, Map<Integer, List<Integer>> conflicts,
                                                          List<ReviewerPreferences> preferences) {
        Map<List<Integer>, ReviewerPreferences.ReviewerPreferenceEnum> preferencesMap = new HashMap<>();
        for (ReviewerPreferences pref : preferences) {
            preferencesMap.put(Arrays.asList(pref.getReviewerId(), pref.getPaperId()), pref.getReviewerPreference());
        }
        List<Review> reviews = new ArrayList<>();
        for (Paper paper : papers) {
            reviews.addAll(assignReviewersToPaper(paper, conflicts, preferencesMap));
        }
        return reviews;
    }

    /**
     * This method takes a paper and the conflicts and preferences of all the reviewers in the database and assigns
     * reviewers to the paper, while making sure there are few conflicts and while the preferences of the reviewers
     * are kept in mind.
     *
     * @param paper          to assign the reviewers to
     * @param conflicts      of interest of the reviewers
     * @param preferencesMap with all the preferences of the reviewers
     * @return a list with all the reviews that have been created for the paper
     */
    private static List<Review> assignReviewersToPaper(Paper paper, Map<Integer, List<Integer>> conflicts, Map<List<Integer>,
            ReviewerPreferences.ReviewerPreferenceEnum> preferencesMap) {
        int count = 0;
        List<Review> reviews = new ArrayList<>(3);
        List<Integer> cannot = new ArrayList<>();
        List<Integer> withConflict = new ArrayList<>();
        for (int reviewer : conflicts.keySet()) {
            if (!intersect(paper.getAuthors(), conflicts.get(reviewer))) {
                if (preferencesMap.get(Arrays.asList(reviewer, paper.getId()))
                        != ReviewerPreferences.ReviewerPreferenceEnum.CANNOT_REVIEW) {
                    count++;
                    reviews.add(createReview(paper.getId(), reviewer));
                } else {
                    cannot.add(reviewer);
                }
            } else {
                withConflict.add(reviewer);
            }
            if (count == 3) {
                break;
            }
        }
        reviews.addAll(assignWithoutPreference(paper, count, cannot, withConflict));
        return reviews;
    }

    /**
     * This method assigns papers without keeping in mind the preferences or COI's of the reviewer, in case the previous
     * algorithm fails.
     *
     * @param paper        to assign reviews for
     * @param count        the count of reviews that have already been assigned
     * @param cannot       a list of reviewers who do not want to review this paper
     * @param withConflict a list of reviewers who have a conflict of interest with the papers author
     * @return a list of reviews that have been created for the given paper
     */
    private static List<Review> assignWithoutPreference(Paper paper, int count, List<Integer> cannot,
                                                        List<Integer> withConflict) {
        List<Review> reviews = new ArrayList<>(3 - count);
        int i = 0;
        while (count < 3 && i < cannot.size()) {
            count++;
            reviews.add(createReview(paper.getId(), cannot.get(i)));
            i++;
        }
        i = 0;
        while (count < 3 && i < withConflict.size()) {
            count++;
            reviews.add(createReview(paper.getId(), withConflict.get(i)));
            i++;
        }
        return reviews;
    }

    /**
     * Method that creates a new review.
     *
     * @param paperId    the ID of the paper.
     * @param reviewerId the ID of the reviewer.
     * @return the final review object.
     */
    public static Review createReview(int paperId, int reviewerId) {
        Review review = new Review();
        review.reviewerId(reviewerId);
        review.paperId(paperId);
        return review;
    }

    /**
     * Method that checks if two lists have common elements.
     *
     * @param a one list.
     * @param b the other list.
     * @return true if-f the two lists have some element in common.
     */
    public static boolean intersect(List<Integer> a, List<Integer> b) {
        for (int i : a) {
            if (b.contains(i)) {
                return true;
            }
        }
        return false;
    }

}
