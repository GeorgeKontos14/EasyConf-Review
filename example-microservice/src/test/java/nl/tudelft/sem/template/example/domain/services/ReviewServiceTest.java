package nl.tudelft.sem.template.example.domain.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;

import nl.tudelft.sem.template.example.domain.models.PcChair;
import nl.tudelft.sem.template.example.domain.repositories.CommentRepository;
import nl.tudelft.sem.template.example.domain.repositories.PcChairRepository;
import nl.tudelft.sem.template.example.domain.repositories.ReviewRepository;
import nl.tudelft.sem.template.example.domain.repositories.ReviewerRepository;
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

import java.util.*;

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
        sut = new ReviewService(repo, pcChairRepository, reviewerRepository, commentRepository);
        Paper p1 = new Paper();
        p1.setId(1);
        p1.setAuthors(Arrays.asList(1,2,3,4));
        Paper p2 = new Paper();
        p2.setId(2);
        p2.setAuthors(Arrays.asList(4,5,6));
        Paper p3 = new Paper();
        p3.setId(3);
        p3.setAuthors(Arrays.asList(7,8,9,10));
        Paper p4 = new Paper();
        p4.setId(4);
        p4.setAuthors(Arrays.asList(11,12));
        Paper p5 = new Paper();
        p5.setId(5);
        p5.setAuthors(Arrays.asList(13,14,5,15));
        papers = Arrays.asList(p1,p2,p3,p4,p5);
        prefs = new ArrayList<>();
        conflicts = new HashMap<>();
    }

    /**
     * Tests if the intersect method returns
     * false when the lists have no common elements.
     */
    @Test
    public void intersectNoneTest() {
        List<Integer> a = Arrays.asList(1,2,3,4,5,6);
        List<Integer> b = Arrays.asList(7,8,9,33,55,66);
        assertThat(sut.intersect(a,b)).isFalse();
    }

    /**
     * Tests if the intersect method returns
     * true when the lists have one common element.
     */
    @Test
    public void intersectSingleElementTest() {
        List<Integer> a = Arrays.asList(1,2,3,4,5);
        List<Integer> b = Arrays.asList(5,6,7,82);
        assertThat(sut.intersect(a, b)).isTrue();
    }

    /**
     * Tests if the intersect method returns
     * true when the lists have multiple common elements.
     */
    @Test
    public void intersectMultipleElementsTest() {
        List<Integer> a = Arrays.asList(1,2,3,4,5,6,82);
        List<Integer> b = Arrays.asList(5,6,7,82);
        assertThat(sut.intersect(a, b)).isTrue();
    }

    /**
     * Checks if all papers are assigned to three reviews
     * in the case where no paper has too many conflicts
     * or too many cannot review
     */
    @Test
    public void directAssignmentTest() {
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 10; j++) {
                ReviewerPreferences pr = new ReviewerPreferences();
                pr.setReviewerId(j);
                pr.setPaperId(i);
                if (i==4)
                    pr.setReviewerPreference(ReviewerPreferences
                            .ReviewerPreferenceEnum.NEUTRAL);
                else
                    pr.setReviewerPreference(ReviewerPreferences
                            .ReviewerPreferenceEnum.CAN_REVIEW);
                prefs.add(pr);
            }
        }
        for (int j = 1; j <= 10; j++)
            conflicts.put(j, Collections.emptyList());
        List<Review> assignments = sut.assignReviewsAutomatically(papers, conflicts, prefs);
        assertThat(assignments.size()).isEqualTo(15);
        sut.saveReviews(assignments);
        Mockito.verify(repo).saveAll(assignments);
    }

    /**
     * Checks if all papers are assigned to three reviews
     * in the case that a paper has too many cannot review.
     */
    @Test
    public void paperWithOnlyCannotReviewTest() {
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 10; j++) {
                ReviewerPreferences pr = new ReviewerPreferences();
                pr.setReviewerId(j);
                pr.setPaperId(i);
                if (i==4)
                    pr.setReviewerPreference(ReviewerPreferences
                            .ReviewerPreferenceEnum.CANNOT_REVIEW);
                else
                    pr.setReviewerPreference(ReviewerPreferences
                            .ReviewerPreferenceEnum.CAN_REVIEW);
                prefs.add(pr);
            }
        }
        for (int j = 1; j <= 10; j++)
            conflicts.put(j, Collections.emptyList());
        List<Review> assignments = sut.assignReviewsAutomatically(papers, conflicts, prefs);
        assertThat(assignments.size()).isEqualTo(15);
        sut.saveReviews(assignments);
    }

    /**
     * Checks if all papers are assigned to three reviews
     * in the case that a paper has too many conflicts.
     */
    @Test
    public void paperWithConflictsTest() {
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 10; j++) {
                ReviewerPreferences pr = new ReviewerPreferences();
                pr.setReviewerId(j);
                pr.setPaperId(i);
                if (i==4)
                    pr.setReviewerPreference(ReviewerPreferences
                            .ReviewerPreferenceEnum.CANNOT_REVIEW);
                else
                    pr.setReviewerPreference(ReviewerPreferences
                            .ReviewerPreferenceEnum.CAN_REVIEW);
                prefs.add(pr);
            }
        }
        for (int j = 1; j <= 10; j++)
            conflicts.put(j, Collections.singletonList(6));
        List<Review> assignments = sut.assignReviewsAutomatically(papers, conflicts, prefs);
        assertThat(assignments.size()).isEqualTo(15);
        sut.saveReviews(assignments);
    }

    /**
     * Checks if all papers are assigned to three reviews
     * in the case that a paper has both too many conflicts
     * and too many cannot review.
     */
    @Test
    public void paperWithBothProblemsTest() {
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 10; j++) {
                ReviewerPreferences pr = new ReviewerPreferences();
                pr.setReviewerId(j);
                pr.setPaperId(i);
                if (i==4)
                    pr.setReviewerPreference(ReviewerPreferences
                            .ReviewerPreferenceEnum.CANNOT_REVIEW);
                else
                    pr.setReviewerPreference(ReviewerPreferences
                            .ReviewerPreferenceEnum.CAN_REVIEW);
                prefs.add(pr);
            }
        }
        for (int j = 1; j <= 10; j++)
            conflicts.put(j, Collections.singletonList(11));
        List<Review> assignments = sut.assignReviewsAutomatically(papers, conflicts, prefs);
        assertThat(assignments.size()).isEqualTo(15);
        sut.saveReviews(assignments);
    }

    /**
     * Test for the verifyPcChair method.
     */
    @Test
    public void verifyPcChairTest() {
        PcChair chair = new PcChair(Arrays.asList(1,2,3));
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
        Optional<String> result = sut.getTrackDeadline(2, restTemplate);
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
        Optional<String> result = sut.getTrackDeadline(2, restTemplate);
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    public void advanceOneWeekTest() {
        assertThat(sut.advanceOneWeek("2024-12-31")).isEqualTo("2025-01-07");
    }

}
