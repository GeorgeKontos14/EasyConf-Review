package nl.tudelft.sem.template.example.domain.services;

import nl.tudelft.sem.template.example.domain.models.TrackPhase;
import nl.tudelft.sem.template.example.domain.repositories.TrackPhaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

public class TrackPhaseServiceTest {
    private TrackPhaseRepository trackPhaseRepository;
    private TrackPhaseService sut;
    private RestTemplate restTemplate;

    @BeforeEach
    public void setup() {
        trackPhaseRepository = Mockito.mock(TrackPhaseRepository.class);
        restTemplate = Mockito.mock(RestTemplate.class);
        sut = new TrackPhaseService(trackPhaseRepository);
    }

    @Test
    public void getTrackPapersExceptionTest() {
        Mockito.when(restTemplate.getForObject(anyString(), any()))
                .thenThrow(IllegalArgumentException.class);
        Optional<List<Integer>> result = sut.getTrackPapers(1, restTemplate);
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    public void getTrackPhaseNullResponseTest() {
        Mockito.when(restTemplate.getForObject(anyString(), any()))
                .thenReturn(null);
        Optional<List<Integer>> result = sut.getTrackPapers(1, restTemplate);
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    public void getTrackPhaseOkTest() {
        Mockito.when(restTemplate.getForObject(anyString(), any()))
                .thenReturn(new TrackPhaseService.IntegerList(Arrays.asList(1,2,34)));
        Optional<List<Integer>> result = sut.getTrackPapers(1, restTemplate);
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo(Arrays.asList(1,2,34));
    }

    @Test
    public void saveTrackPhaseTest() {
        TrackPhase phase = new TrackPhase(Arrays.asList(1,2));
        sut.saveTrackPhase(phase);
        Mockito.verify(trackPhaseRepository).save(any(TrackPhase.class));
    }

}
