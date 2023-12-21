package nl.tudelft.sem.template.example.domain.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.example.domain.repositories.PaperRepository;
import nl.tudelft.sem.template.example.domain.services.PaperService;
import nl.tudelft.sem.template.model.Paper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;



class PaperServiceTest {

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
        paperService = new PaperService(paperRepository);
    }

    @Test
    void getPaperObjectWithIdExistentTest() {
        Mockito.when(paperRepository.findById(3)).thenReturn(Optional.of(goodPaper));
        Paper result = paperService.getPaperObjectWithId(3);
        assertThat(result).isEqualTo(goodPaper);
    }

    @Test
    void getPaperObjectWithIdNonExistentTest() {
        Mockito.when(paperRepository.findById(5)).thenReturn(Optional.empty());
        Paper result = paperService.getPaperObjectWithId(5);
        assertThat(result).isNull();
    }
}