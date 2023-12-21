package nl.tudelft.sem.template.example.domain.services;

import java.util.Optional;
import nl.tudelft.sem.template.model.Paper;
import nl.tudelft.sem.template.example.domain.repositories.PaperRepository;
import org.springframework.stereotype.Service;

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
    public Paper getPaperObjectWithId(int paperId) {
        Optional<Paper> foundPaper = paperRepository.findById(paperId);
        return foundPaper.orElse(null);
    }

}
