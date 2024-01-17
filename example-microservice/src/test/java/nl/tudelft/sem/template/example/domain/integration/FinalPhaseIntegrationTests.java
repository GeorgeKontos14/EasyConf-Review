package nl.tudelft.sem.template.example.domain.integration;

import nl.tudelft.sem.template.example.domain.models.PcChair;
import nl.tudelft.sem.template.example.domain.models.TrackPhase;
import nl.tudelft.sem.template.example.domain.repositories.PaperRepository;
import nl.tudelft.sem.template.example.domain.repositories.PcChairRepository;
import nl.tudelft.sem.template.example.domain.repositories.ReviewRepository;
import nl.tudelft.sem.template.example.domain.repositories.TrackPhaseRepository;
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

    }

}
