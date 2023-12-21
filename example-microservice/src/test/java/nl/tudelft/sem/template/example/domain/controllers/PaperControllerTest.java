package nl.tudelft.sem.template.example.domain.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import nl.tudelft.sem.template.example.domain.controllers.PaperController;
import nl.tudelft.sem.template.example.domain.services.PaperService;
import nl.tudelft.sem.template.example.domain.services.UserService;
import nl.tudelft.sem.template.model.Paper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;




public class PaperControllerTest {
    private PaperService paperService;
    private UserService userService;

    private PaperController paperController;

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
        goodPaper = buildPaper(3, List.of(1, 2, 4, 5), null);
        paperService = Mockito.mock(PaperService.class);
        userService = Mockito.mock(UserService.class);
        paperController = new PaperController(userService, paperService);
    }

    @Test
    void paperGetPaperById_BadRequest_Test() {
        Mockito.when(paperService.getPaperObjectWithId(any(Integer.class))).thenReturn(goodPaper);
        Mockito.when(userService.validateUser(any(Integer.class))).thenReturn(true);

        ResponseEntity<List<Paper>> response = paperController.paperGetPaperByIDGet(null, 3);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(null);

        response = paperController.paperGetPaperByIDGet(3, null);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(null);

        response = paperController.paperGetPaperByIDGet(3, -4);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(null);

        response = paperController.paperGetPaperByIDGet(-4, 3);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(null);
    }

    @Test
    void paperGetPaperById_Unauthorized_Test() {
        Mockito.when(paperService.getPaperObjectWithId(any(Integer.class))).thenReturn(goodPaper);
        Mockito.when(userService.validateUser(4)).thenReturn(false);

        ResponseEntity<List<Paper>> response = paperController.paperGetPaperByIDGet(5, 4);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void paperGetPaperById_NotFound_Test() {
        Mockito.when(userService.validateUser(any(Integer.class))).thenReturn(true);
        Mockito.when(paperService.getPaperObjectWithId(3)).thenReturn(null);

        ResponseEntity<List<Paper>> response = paperController.paperGetPaperByIDGet(3, 8);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void paperGetPaperById_Ok_Test() {
        Mockito.when(userService.validateUser(any(Integer.class))).thenReturn(true);
        Mockito.when(paperService.getPaperObjectWithId(3)).thenReturn(goodPaper);

        ResponseEntity<List<Paper>> response = paperController.paperGetPaperByIDGet(3, 4);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get(0)).isEqualTo(goodPaper);
        assertThat(response.getBody().size()).isEqualTo(1);

    }

    @Test
    void paperGetPaperById_InternalServerError_Test() {

        Mockito.when(paperService.getPaperObjectWithId(3)).thenReturn(goodPaper);
        Mockito.when(userService.validateUser(4)).thenThrow(RuntimeException.class);

        ResponseEntity<List<Paper>> response = paperController.paperGetPaperByIDGet(3, 4);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo(null);
    }
}
