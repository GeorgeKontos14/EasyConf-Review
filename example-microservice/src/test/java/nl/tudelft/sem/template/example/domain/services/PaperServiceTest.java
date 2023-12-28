package nl.tudelft.sem.template.example.domain.services;

import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.STRING;
import static org.mockito.ArgumentMatchers.*;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.example.domain.repositories.PaperRepository;
import nl.tudelft.sem.template.example.domain.responses.PaperResponse;
import nl.tudelft.sem.template.example.domain.services.PaperService;
import nl.tudelft.sem.template.model.Paper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


class PaperServiceTest {

    private RestTemplate restTemplate;
    private PaperRepository paperRepository;
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
        paperService = new PaperService(paperRepository);
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
        PaperResponse paperResponse = new PaperResponse("hello", List.of(1, 2, 3), 4, "abstr", List.of("key1","key2"), "link1", List.of(1,2,3), "link2");

        ResponseEntity<PaperResponse> result = ResponseEntity.of(Optional.of(paperResponse));
        Mockito.when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(PaperResponse.class))).thenReturn(result);
        Optional<PaperResponse> response = paperService.getPaperObjectFromSubmissions(3, restTemplate);
        assertThat(response).isEqualTo(Optional.of(paperResponse));
    }
}