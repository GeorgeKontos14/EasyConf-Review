package nl.tudelft.sem.template.example.domain.builder;

import java.util.List;

public class CheckSubjectBuilder {

    private List<Object> inputParameters;
    private Integer userId;
    private List<Integer> paperIds;
    private List<Integer> reviewIds;
    private String enumString;
    private List<String> acceptedEnumStrings;

    public void setInputParameters(List<Object> inputParameters) {
        this.inputParameters = inputParameters;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setPaperIds(List<Integer> paperIds) {
        this.paperIds = paperIds;
    }

    public void setReviewIds(List<Integer> reviewIds) {
        this.reviewIds = reviewIds;
    }

    public CheckSubject build() {
        return new CheckSubject(inputParameters, userId, paperIds, reviewIds, enumString, acceptedEnumStrings);
    }

    public void setEnumValue(String status) {
        this.enumString = status;
    }

    public void setGoodEnumValues(List<String> acceptedEnumStrings) {
        this.acceptedEnumStrings = acceptedEnumStrings;
    }
}
