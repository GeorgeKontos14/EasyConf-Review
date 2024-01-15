package nl.tudelft.sem.template.example.domain.builder;


import java.util.List;
import lombok.Getter;

@Getter
public class CheckSubject {

    private final List<Object> inputParameters;
    private final Integer userId;

    private final List<Integer> paperIds;
    private final List<Integer> reviewIds;

    private final String enumString;
    private final List<String> acceptedEnumStrings;

    /**
     * class constructor for CheckSubject.
     *
     * @param inputParameters - list of input parameters of an arbitrary endpoint
     * @param userId - userId passed in as a parameter of an arbitrary endpoint
     * @param paperIds - list of paper ids that need to be checked for validity in the database
     * @param reviewIds- list of review ids that need to be checked for validity in the database
     * @param enumString - enum string object that needs to be verified among a list of valid enum values
     * @param acceptedEnumStrings - the enum values that enumString needs to be verified with
     */

    public CheckSubject(List<Object> inputParameters, Integer userId, List<Integer> paperIds, List<Integer> reviewIds,
                        String enumString, List<String> acceptedEnumStrings) {
        this.inputParameters = inputParameters;
        this.userId = userId;
        this.paperIds = paperIds;
        this.reviewIds = reviewIds;
        this.enumString = enumString;
        this.acceptedEnumStrings = acceptedEnumStrings;
    }
}
