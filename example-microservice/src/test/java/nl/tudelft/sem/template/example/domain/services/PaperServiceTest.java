package nl.tudelft.sem.template.example.domain.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nl.tudelft.sem.template.example.domain.repositories.CommentRepository;
import nl.tudelft.sem.template.example.domain.repositories.PaperRepository;
import nl.tudelft.sem.template.example.domain.repositories.ReviewRepository;
import nl.tudelft.sem.template.example.domain.responses.PaperResponse;
import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.Paper;
import nl.tudelft.sem.template.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


class PaperServiceTest {

    private RestTemplate restTemplate;
    private PaperRepository paperRepository;
    private CommentRepository commentRepository;
    private ReviewRepository reviewRepository;
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
        reviewRepository = Mockito.mock(ReviewRepository.class);
        paperService = new PaperService(paperRepository, commentRepository, reviewRepository);
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
        Optional<PaperResponse> response = paperService.getPaperObjectFromSubmissions(3, restTemplate);
        assertThat(response).isEqualTo(Optional.of(paperResponse));
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
    void paperGetAllPapersForIDGetTest() {
        Paper p = new Paper();
        p.id(1);
        Mockito.when(paperRepository.findAllById(List.of(1)))
                .thenReturn(List.of(p));
        Review r = new Review();
        r.id(7);
        r.reviewerId(5);
        r.paperId(1);
        Mockito.when(reviewRepository.findReviewByReviewerId(5))
                .thenReturn(List.of(r));
        assertThat(paperService.paperGetAllPapersForIDGet(5)).isEqualTo(List.of(p));
    }
}