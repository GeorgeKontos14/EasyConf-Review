package nl.tudelft.sem.template.example.domain.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import nl.tudelft.sem.template.example.domain.models.PcChair;
import nl.tudelft.sem.template.example.domain.repositories.CommentRepository;
import nl.tudelft.sem.template.example.domain.repositories.PcChairRepository;
import nl.tudelft.sem.template.example.domain.repositories.ReviewRepository;
import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.Paper;
import nl.tudelft.sem.template.model.Review;
import nl.tudelft.sem.template.model.ReviewerPreferences;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ReviewService {

    private final transient ReviewRepository reviewRepository;
    private final transient PcChairRepository pcChairRepository;
    private final transient CommentRepository commentRepository;

    /**
     * Constructor for the Review Service.
     *
     * @param reviewRepository  the repositories where reviews are meant to be stored.
     * @param pcChairRepository the repository containing pcChairs
     */
    public ReviewService(ReviewRepository reviewRepository, PcChairRepository pcChairRepository,
                         CommentRepository commentRepository) {
        this.reviewRepository = reviewRepository;
        this.pcChairRepository = pcChairRepository;
        this.commentRepository = commentRepository;
    }

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
    public List<Review> assignReviewsAutomatically(List<Paper> papers, Map<Integer, List<Integer>> conflicts,
                                                   List<ReviewerPreferences> preferences) {
        Map<List<Integer>, ReviewerPreferences.ReviewerPreferenceEnum> preferencesMap = new HashMap<>();
        for (ReviewerPreferences pref : preferences) {
            preferencesMap.put(Arrays.asList(pref.getReviewerId(), pref.getPaperId()), pref.getReviewerPreference());
        }
        List<Review> reviews = new ArrayList<>();
        int count;
        for (Paper paper : papers) {
            count = 0;
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
    Review createReview(int paperId, int reviewerId) {
        Review review = new Review();
        review.reviewerId(reviewerId);
        review.paperId(paperId);
        return review;
    }


    /**
     * Method that saves reviews to the database.
     *
     * @param reviews the list of reviews to be stored.
     */
    public void saveReviews(List<Review> reviews) {
        reviewRepository.saveAll(reviews);
    }

    /**
     * Method that checks if two lists have common elements.
     *
     * @param a one list.
     * @param b the other list.
     * @return true if-f the two lists have some element in common.
     */
    public boolean intersect(List<Integer> a, List<Integer> b) {
        for (int i : a) {
            if (b.contains(i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method that retrieves all reviews assigned to a specific reviewer.
     *
     * @param reviewerId the ID of the reviewer
     * @return the List of reviews assigned to the reviewer in question
     */
    public List<Review> reviewsByReviewer(int reviewerId) {
        return reviewRepository.findReviewByReviewerId(reviewerId);
    }

    /**
     * Method that retrieves all reviews for a specific paper.
     *
     * @param paperId the ID of the paper
     * @return the list of reviews for the paper in question.
     */
    public List<Review> reviewsByPaper(int paperId) {
        return reviewRepository.findReviewsByPaperId(paperId);
    }

    /**
     * Method that retrieves a pc chair from the repository and
     * checks whether they have access to a given track.
     *
     * @param userId  the ID of the user(PC Chair)
     * @param trackId the ID of the track
     * @return true if-f the user is responsible for the track in question.
     */
    public boolean verifyPcChair(int userId, int trackId) {
        Optional<PcChair> chair = pcChairRepository.findById(userId);
        return chair.map(pcChair -> pcChair.hasAccess(trackId)).orElse(false);
    }

    /**
     * Method that retrieves a review with a given ID, if one exists.
     *
     * @param reviewId the ID in question.
     * @return an optional object, containing the review if one exists; empty otherwise.
     */
    public Optional<Review> findReviewObjectWithId(int reviewId) {
        return reviewRepository.findById(reviewId);
    }


    public boolean existsReview(int reviewId) {
        return reviewRepository.existsById(reviewId);
    }

    public Review saveAndReturnReview(Review review) {
        return reviewRepository.save(review);
    }

    public Comment reviewPostCommentPost(Comment comment) {
        commentRepository.save(comment);
        return comment;
    }

    /**
     * Finds all the papers for a given reviewerId.
     *
     * @param reviewerId to find the papers for
     * @return a list of all the ids of the found papers
     */
    public List<Integer> findAllPapersByReviewerId(int reviewerId) {
        return reviewRepository
                .findReviewByReviewerId(reviewerId)
                .stream()
                .map(Review::getPaperId)
                .collect(Collectors.toList());
    }

    /**
     * Method that gets the track deadline from the users' microservice.
     *
     * @param trackId      the ID of the track.
     * @param restTemplate the rest template.
     * @return an optional containing the deadline; if any.
     */
    public Optional<String> getTrackDeadline(int trackId, RestTemplate restTemplate) {
        String submissionsUri = "localhost:8082" + trackId + "/deadline";
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(submissionsUri, HttpMethod.GET, entity, String.class);
        } catch (Exception e) {
            return Optional.empty();
        }
        String submissionDeadline = response.getBody();
        return Optional.of(advanceOneWeek(submissionDeadline));
    }

    /**
     * Method that advances the date by one week.
     *
     * @param currentDate the current date.
     * @return the new date
     */
    public String advanceOneWeek(String currentDate) {
        LocalDate currentDateObject = LocalDate.parse(currentDate);
        LocalDate advanced = currentDateObject.plusWeeks(1);
        return advanced.toString();
    }

    public List<Review> findAllReviewsByPaperId(int paperId) {
        return reviewRepository.findReviewsByPaperId(paperId);
    }
}

