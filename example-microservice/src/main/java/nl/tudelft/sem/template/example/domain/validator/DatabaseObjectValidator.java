package nl.tudelft.sem.template.example.domain.validator;

import nl.tudelft.sem.template.example.domain.builder.CheckSubject;
import nl.tudelft.sem.template.example.domain.services.PaperService;
import org.springframework.http.HttpStatus;

public class DatabaseObjectValidator extends BaseValidator{

    PaperService paperService;
    DatabaseObjectValidator(PaperService paperService) {
        this.paperService = paperService;
    }

    @Override
    public boolean handle(CheckSubject checkSubject) throws ValidatorException {

        if(checkSubject.getPaperIds() == null)
            return super.checkNext(checkSubject);

        for(Integer paperId : checkSubject.getPaperIds())
            if(!paperService.isExistingPaper(paperId))
                throw new ValidatorException(HttpStatus.NOT_FOUND);

        return super.checkNext(checkSubject);
    }
}
