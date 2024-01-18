package nl.tudelft.sem.template.example.domain.services;

import nl.tudelft.sem.template.example.domain.models.PcChair;
import nl.tudelft.sem.template.example.domain.repositories.CommentRepository;
import nl.tudelft.sem.template.example.domain.repositories.PcChairRepository;
import nl.tudelft.sem.template.example.domain.repositories.ReviewRepository;
import nl.tudelft.sem.template.example.domain.util.DateUtils;
import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.Review;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final transient ReviewRepository reviewRepository;
    private final transient PcChairRepository pcChairRepository;
    private final transient CommentRepository commentRepository;
    private final RestTemplate restTemplate;
    private static int id;
    /**
     * Constructor for the Review Service.
     *
     * @param reviewRepository  the repositories where reviews are meant to be stored.
     * @param pcChairRepository the repository containing pcChairs
     */
    public ReviewService(ReviewRepository reviewRepository, PcChairRepository pcChairRepository,
                         CommentRepository commentRepository, RestTemplate restTemplate) {
        this.reviewRepository = reviewRepository;
        this.pcChairRepository = pcChairRepository;
        this.commentRepository = commentRepository;
        this.restTemplate = restTemplate;
        id = 1;
    }

    /**
     * Method that saves reviews to the database.
     *
     * @param reviews the list of reviews to be stored.
     */
    public void saveReviews(List<Review> reviews) {
        for (Review r: reviews)
            if (r.getId() == null)
                r.setId(id++);
        reviewRepository.saveAll(reviews);
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
     * @return an optional containing the deadline; if any.
     */
    public Optional<String> getTrackDeadline(int trackId) {
        String submissionsUri = "localhost:8082/" + trackId + "/deadline";
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
        return Optional.of(DateUtils.advanceOneWeek(submissionDeadline));
    }

    public List<Review> findAllReviewsByPaperId(int paperId) {
        return reviewRepository.findReviewsByPaperId(paperId);
    }
}

