package nl.tudelft.sem.template.example.domain.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import nl.tudelft.sem.template.example.domain.repositories.CommentRepository;
import nl.tudelft.sem.template.example.domain.repositories.PaperRepository;
import nl.tudelft.sem.template.example.domain.responses.PaperResponse;
import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.Paper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PaperService {

    private final transient PaperRepository paperRepository;
    private final transient CommentRepository commentRepository;

    public PaperService(PaperRepository paperRepository, CommentRepository commentRepository) {
        this.paperRepository = paperRepository;
        this.commentRepository = commentRepository;
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

    public List<Comment> paperGetPaperCommentsGet(int paperId) {
        if (paperRepository.findById(paperId).isEmpty())
            // This probably shouldn't be like this, but it is like this in the specs.yaml
            return new ArrayList<>(0);
        return commentRepository.findCommentByPaperId(paperId);
    }

}
