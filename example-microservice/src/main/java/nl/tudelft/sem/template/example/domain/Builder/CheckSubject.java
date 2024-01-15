package nl.tudelft.sem.template.example.domain.Builder;

import lombok.Getter;

import java.util.List;

@Getter
public class CheckSubject {

    private final List<Object> inputParameters;
    private final Integer userId;

    private final List<Integer> paperIds;
    private final List<Integer> reviewIds;

    private final String enumString;
    private final List<String> acceptedEnumStrings;

    public CheckSubject(List<Object> inputParameters, Integer userId, List<Integer> paperIds, List<Integer> reviewIds, String enumString, List<String> acceptedEnumStrings) {
        this.inputParameters = inputParameters;
        this.userId = userId;
        this.paperIds = paperIds;
        this.reviewIds = reviewIds;
        this.enumString = enumString;
        this.acceptedEnumStrings = acceptedEnumStrings;
    }
}
