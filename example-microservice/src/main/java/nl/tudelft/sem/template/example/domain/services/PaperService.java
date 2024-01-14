package nl.tudelft.sem.template.example.domain.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nl.tudelft.sem.template.example.domain.repositories.CommentRepository;
import nl.tudelft.sem.template.example.domain.repositories.PaperRepository;
import nl.tudelft.sem.template.example.domain.repositories.ReviewRepository;
import nl.tudelft.sem.template.example.domain.responses.PaperResponse;
import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.Paper;
import nl.tudelft.sem.template.model.Review;
import org.h2.engine.User;
import org.springframework.http.*;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class PaperService {
    private final transient UserService userService;
    private final transient ReviewService reviewService;
    private final transient PaperRepository paperRepository;
    private final transient CommentRepository commentRepository;

    public PaperService(UserService userService, ReviewService reviewService, PaperRepository paperRepository, CommentRepository commentRepository) {
        this.paperRepository = paperRepository;
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.reviewService = reviewService;
    }

    /**
     * Returns a paper with given id from the paper repository.
     *
     * @param paperId - the id of the paper we want to return
     * @return the found Paper object, or null if there is no object found
     */
    public Optional<Paper> getPaperObjectWithId(int paperId) {
        return paperRepository.findById(paperId);
    }

    public Optional<PaperResponse> getPaperObjectFromSubmissions(int paperId, RestTemplate restTemplate) {
        String submissionsUri = "localhost:8082/submissions/" + paperId + "/info";
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        ResponseEntity<PaperResponse> result;
        try {
            result = restTemplate.exchange(submissionsUri, HttpMethod.GET, entity, PaperResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
        return Optional.ofNullable(result.getBody());
    }

    public List<Comment> paperGetPaperCommentsGet(int paperId) {
        if (paperRepository.findById(paperId).isEmpty())
            // This probably shouldn't be like this, but it is like this in the specs.yaml
            return new ArrayList<>(0);
        return commentRepository.findCommentByPaperId(paperId);
    }

    /**
     * This method checks whether the paper already exists in the db
     * @param paperId the paper id to check
     * @return true if the paper is in the db
     */
    public boolean isExistingPaper(int paperId) {
        return paperRepository.existsById(paperId);
    }

    /**
     * This method will update the final verdict of a given paper
     * @param paperId the id of the paper to update
     * @param verdict to set
     * @return whether the method succeeded
     */
    public boolean paperUpdatePaperStatusPut(int paperId, Paper.FinalVerdictEnum verdict) {
        Optional<Paper> optional = paperRepository.findById(paperId);
        if (optional.isEmpty())
            return false;
        Paper paper = optional.get();
        paper.finalVerdict(verdict);
        paperRepository.save(paper);
        return true;
    }

    public List<Paper> findAllPapersForIdList(List<Integer> paperIds) {
        return paperRepository.findAllById(paperIds);
    }


    /**
     * returns a List of Papers including only the id and the final decision,
     * each of them having been reviewed by the provided reviewer.
     *
     * @param reviewerId - the id of the reviewer
     * @return List of Paper objects
     */
    public List<Paper> getFinalDecisionsOfPapersForReviewer(int reviewerId) {
        if(!userService.validateUser(reviewerId))
            return List.of();
        List<Integer> allPaperIdsForReviewer = reviewService.findAllPapersByReviewerId(reviewerId);
        List<Paper> papersForReviewer = new ArrayList<>();
        for(Integer paperId : allPaperIdsForReviewer){
            Optional<Paper> retrieved = paperRepository.findById(paperId);
            if(retrieved.isEmpty())
                continue;
            Paper abstracted = new Paper();
            abstracted.setId(paperId);
            abstracted.setFinalVerdict(retrieved.get().getFinalVerdict());
            papersForReviewer.add(abstracted);
        }
        return papersForReviewer;
    }

}
