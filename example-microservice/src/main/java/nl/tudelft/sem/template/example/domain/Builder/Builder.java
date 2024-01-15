package nl.tudelft.sem.template.example.domain.Builder;

import java.util.List;

public interface Builder {
    public void setInputParameters(List<Object> inputParameters);

    public void setUserId(Integer userId);

    public void setPaperIds(List<Integer> paperIds);

    public void setReviewIds(List<Integer> reviewIds);

    public CheckSubject build();
}
