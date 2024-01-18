package nl.tudelft.sem.template.example.domain.services;

import java.util.*;
import nl.tudelft.sem.template.example.domain.models.Reviewer;
import nl.tudelft.sem.template.example.domain.repositories.CommentRepository;
import nl.tudelft.sem.template.example.domain.repositories.PaperRepository;
import nl.tudelft.sem.template.example.domain.repositories.ReviewerRepository;
import nl.tudelft.sem.template.example.domain.responses.PaperResponse;
import nl.tudelft.sem.template.example.domain.responses.SubmissionPaperIdsResponse;
import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.Paper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class PaperService {
    private final transient UserService userService;
    private final transient ReviewService reviewService;
    private final transient PaperRepository paperRepository;
    private final transient CommentRepository commentRepository;
    private final transient ReviewerRepository reviewerRepository;
    private RestTemplate restTemplate;

    /**
     * PaperService constructor.
     *
     * @param userService UserService reference
     * @param reviewService ReviewService reference
     * @param paperRepository PaperRepository reference
     * @param commentRepository CommentRepository reference
     */
    public PaperService(UserService userService, ReviewService reviewService, PaperRepository paperRepository,
                        CommentRepository commentRepository, ReviewerRepository reviewerRepository, RestTemplate restTemplate) {
        this.paperRepository = paperRepository;
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.reviewService = reviewService;
        this.reviewerRepository = reviewerRepository;
        this.restTemplate = restTemplate;
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

    /**
     * Gets the paper object from the submission.
     *
     * @param paperId to get
     * @return the optional of a paper response
     */
    public Optional<PaperResponse> getPaperObjectFromSubmissions(int paperId) {
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

    /**
     * Method that retrieves the conflicts of interest per reviewer
     * for the papers of a given track
     *
     * @param paperIds the ids of the papers
     * @return the map of the conflicts of interest
     */
    public Map<Integer, List<Integer>> getConflictsPerReviewers(List<Integer> paperIds) {
        Map<Integer, List<Integer>> conflictsOfInterest = new HashMap<>();
        List<Reviewer> reviewers = reviewerRepository.findAll();
        for (Reviewer r: reviewers)
            conflictsOfInterest.put(r.getId(), new ArrayList<>());
        for (Integer id: paperIds) {
            Optional<PaperResponse> response = getPaperObjectFromSubmissions(id);
            if (response.isEmpty())
                continue;
            for (Integer conflict: response.get().getConflictsOfInterest()) {
                if (!conflictsOfInterest.containsKey(conflict))
                    conflictsOfInterest.put(conflict, new ArrayList<>());
                for (Integer author: response.get().getAuthors())
                    if (!conflictsOfInterest.get(conflict).contains(author))
                        conflictsOfInterest.get(conflict).add(author);
            }
        }
        return conflictsOfInterest;
    }


    /**
     * Gets all the comments on a single paper.
     *
     * @param paperId of the paper
     * @return a list of comments
     */
    public List<Comment> paperGetPaperCommentsGet(int paperId) {
        if (paperRepository.findById(paperId).isEmpty()) {
            // This probably shouldn't be like this, but it is like this in the specs.yaml
            return Collections.emptyList();
        }
        return commentRepository.findCommentByPaperId(paperId);
    }

    /**
     * This method checks whether the paper already exists in the db.
     *
     * @param paperId the paper id to check
     * @return true if the paper is in the db
     */
    public boolean isExistingPaper(int paperId) {
        return paperRepository.existsById(paperId);
    }

    /**
     * This method will update the final verdict of a given paper.
     *
     * @param paperId the id of the paper to update
     * @param status to set. Must be either "Unresolved", "Accepted" or "Rejected"
     * @return whether the method succeeded
     */
    public boolean paperUpdatePaperStatusPut(int paperId, String status) {
        Optional<Paper> optional = paperRepository.findById(paperId);
        if (optional.isEmpty()) {
            return false;
        }
        Paper paper = optional.get();

        Paper.FinalVerdictEnum verdict = null;
        if (status.equals("Accepted") || status.equals("Rejected")) {
            verdict = Paper.FinalVerdictEnum.fromValue(status);
        }

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
        if (!userService.validateUser(reviewerId)) {
            return List.of();
        }
        List<Integer> allPaperIdsForReviewer = reviewService.findAllPapersByReviewerId(reviewerId);
        List<Paper> papersForReviewer = new ArrayList<>();
        for (Integer paperId : allPaperIdsForReviewer) {

            Optional<Paper> retrieved = paperRepository.findById(paperId);
            if (retrieved.isEmpty()) {
                continue;
            }

            Paper abstracted = new Paper();
            abstracted.setId(paperId);
            abstracted.setFinalVerdict(retrieved.get().getFinalVerdict());

            papersForReviewer.add(abstracted);
        }
        return papersForReviewer;
    }

    public Optional<List<Integer>> getAllPaperIdsForTrack(int trackId) {
        String submissionsUri = "localhost:8082/tracks/" + trackId + "/submissions";
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        ResponseEntity<SubmissionPaperIdsResponse> response;
        try {
            response = restTemplate.exchange(submissionsUri, HttpMethod.GET, entity, SubmissionPaperIdsResponse.class);
        } catch (Exception e) {
            return Optional.empty();
        }
        SubmissionPaperIdsResponse paperIdsResponse = response.getBody();
        List<Integer> paperIds = null;
        if(paperIdsResponse == null)
            return Optional.empty();
        paperIds = paperIdsResponse.getSubmissionIds();
        if(paperIds == null)
            return Optional.empty();
        for(int id : paperIds) {
            Paper p = new Paper();
            p.setId(id);
            paperRepository.save(p);
        }
        return Optional.of(paperIds);
    }

}
