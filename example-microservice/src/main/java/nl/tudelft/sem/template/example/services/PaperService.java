package nl.tudelft.sem.template.example.services;

import java.util.Optional;
import nl.tudelft.sem.template.example.entities.Paper;
import nl.tudelft.sem.template.example.repositories.PaperRepository;
import org.springframework.stereotype.Service;

@Service
public class PaperService {

    private transient PaperRepository paperRepository;

    PaperService(PaperRepository paperRepository) {
        this.paperRepository = paperRepository;
    }

    /**
     * Returns a paper with given id from the paper repository.
     *
     * @param paperId - the id of the paper we want to return
     * @return the found Paper object, or null if there is no object found
     */
    public Paper getPaperWithId(int paperId) {
        Optional<Paper> foundPaper = paperRepository.findById(paperId);
        return foundPaper.orElse(null);
    }

    /**
     * Method that converts a Paper JPA Entity to an Paper Model used by the API,
     * containing exactly the same information.
     *
     * @param paperEntity a Paper Entity object
     * @return an equivalent Paper Model
     */
    public nl.tudelft.sem.template.model.Paper turnEntityPaperToModel(Paper paperEntity) {
        nl.tudelft.sem.template.model.Paper paperModel = new nl.tudelft.sem.template.model.Paper();
        paperModel.setId(paperEntity.getId());
        paperModel.setAuthors(paperEntity.getAuthors());
        String enumValue = paperEntity.getFinalVerdict().getValue();
        paperModel.setFinalVerdict(nl.tudelft.sem.template.model.Paper.FinalVerdictEnum.valueOf(enumValue));
        return paperModel;
    }
}
