package nl.tudelft.sem.template.example.domain.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.template.example.domain.models.TrackPhase;
import nl.tudelft.sem.template.example.domain.repositories.*;
import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.Paper;
import nl.tudelft.sem.template.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class DiscussionPhaseIntegrationTest {

    @Autowired
    private TrackPhaseRepository trackPhaseRepository;
    @Autowired
    private PaperRepository paperRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        Paper p = new Paper();
        p.id(3);
        paperRepository.save(p);
        Review r = new Review();
        r.setId(5);
        r.paperId(3);
        r.reviewerId(10);
        r.setOverallScore(Review.OverallScoreEnum.NUMBER_MINUS_1);
        reviewRepository.save(r);
        Review r1 = new Review();
        r1.setId(6);
        r1.paperId(3);
        r1.reviewerId(11);
        reviewRepository.save(r1);
        TrackPhase tp = new TrackPhase();
        tp.setId(54);
        tp.setPapers(List.of(3));
        tp.setPhase(TrackPhase.PhaseEnum.DISCUSSION);
        trackPhaseRepository.save(tp);
    }

    @Test
    void postReviewTest() throws Exception {
        Comment c = new Comment();
        c.id(77);
        c.paperId(3);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/review/postComment")
                        .param("userID", Integer.toString(10))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsBytes(c))
                ).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(77));

        this.mockMvc.perform(MockMvcRequestBuilders.post("/review/postComment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsBytes(c))
                ).andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }



    @Test
    void editConfidenceScoreTest() throws Exception {
        Review r = new Review();
        r.setId(5);
        r.paperId(3);
        r.reviewerId(10);
        r.setOverallScore(Review.OverallScoreEnum.NUMBER_1);

        mockMvc.perform(MockMvcRequestBuilders.put("/review/editConfidenceScore")
                .param("userID", Integer.toString(10))
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsBytes(r)))
                .andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.put("/review/editConfidenceScore")
                        .param("userId", Integer.toString(10))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void findAllReviewsByPaperIdTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/review/findAllReviewsByPaperId")
                .param("paperID", "3")
                        .param("userID", "10"))
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(6));

        mockMvc.perform(MockMvcRequestBuilders.get("/review/findAllReviewsByPaperId")
                        .param("paperID", "3"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.get("/review/findAllReviewsByPaperId")
                        .param("paperID", "455")
                        .param("userID", "10"))
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
    }

    @Test
    void findPaperByReviewId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/review/findPaperByReviewId")
                        .param("reviewID", "6")
                        .param("userID", "11"))
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(3));

        mockMvc.perform(MockMvcRequestBuilders.get("/review/findPaperByReviewId")
                        .param("reviewId", "6")
                        .param("userID", "11"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.get("/review/findPaperByReviewId")
                        .param("reviewID", "499")
                        .param("userID", "11"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }




}
