package nl.tudelft.sem.template.example.domain.responses;

import java.util.List;

public class SubmissionPaperIdsResponse {
    private List<Integer> submissionIds;

    public List<Integer> getSubmissionIds() {
        return submissionIds;
    }

    public void setSubmissionIds(List<Integer> submissionIds) {
        this.submissionIds = submissionIds;
    }
}
