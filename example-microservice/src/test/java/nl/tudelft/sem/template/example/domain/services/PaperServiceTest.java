package nl.tudelft.sem.template.example.domain.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import java.util.*;

import nl.tudelft.sem.template.example.domain.models.Reviewer;
import nl.tudelft.sem.template.example.domain.repositories.CommentRepository;
import nl.tudelft.sem.template.example.domain.repositories.PaperRepository;
import nl.tudelft.sem.template.example.domain.repositories.ReviewerRepository;
import nl.tudelft.sem.template.example.domain.responses.PaperResponse;
import nl.tudelft.sem.template.example.domain.responses.SubmissionPaperIdsResponse;
import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.Paper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;


class PaperServiceTest {

    private RestTemplate restTemplate;
    private UserService userService;
    private ReviewService reviewService;
    private PaperRepository paperRepository;
    private CommentRepository commentRepository;
    private ReviewerRepository reviewerRepository;
    private PaperService paperService;
    private Paper goodPaper;

    private Paper buildPaper(int id, List<Integer> authors, Paper.FinalVerdictEnum finalVerdictEnum) {
        Paper paper = new Paper();
        paper.setId(id);
        paper.setAuthors(authors);
        paper.setFinalVerdict(finalVerdictEnum);
        return paper;
    }

    @BeforeEach
    void setup() {
        goodPaper = buildPaper(3, List.of(1, 2, 4, 5, 6), null);
        paperRepository = Mockito.mock(PaperRepository.class);
        restTemplate = Mockito.mock(RestTemplate.class);
        commentRepository = Mockito.mock(CommentRepository.class);
        reviewService = Mockito.mock(ReviewService.class);
        userService = Mockito.mock(UserService.class);
        reviewerRepository = Mockito.mock(ReviewerRepository.class);
        paperService = new PaperService(userService, reviewService, paperRepository,
                commentRepository, reviewerRepository, restTemplate );
    }

    @Test
    void getPaperObjectWithIdExistentTest() {
        Mockito.when(paperRepository.findById(3)).thenReturn(Optional.of(goodPaper));
        Optional<Paper> result = paperService.getPaperObjectWithId(3);
        assertThat(result).isEqualTo(Optional.of(goodPaper));
    }

    @Test
    void getPaperObjectWithIdNonExistentTest() {
        Mockito.when(paperRepository.findById(5)).thenReturn(Optional.empty());
        Optional<Paper> result = paperService.getPaperObjectWithId(5);
        assertThat(result).isEqualTo(Optional.empty());
    }

    @Test
    void getPaperObjectFromSubmissions() {
        PaperResponse paperResponse = new PaperResponse("hello", List.of(1, 2, 3), 4, "abstr", List.of("key1", "key2"),
                "link1", List.of(1, 2, 3), "link2");

        ResponseEntity<PaperResponse> result = ResponseEntity.of(Optional.of(paperResponse));
        Mockito.when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
                eq(PaperResponse.class))).thenReturn(result);
        Optional<PaperResponse> response = paperService.getPaperObjectFromSubmissions(3);
        assertThat(response).isEqualTo(Optional.of(paperResponse));
    }

    @Test
    public void getPaperObjectFromSubmissionsExceptionTest() {
        RuntimeException e = Mockito.mock(RuntimeException.class);
        Mockito.when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
                eq(PaperResponse.class))).thenThrow(e);
        Optional<PaperResponse> res = paperService.getPaperObjectFromSubmissions(1);
        assertThat(res.isEmpty()).isTrue();
        Mockito.verify(e).printStackTrace();
    }

    @Test
    void paperGetPaperCommentsGet() {
        Comment c = new Comment();
        c.id(3);
        c.paperId(1);
        Paper p = new Paper();
        p.id(2);
        Mockito.when(commentRepository.findCommentByPaperId(2)).thenReturn(List.of(c));
        Mockito.when(paperRepository.findById(1)).thenReturn(Optional.empty());
        Mockito.when(paperRepository.findById(2)).thenReturn(Optional.of(p));
        assertThat(paperService.paperGetPaperCommentsGet(1))
                .isEqualTo(new ArrayList<>());
        assertThat(paperService.paperGetPaperCommentsGet(2))
                .isEqualTo(List.of(c));
    }

    @Test
    void isExistingPaperTest() {
        Mockito.when(paperRepository.existsById(1)).thenReturn(false);
        assertThat(paperService.isExistingPaper(1)).isEqualTo(false);
        Mockito.when(paperRepository.existsById(2)).thenReturn(true);
        assertThat(paperService.isExistingPaper(2)).isEqualTo(true);
    }

    @Test
    void paperUpdatePaperStatusPutTest() {
        Mockito.when(paperRepository.findById(1)).thenReturn(Optional.of(new Paper()));
        assertThat(paperService.paperUpdatePaperStatusPut(1, "Accepted"))
                .isEqualTo(true);
    }

    @Test
    void paperUpdatePaperStatusPutFailTest() {
        Mockito.when(paperRepository.findById(1)).thenReturn(Optional.empty());
        assertThat(paperService.paperUpdatePaperStatusPut(1, null))
                .isEqualTo(false);
    }

    @Test
    void paperGetAllPapersForIdGetTest() {
        Paper p = new Paper();
        p.id(1);
        Mockito.when(paperRepository.findAllById(List.of(1)))
                .thenReturn(List.of(p));
        assertThat(paperService.findAllPapersForIdList(List.of(1))).isEqualTo(List.of(p));
    }

    @Test
    void getFinalDecisionsOfPapersForReviewerInvalidUser() {
        Mockito.when(userService.validateUser(3)).thenReturn(false);
        assertThat(paperService.getFinalDecisionsOfPapersForReviewer(3)).isEqualTo(List.of());

    }

    @Test
    void getFinalDecisionsEmptyList() {
        Mockito.when(userService.validateUser(3)).thenReturn(true);
        Mockito.when(reviewService.findAllPapersByReviewerId(3)).thenReturn(List.of());
        assertThat(paperService.getFinalDecisionsOfPapersForReviewer(3)).isEqualTo(List.of());
    }

    @Test
    void getFinalDecisionsOnePaper() {
        Paper goodPaper1 = buildPaper(1, List.of(1), Paper.FinalVerdictEnum.ACCEPTED);
        Mockito.when(userService.validateUser(3)).thenReturn(true);
        Mockito.when(reviewService.findAllPapersByReviewerId(3)).thenReturn(List.of(1));
        Mockito.when(paperRepository.findById(1)).thenReturn(Optional.of(goodPaper1));
        List<Paper> ans = paperService.getFinalDecisionsOfPapersForReviewer(3);
        assertThat(ans.size()).isEqualTo(1);
        Paper ansPaper1 = buildPaper(1, null, Paper.FinalVerdictEnum.ACCEPTED);
        assertThat(ans.get(0)).isEqualTo(ansPaper1);
    }

    @Test
    void getFinalDecisionTwoPapers() {
        Paper goodPaper1 = buildPaper(1, List.of(1), Paper.FinalVerdictEnum.ACCEPTED);
        Paper goodPaper2 = buildPaper(2, List.of(1), Paper.FinalVerdictEnum.REJECTED);
        Mockito.when(userService.validateUser(3)).thenReturn(true);
        Mockito.when(reviewService.findAllPapersByReviewerId(3)).thenReturn(List.of(1, 2));
        Mockito.when(paperRepository.findById(1)).thenReturn(Optional.of(goodPaper1));
        Mockito.when(paperRepository.findById(2)).thenReturn(Optional.of(goodPaper2));
        List<Paper> ans = paperService.getFinalDecisionsOfPapersForReviewer(3);
        assertThat(ans.size()).isEqualTo(2);
        Paper ansPaper1 = buildPaper(1, null, Paper.FinalVerdictEnum.ACCEPTED);
        Paper ansPaper2 = buildPaper(2, null, Paper.FinalVerdictEnum.REJECTED);
        assertThat(ans.get(0)).isEqualTo(ansPaper1);
        assertThat(ans.get(1)).isEqualTo(ansPaper2);
    }

    @Test
    void onePaperNotFound() {
        Paper goodPaper1 = buildPaper(1, List.of(1), Paper.FinalVerdictEnum.ACCEPTED);
        Mockito.when(userService.validateUser(3)).thenReturn(true);
        Mockito.when(reviewService.findAllPapersByReviewerId(3)).thenReturn(List.of(1, 2));
        Mockito.when(paperRepository.findById(1)).thenReturn(Optional.of(goodPaper1));
        Mockito.when(paperRepository.findById(2)).thenReturn(Optional.empty());
        List<Paper> ans = paperService.getFinalDecisionsOfPapersForReviewer(3);
        assertThat(ans.size()).isEqualTo(1);
        Paper ansPaper1 = buildPaper(1, null, Paper.FinalVerdictEnum.ACCEPTED);
        assertThat(ans.get(0)).isEqualTo(ansPaper1);
    }


    @Test
    void getAllPaperIdsForTrack() {
        ResponseEntity<SubmissionPaperIdsResponse> response1 = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
            eq(SubmissionPaperIdsResponse.class))).thenReturn(response1);
        assertThat(paperService.getAllPaperIdsForTrack(1)).isEqualTo(Optional.empty());

        Mockito.when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
            eq(SubmissionPaperIdsResponse.class))).thenThrow(new RuntimeException());
        assertThat(paperService.getAllPaperIdsForTrack(1)).isEqualTo(Optional.empty());

        ResponseEntity<SubmissionPaperIdsResponse> response2 = ResponseEntity.of(Optional.of(new SubmissionPaperIdsResponse()));
        Mockito.when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
            eq(SubmissionPaperIdsResponse.class))).thenReturn(response2);
        assertThat(paperService.getAllPaperIdsForTrack(1)).isEqualTo(Optional.empty());

        SubmissionPaperIdsResponse submissionPaperIdsResponse = new SubmissionPaperIdsResponse();
        submissionPaperIdsResponse.setSubmissionIds(List.of());
        ResponseEntity<SubmissionPaperIdsResponse> emptyPapers = ResponseEntity.of(Optional.of(submissionPaperIdsResponse));
        Mockito.when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
            eq(SubmissionPaperIdsResponse.class))).thenReturn(emptyPapers);
        assertThat(paperService.getAllPaperIdsForTrack(1)).isEqualTo(Optional.of(submissionPaperIdsResponse.getSubmissionIds()));
        Mockito.verify(paperRepository, Mockito.times(0)).save(any());
    }

    @Test
    void getAllPaperIdsForTrackWork() {
        SubmissionPaperIdsResponse submissionPaperIdsResponse = new SubmissionPaperIdsResponse();
        submissionPaperIdsResponse.setSubmissionIds(List.of(1, 2, 3));
        ResponseEntity<SubmissionPaperIdsResponse> papers = ResponseEntity.of(Optional.of(submissionPaperIdsResponse));
        Mockito.when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
            eq(SubmissionPaperIdsResponse.class))).thenReturn(papers);
        assertThat(paperService.getAllPaperIdsForTrack(1)).isEqualTo(Optional.of(submissionPaperIdsResponse.getSubmissionIds()));
        Mockito.verify(paperRepository, Mockito.times(3)).save(any());

    }

    @Test
    public void getConflictsPerReviewerTest() {
        List<Reviewer> reviewers = new ArrayList<>();
        reviewers.add(new Reviewer(1));
        reviewers.add(new Reviewer(2));
        reviewers.add(new Reviewer(3));
        reviewers.add(new Reviewer(4));
        PaperResponse r1 = new PaperResponse("a", Arrays.asList(1,2), 1, "a",
                Collections.emptyList(), "a", Arrays.asList(1,2,4), "a");
        PaperResponse r2 = new PaperResponse("a", Arrays.asList(3,4), 1, "a",
                Collections.emptyList(), "a", Arrays.asList(1,3,5), "a");
        PaperResponse r3 = new PaperResponse("a", Arrays.asList(2, 5), 1, "a",
                Collections.emptyList(), "a", Arrays.asList(2,4, 5), "a");
        Mockito.when(reviewerRepository.findAll()).thenReturn(reviewers);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        Mockito.when(restTemplate.exchange("localhost:8082/submissions/1/info",
                HttpMethod.GET, entity, PaperResponse.class))
                .thenReturn(ResponseEntity.of(Optional.of(r1)));
        Mockito.when(restTemplate.exchange("localhost:8082/submissions/2/info",
                        HttpMethod.GET, entity, PaperResponse.class))
                .thenReturn(ResponseEntity.of(Optional.of(r2)));
        Mockito.when(restTemplate.exchange("localhost:8082/submissions/3/info",
                        HttpMethod.GET, entity, PaperResponse.class))
                .thenReturn(ResponseEntity.of(Optional.of(r3)));
        Map<Integer, List<Integer>> result = paperService
                .getConflictsPerReviewers(Arrays.asList(1,2,3));
        assertThat(result.keySet().size()).isEqualTo(5);
        assertThat(result.get(1).size()).isEqualTo(4);
        assertThat(result.get(2).size()).isEqualTo(3);
        assertThat(result.get(3).size()).isEqualTo(2);
        assertThat(result.get(4).size()).isEqualTo(3);
        assertThat(result.get(5).size()).isEqualTo(4);
    }


}