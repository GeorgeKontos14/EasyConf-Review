package nl.tudelft.sem.template.example.services;

import nl.tudelft.sem.template.example.repositories.PaperRepository;
import nl.tudelft.sem.template.model.Paper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaperService {

    private PaperRepository paperRepository;
    PaperService(PaperRepository paperRepository)
    {
        this.paperRepository = paperRepository;
    }
    public Paper getPaperWithId(int paperId)
    {
        Optional<Paper> foundPaper = paperRepository.findById(paperId);
        return foundPaper.orElse(null);
    }
}
