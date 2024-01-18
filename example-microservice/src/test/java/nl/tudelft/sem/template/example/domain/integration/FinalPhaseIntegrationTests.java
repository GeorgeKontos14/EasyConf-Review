package nl.tudelft.sem.template.example.domain.integration;

import nl.tudelft.sem.template.example.domain.models.PcChair;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc

public class FinalPhaseIntegrationTests {

//  Final Phase: the PC Chair gets the reviews, adds comments on them, updates the final verdict and resolves conflicts.

    @Autowired
    private TrackPhaseRepository trackPhaseRepository;
    @Autowired
    private PaperRepository paperRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private PcChairRepository pcChairRepository;
    @Autowired
    private CommentRepository commentRepository;
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
        PcChair pcChair = new PcChair();
        pcChair.setId(66);
        pcChair.addPaper(p.getId());
        pcChairRepository.save(pcChair);
        Comment c = new Comment();
        c.setId(5);
        c.paperId(3);
        c.confidential(true);
        commentRepository.save(c);
    }

    @Test
    void getPaperCommentsTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/paper/getPaperComments")
                .param("paperID", "3")
                .param("userID", "99"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(5));

        mockMvc.perform(MockMvcRequestBuilders.get("/paper/getPaperComments")
                        .param("paperID", "333")
                        .param("userID", "99"));

        mockMvc.perform(MockMvcRequestBuilders.get("/paper/getPaperComments")
                        .param("paperId", "333")
                        .param("userID", "99"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void getPaperReviewsTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/paper/getPaperReviews")
                .param("paperId", "3")
                .param("userID", "99"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(6));

        mockMvc.perform(MockMvcRequestBuilders.get("/paper/getPaperReviews")
                        .param("paperId", "93")
                        .param("userID", "99"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        mockMvc.perform(MockMvcRequestBuilders.get("/paper/getPaperReviews")
                        .param("paperID", "93")
                        .param("userID", "99"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void updatePaperStatusAccepted() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/paper/updatePaperStatus")
                .param("paperID", "3")
                .param("status", "Accepted")
                .param("userID", "66"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void updatePaperStatusRejected() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/paper/updatePaperStatus")
                        .param("paperID", "3")
                        .param("status", "Rejected")
                        .param("userID", "66"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void updatePaperStatusUnresolved() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/paper/updatePaperStatus")
                        .param("paperID", "3")
                        .param("status", "Unresolved")
                        .param("userID", "66"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void updatePaperStatusBalderdash() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/paper/updatePaperStatus")
                        .param("paperID", "3")
                        .param("status", "balderdash")
                        .param("userID", "66"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void updatePaperStatusBadParam() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/paper/updatePaperStatus")
                        .param("paperId", "3")
                        .param("status", "balderdash")
                        .param("userID", "66"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void updatePaperStatusNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/paper/updatePaperStatus")
                        .param("paperID", "56")
                        .param("status", "Rejected")
                        .param("userID", "66"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}
