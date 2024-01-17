package nl.tudelft.sem.template.example.domain.integration;

import nl.tudelft.sem.template.example.domain.repositories.PaperRepository;
import nl.tudelft.sem.template.example.domain.repositories.ReviewRepository;
import nl.tudelft.sem.template.example.domain.repositories.ReviewerPreferencesRepository;
import nl.tudelft.sem.template.example.domain.services.TrackPhaseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class AssignmentPhaseIntegrationTest {
    @Autowired
    private PaperRepository paperRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReviewerPreferencesRepository reviewerPreferencesRepository;
    private RestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void manualAssignmentTest() throws Exception {
        restTemplate = Mockito.mock(RestTemplate.class);
        Mockito.when(restTemplate
                        .getForObject("localhost:8081/tracks/1/submissions",
                        TrackPhaseService.IntegerList.class))
                        .thenReturn(new TrackPhaseService.IntegerList(Arrays.asList(1,2,3)));
        TrackPhaseService.IntegerList l = restTemplate.getForObject("localhost:8081/tracks/1/submissions",
                TrackPhaseService.IntegerList.class);
        System.out.println(l.ints);
    }


}
