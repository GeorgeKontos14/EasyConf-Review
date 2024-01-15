package nl.tudelft.sem.template.example.domain.Builder;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

public class CheckSubject {

    private List<Object> inputParameters;
    private Integer userId;
    private List<Integer> paperIds;
    private List<Integer> reviewIds;

    public CheckSubject(List<Object> inputParameters, Integer userId, List<Integer> paperIds, List<Integer> reviewIds) {
        this.inputParameters = inputParameters;
        this.userId = userId;
        this.paperIds = paperIds;
        this.reviewIds = reviewIds;
    }
}
