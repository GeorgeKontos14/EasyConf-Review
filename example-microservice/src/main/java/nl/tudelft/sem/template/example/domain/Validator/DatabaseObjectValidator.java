package nl.tudelft.sem.template.example.domain.Validator;

import nl.tudelft.sem.template.example.domain.Builder.CheckSubject;
import nl.tudelft.sem.template.example.domain.repositories.PaperRepository;
import nl.tudelft.sem.template.example.domain.services.PaperService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class DatabaseObjectValidator extends BaseValidator{

    PaperService paperService;
    DatabaseObjectValidator(PaperService paperService) {
        this.paperService = paperService;
    }

    @Override
    public ResponseEntity<Void> handle(CheckSubject checkSubject) {
        for(Integer paperId : checkSubject.getPaperIds())
            if(!paperService.isExistingPaper(paperId))
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return super.checkNext(checkSubject);
    }
}
