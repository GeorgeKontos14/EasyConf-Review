package nl.tudelft.sem.template.example.domain.services;

import java.util.Collections;
import java.util.Optional;
import nl.tudelft.sem.template.example.domain.repositories.PaperRepository;
import nl.tudelft.sem.template.example.domain.responses.PaperResponse;
import nl.tudelft.sem.template.model.Paper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PaperService {

    private transient PaperRepository paperRepository;

    public PaperService(PaperRepository paperRepository) {
        this.paperRepository = paperRepository;
    }

    /**
     * Returns a paper with given id from the paper repository.
     *
     * @param paperId - the id of the paper we want to return
     * @return the found Paper object, or null if there is no object found
     */
    public Optional<Paper> getPaperObjectWithId(int paperId) {
        Optional<Paper> foundPaper = paperRepository.findById(paperId);
        return foundPaper;
    }

    public Optional<PaperResponse> getPaperObjectFromSubmissions(int paperId, RestTemplate restTemplate)
    {
        String submissionsUri = "localhost:8082/submissions/"+paperId+"/info";
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        ResponseEntity<PaperResponse> result = null;
        try {
            result = restTemplate.exchange(submissionsUri, HttpMethod.GET, entity, PaperResponse.class);
        } catch(Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
        return Optional.of(result.getBody());
    }

}
