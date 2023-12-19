package nl.tudelft.sem.template.example.services;

import java.util.Optional;
import nl.tudelft.sem.template.example.repositories.PaperRepository;
import nl.tudelft.sem.template.model.Paper;
import org.springframework.stereotype.Service;

@Service
public class PaperService {

    private transient PaperRepository paperRepository;

    PaperService(PaperRepository paperRepository) {
        this.paperRepository = paperRepository;
    }

    /**
     * Function that returns a paper with given id from the database.
     *
     * @param paperId - the id of the paper we want to return
     * @return the found Paper object, or null if there is no object found
     */
    public Paper getPaperWithId(int paperId) {
        Optional<Paper> foundPaper = paperRepository.findById(paperId);
        return foundPaper.orElse(null);
    }
}
