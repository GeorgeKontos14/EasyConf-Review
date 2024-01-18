package nl.tudelft.sem.template.example.domain.responses;

import java.util.List;
import lombok.Getter;

@Getter
public class PaperResponse {
    String title;
    List<Integer> authors;

    Integer trackId;
    String abstractString;

    List<String> keywords;
    String replicationPackageLink;
    List<Integer> conflictsOfInterest;

    String paperDownloadLink;

    /**
     * Constructor of the PaperResponse class.
     *
     * @param title of the paper
     * @param authors of the paper
     * @param trackId of the paper
     * @param abstractString of the paper
     * @param keywords of the paper
     * @param replicationPackageLink of the paper
     * @param conflictsOfInterest of the paper
     * @param paperDownloadLink of the paper
     */
    public PaperResponse(String title, List<Integer> authors, Integer trackId, String abstractString,
                         List<String> keywords, String replicationPackageLink, List<Integer> conflictsOfInterest,
                         String paperDownloadLink) {
        this.title = title;
        this.authors = authors;
        this.trackId = trackId;
        this.abstractString = abstractString;
        this.keywords = keywords;
        this.replicationPackageLink = replicationPackageLink;
        this.conflictsOfInterest = conflictsOfInterest;
        this.paperDownloadLink = paperDownloadLink;
    }

    public String getAbstract() {
        return abstractString;
    }
}
