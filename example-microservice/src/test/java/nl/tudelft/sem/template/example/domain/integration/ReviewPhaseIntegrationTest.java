package nl.tudelft.sem.template.example.domain.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.Access;
import javax.persistence.criteria.CriteriaBuilder;
import nl.tudelft.sem.template.example.domain.models.PcChair;
import nl.tudelft.sem.template.example.domain.models.PreferenceEntity;
import nl.tudelft.sem.template.example.domain.models.Reviewer;
import nl.tudelft.sem.template.example.domain.repositories.CommentRepository;
import nl.tudelft.sem.template.example.domain.repositories.PaperRepository;
import nl.tudelft.sem.template.example.domain.repositories.PcChairRepository;
import nl.tudelft.sem.template.example.domain.repositories.ReviewRepository;
import nl.tudelft.sem.template.example.domain.repositories.ReviewerPreferencesRepository;
import nl.tudelft.sem.template.example.domain.repositories.ReviewerRepository;
import nl.tudelft.sem.template.example.domain.repositories.TrackPhaseRepository;
import nl.tudelft.sem.template.example.domain.responses.PaperResponse;
import nl.tudelft.sem.template.example.domain.responses.SubmissionPaperIdsResponse;
import nl.tudelft.sem.template.example.domain.services.PaperService;
import nl.tudelft.sem.template.example.domain.services.TrackPhaseService;
import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.Paper;
import nl.tudelft.sem.template.model.PcChairReviewComment;
import nl.tudelft.sem.template.model.Review;
import nl.tudelft.sem.template.model.ReviewerPreferences;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class ReviewPhaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ReviewerPreferencesRepository reviewerPreferencesRepository;

    @Autowired
    private ReviewerRepository reviewerRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PaperRepository paperRepository;

    @Autowired
    private CommentRepository commentRepository;

    @MockBean
    private RestTemplate restTemplate;


    private PreferenceEntity buildPref(int id, int paperId, int reviewerId,
                                       ReviewerPreferences.ReviewerPreferenceEnum preferenceEnum) {
        PreferenceEntity pref = new PreferenceEntity();
        pref.setId(id);
        pref.setPaperId(paperId);
        pref.setReviewerId(reviewerId);
        pref.setPreferenceEnum(preferenceEnum);
        return pref;
    }


    private Review buildReview(int id, int paperId, int reviewerId) {
        Review review = new Review();
        review.setId(id);
        review.setPaperId(paperId);
        review.setReviewerId(reviewerId);
        review.setConfidenceScore(Review.ConfidenceScoreEnum.NUMBER_1);
        review.setOverallScore(Review.OverallScoreEnum.NUMBER_1);
        return review;
    }

    private Reviewer buildReviewer(int id, List<Integer> reviews, List<Integer> preferences) {
        Reviewer reviewer = new Reviewer();
        reviewer.setId(id);
        reviewer.setReviews(reviews);
        reviewer.setPreferences(preferences);
        return reviewer;
    }

    private Paper buildPaper(int id, List<Integer> authors) {
        Paper paper = new Paper();
        paper.setId(id);
        paper.setAuthors(authors);
        paper.setFinalVerdict(Paper.FinalVerdictEnum.ACCEPTED);
        return paper;
    }

    /**
     * a reviewer gets the papers they are responsible for, retrieves the full text, updates their confidence score, writes comments to authors/ reviewers and gives a final score.
     * */
    @Test
    void reviewScenarioTest() throws Exception {


        // Create the Reviewer
        Reviewer reviewer = buildReviewer(1, List.of(), List.of(1));
        reviewerRepository.save(reviewer);
        //Create reviews and papers
        Review r1 = buildReview(1, 1, 1);
        Review r2 = buildReview(2, 2, 1);
        Review r3 = buildReview(3, 3, 1);
        Paper p1 = buildPaper(1,List.of(4));
        Paper p2 = buildPaper(2, List.of(5));
        Paper p3 = buildPaper(3, List.of(6));
        reviewRepository.saveAll(List.of(r1,r2,r3));
        paperRepository.saveAll(List.of(p1,p2,p3));
        // Get all papers reviewer is responsible for getPapersWithId (api endpoint for papers)
        ///paper/getAllPapersForID
        ObjectMapper objectMapper = new ObjectMapper();
        ResultActions paperJson = mockMvc.perform(
            get("/paper/getAllPapersForID").contentType(MediaType.APPLICATION_JSON)
                .param("reviewer_id", Integer.toString(1)));

        paperJson.andExpect(status().isOk());
        String responseContent = paperJson.andReturn().getResponse().getContentAsString();
        List<Paper> papers = objectMapper.readValue(responseContent, new TypeReference<List<Paper>>() {});
        assertThat(papers.size()).isEqualTo(3);
        // for each paperId, get PaperResponse object with full text
        for(Paper paper : papers)
            getFullText(paper.getId());
        // update confidence score endpoint
        editConfidenceScores(r1, r2, r3);
        // writes comments to authors/reviewers

        Comment c1 = buildComment(1, "hello", 1, 1);
        Comment c2 = buildComment(2, "now", 2, 1);

        mockMvc.perform(
                post("/review/postComment").contentType(MediaType.APPLICATION_JSON)
                    .param("userID", Integer.toString(1))
                    .content(objectMapper.writeValueAsString(c1)))
            .andExpect(status().isOk());

        mockMvc.perform(
                post("/review/postComment").contentType(MediaType.APPLICATION_JSON)
                    .param("userID", Integer.toString(1))
                    .content(objectMapper.writeValueAsString(c2)))
            .andExpect(status().isOk());

        assertThat(commentRepository.existsById(1)).isTrue();
        assertThat(commentRepository.existsById(2)).isTrue();

        // post final score

        setFinalScore(r1, r2, r3);

    }


    private void editConfidenceScores(Review r1, Review r2, Review r3) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        r1.setConfidenceScore(Review.ConfidenceScoreEnum.NUMBER_2);
        r2.setConfidenceScore(Review.ConfidenceScoreEnum.NUMBER_1);
        r3.setConfidenceScore(Review.ConfidenceScoreEnum.NUMBER_3);
        ///review/editConfidenceScore
        mockMvc.perform(
                put("/review/editConfidenceScore").contentType(MediaType.APPLICATION_JSON)
                    .param("userID", Integer.toString(1))
                    .content(objectMapper.writeValueAsString(r1)))
            .andExpect(status().isOk());

        mockMvc.perform(
                put("/review/editConfidenceScore").contentType(MediaType.APPLICATION_JSON)
                    .param("userID", Integer.toString(1))
                    .content(objectMapper.writeValueAsString(r2)))
            .andExpect(status().isOk());

        mockMvc.perform(
                put("/review/editConfidenceScore").contentType(MediaType.APPLICATION_JSON)
                    .param("userID", Integer.toString(1))
                    .content(objectMapper.writeValueAsString(r3)))
            .andExpect(status().isOk());

        Optional<Review> extractedFirst = reviewRepository.findById(1);
        assertThat(extractedFirst.isPresent()).isTrue();
        assertThat(extractedFirst.get().getConfidenceScore()).isEqualTo(Review.ConfidenceScoreEnum.NUMBER_2);

        Optional<Review> extractedSecond = reviewRepository.findById(2);
        assertThat(extractedSecond.isPresent()).isTrue();
        assertThat(extractedSecond.get().getConfidenceScore()).isEqualTo(Review.ConfidenceScoreEnum.NUMBER_1);

        Optional<Review> extractedThird = reviewRepository.findById(3);
        assertThat(extractedThird.isPresent()).isTrue();
        assertThat(extractedThird.get().getConfidenceScore()).isEqualTo(Review.ConfidenceScoreEnum.NUMBER_3);

    }


    private void setFinalScore(Review r1, Review r2, Review r3) throws Exception {
        r1.setOverallScore(Review.OverallScoreEnum.NUMBER_MINUS_2);
        r2.setOverallScore(Review.OverallScoreEnum.NUMBER_MINUS_1);
        r3.setOverallScore(Review.OverallScoreEnum.NUMBER_1);

        ObjectMapper objectMapper = new ObjectMapper();
        mockMvc.perform(
                put("/review/editOverallScore").contentType(MediaType.APPLICATION_JSON)
                    .param("userID", Integer.toString(1))
                    .content(objectMapper.writeValueAsString(r1)))
            .andExpect(status().isOk());

        mockMvc.perform(
                put("/review/editOverallScore").contentType(MediaType.APPLICATION_JSON)
                    .param("userID", Integer.toString(1))
                    .content(objectMapper.writeValueAsString(r2)))
            .andExpect(status().isOk());

        mockMvc.perform(
                put("/review/editOverallScore").contentType(MediaType.APPLICATION_JSON)
                    .param("userID", Integer.toString(1))
                    .content(objectMapper.writeValueAsString(r3)))
            .andExpect(status().isOk());

        Optional<Review> extractedFirst = reviewRepository.findById(1);
        assertThat(extractedFirst.isPresent()).isTrue();
        assertThat(extractedFirst.get().getOverallScore()).isEqualTo(Review.OverallScoreEnum.NUMBER_MINUS_2);

        Optional<Review> extractedSecond = reviewRepository.findById(2);
        assertThat(extractedSecond.isPresent()).isTrue();
        assertThat(extractedSecond.get().getOverallScore()).isEqualTo(Review.OverallScoreEnum.NUMBER_MINUS_1);

        Optional<Review> extractedThird = reviewRepository.findById(3);
        assertThat(extractedThird.isPresent()).isTrue();
        assertThat(extractedThird.get().getOverallScore()).isEqualTo(Review.OverallScoreEnum.NUMBER_1);

    }
    private Comment buildComment(int id, String text, int paperId, int authorId) {
        Comment c = new Comment();
        c.setId(id);
        c.setText(text);
        c.setPaperId(paperId);
        c.setAuthorId(authorId);
        return c;
    }

    private String getFullText(Integer paperId) {
        PaperResponse paperResponse = new PaperResponse("title"+paperId, List.of(), null, "abstract1", null, null, null, null);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        given(restTemplate.exchange("localhost:8082/submissions/1/info", HttpMethod.GET, entity,
            PaperResponse.class)).willReturn(ResponseEntity.of(Optional.of(paperResponse)));
        return paperResponse.getAbstract();
    }

}
