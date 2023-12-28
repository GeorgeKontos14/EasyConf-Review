package nl.tudelft.sem.template.example.domain.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import nl.tudelft.sem.template.example.domain.repositories.PaperRepository;
import nl.tudelft.sem.template.example.domain.services.UserService;
import nl.tudelft.sem.template.model.Paper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class PaperIntegrationTest {


    @Autowired
    private PaperRepository paperRepository;

    @Autowired
    private MockMvc mockMvc;

    private Paper buildPaper(int id, List<Integer> authors, Paper.FinalVerdictEnum finalVerdictEnum) {
        Paper paper = new Paper();
        paper.setId(id);
        paper.setAuthors(authors);
        paper.setFinalVerdict(finalVerdictEnum);
        return paper;
    }


    @Test
    void paperGetPaperById_NotFound_Test() throws Exception {
        int badPaperId = 1232112;
        int goodUserId = 3;

        ResultActions result = mockMvc.perform(get("/paper/getPaperByID")
                .contentType(MediaType.APPLICATION_JSON)
                .param("PaperId", Integer.toString(badPaperId))
                .param("userId", Integer.toString(goodUserId)));

        // Assert
        result.andExpect(status().isNotFound());
    }

    @Test
    void paperGetPaperById_Ok_Test() throws Exception {
        Paper goodPaper = buildPaper(3, List.of(1, 3, 4, 5), Paper.FinalVerdictEnum.ACCEPTED);
        paperRepository.save(goodPaper);
        ResultActions result = mockMvc.perform(get("/paper/getPaperByID")
                .contentType(MediaType.APPLICATION_JSON)
                .param("PaperId", Integer.toString(3))
                .param("userId", Integer.toString(7)));
        // Assert
        result.andExpect(status().isOk());
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = result.andReturn().getResponse().getContentAsString();
        List<Paper> response = objectMapper.readValue(jsonResponse, new TypeReference<List<Paper>>() {});
        assertThat(response.get(0)).isEqualTo(goodPaper);
        assertThat(response.size()).isEqualTo(1);
    }
}