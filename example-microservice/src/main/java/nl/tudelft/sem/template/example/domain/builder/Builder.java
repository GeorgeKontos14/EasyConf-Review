package nl.tudelft.sem.template.example.domain.builder;

import java.util.List;

public interface Builder {
    public void setInputParameters(List<Object> inputParameters);

    public void setUserId(Integer userId);

    public void setPaperIds(List<Integer> paperIds);

    public void setReviewIds(List<Integer> reviewIds);

    public void setEnumValue(String status);

    public void setGoodEnumValues(List<String> acceptedEnumStrings);

    public void setTrack(Integer trackId);

    public CheckSubject build();
}
