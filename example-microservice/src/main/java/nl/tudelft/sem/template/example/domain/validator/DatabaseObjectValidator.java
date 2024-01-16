package nl.tudelft.sem.template.example.domain.validator;

import nl.tudelft.sem.template.example.domain.builder.CheckSubject;
import nl.tudelft.sem.template.example.domain.services.PaperService;
import nl.tudelft.sem.template.example.domain.services.ReviewService;
import org.springframework.http.HttpStatus;

public class DatabaseObjectValidator extends BaseValidator{

    PaperService paperService;
    ReviewService reviewService;
    DatabaseObjectValidator(PaperService paperService, ReviewService reviewService) {
        this.paperService = paperService;
        this.reviewService = reviewService;
    }

    @Override
    public boolean handle(CheckSubject checkSubject) throws ValidatorException {

        boolean paperIdStatus = areAllPaperIdsValid(checkSubject);
        if(!paperIdStatus)
            throw new ValidatorException(HttpStatus.NOT_FOUND);

        boolean reviewIdStatus = areAllReviewIdsValid(checkSubject);
        if(!reviewIdStatus)
            throw new ValidatorException(HttpStatus.NOT_FOUND);

        return super.checkNext(checkSubject);
    }

    private boolean areAllReviewIdsValid(CheckSubject checkSubject) {
        if(checkSubject.getReviewIds() == null)
            return true;
        for(Integer reviewId : checkSubject.getReviewIds())
            if(!reviewService.existsReview(reviewId))
                return false;
        return true;
    }

    private boolean areAllPaperIdsValid(CheckSubject checkSubject) {
        if(checkSubject.getPaperIds() == null)
            return true;
        for(Integer paperId : checkSubject.getPaperIds())
            if(!paperService.isExistingPaper(paperId))
                return false;
        return true;
    }
}
