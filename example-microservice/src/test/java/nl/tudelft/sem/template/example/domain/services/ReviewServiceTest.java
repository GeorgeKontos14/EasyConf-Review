package nl.tudelft.sem.template.example.domain.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import nl.tudelft.sem.template.example.domain.models.PcChair;
import nl.tudelft.sem.template.example.domain.repositories.CommentRepository;
import nl.tudelft.sem.template.example.domain.repositories.PcChairRepository;
import nl.tudelft.sem.template.example.domain.repositories.ReviewRepository;
import nl.tudelft.sem.template.example.domain.repositories.ReviewerRepository;
import nl.tudelft.sem.template.example.domain.util.ReviewUtils;
import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.Paper;
import nl.tudelft.sem.template.model.Review;
import nl.tudelft.sem.template.model.ReviewerPreferences;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;



public class ReviewServiceTest {
    private RestTemplate restTemplate;
    private ReviewService sut;
    private ReviewRepository repo;
    private PcChairRepository pcChairRepository;
    private ReviewerRepository reviewerRepository;
    private CommentRepository commentRepository;
    private List<Paper> papers;
    private List<ReviewerPreferences> prefs;
    private Map<Integer, List<Integer>> conflicts;

    /**
     * Setup for the tests.
     */
    @BeforeEach
    public void setup() {
        restTemplate = Mockito.mock(RestTemplate.class);
        repo = Mockito.mock(ReviewRepository.class);
        pcChairRepository = Mockito.mock(PcChairRepository.class);
        reviewerRepository = Mockito.mock(ReviewerRepository.class);
        commentRepository = Mockito.mock(CommentRepository.class);
        sut = new ReviewService(repo, pcChairRepository, commentRepository, restTemplate);
        Paper p1 = new Paper();
        p1.setId(1);
        p1.setAuthors(Arrays.asList(1, 2, 3, 4));
        Paper p2 = new Paper();
        p2.setId(2);
        p2.setAuthors(Arrays.asList(4, 5, 6));
        Paper p3 = new Paper();
        p3.setId(3);
        p3.setAuthors(Arrays.asList(7, 8, 9, 10));
        Paper p4 = new Paper();
        p4.setId(4);
        p4.setAuthors(Arrays.asList(11, 12));
        Paper p5 = new Paper();
        p5.setId(5);
        p5.setAuthors(Arrays.asList(13, 14, 5, 15));
        papers = Arrays.asList(p1, p2, p3, p4, p5);
        prefs = new ArrayList<>();
        conflicts = new HashMap<>();
    }

    /**
     * Test for the verifyPcChair method.
     */
    @Test
    public void verifyPcChairTest() {
        PcChair chair = new PcChair(Arrays.asList(1, 2, 3));
        chair.setId(1);
        Mockito.when(pcChairRepository.findById(1)).thenReturn(Optional.of(chair));
        Mockito.when(pcChairRepository.findById(2)).thenReturn(Optional.empty());
        assertThat(sut.verifyPcChair(1, 1)).isTrue();
        assertThat(sut.verifyPcChair(1, 4)).isFalse();
        assertThat(sut.verifyPcChair(2, 3)).isFalse();
    }

    @Test
    public void postCommentTest() {
        Comment c = new Comment();
        assertThat(sut.reviewPostCommentPost(c)).isEqualTo(c);
    }

    @Test
    public void findAllPapersByReviewerIdTest() {
        Review r = new Review();
        r.id(7);
        r.reviewerId(5);
        r.paperId(1);
        Mockito.when(repo.findReviewByReviewerId(5))
                .thenReturn(List.of(r));
        assertThat(sut.findAllPapersByReviewerId(5)).isEqualTo(List.of(1));
    }

    /**
     * Test for the functionality of the getTrackDeadline method.
     */
    @Test
    public void getTrackDeadlineOkTest() {
        Mockito.when(restTemplate
                        .exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.of(Optional.of("2024-10-10")));
        Optional<String> result = sut.getTrackDeadline(2);
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo("2024-10-17");
    }

    /**
     * Test that makes the getTrackDeadline method throw an exception.
     */
    @Test
    public void getTrackDeadlineExceptionTest() {
        Mockito.when(restTemplate
                        .exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class)))
                .thenThrow(IllegalArgumentException.class);
        Optional<String> result = sut.getTrackDeadline(2);
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    public void findAllReviewsByPaperId() {
        Mockito.when(repo.findReviewsByPaperId(1))
                .thenReturn(List.of(new Review()));
        assertThat(sut.findAllReviewsByPaperId(1)).isEqualTo(List.of(new Review()));
    }

    @Test
    public void reviewsByReviewerTest() {
        Review r1 = ReviewUtils.createReview(1, 1);
        Review r2 = ReviewUtils.createReview(2, 2);
        Mockito.when(repo.findReviewByReviewerId(1)).thenReturn(Arrays.asList(r1, r2));
        assertThat(sut.reviewsByReviewer(1)).isEqualTo(Arrays.asList(r1, r2));
    }

    @Test
    public void reviewsByPaperTest() {
        Review r1 = ReviewUtils.createReview(1, 1);
        Review r2 = ReviewUtils.createReview(2, 2);
        Mockito.when(repo.findReviewsByPaperId(1)).thenReturn(Arrays.asList(r1, r2));
        assertThat(sut.reviewsByPaper(1)).isEqualTo(Arrays.asList(r1, r2));
    }

    @Test
    public void findReviewObjectTest() {
        Review r = ReviewUtils.createReview(1, 1);
        Mockito.when(repo.findById(1)).thenReturn(Optional.of(r));
        assertThat(sut.findReviewObjectWithId(1)).isEqualTo(Optional.of(r));
    }

    @Test
    public void existsReviewTest() {
        Mockito.when(repo.existsById(1)).thenReturn(true);
        Mockito.when(repo.existsById(2)).thenReturn(false);
        assertThat(sut.existsReview(1)).isTrue();
        assertThat(sut.existsReview(2)).isFalse();
    }

    @Test
    public void saveAndReturnReviewTest() {
        Review r = ReviewUtils.createReview(1, 1);
        Mockito.when(repo.save(r)).thenReturn(r);
        assertThat(sut.saveAndReturnReview(r)).isEqualTo(r);
        Mockito.verify(repo).save(r);
    }

}
