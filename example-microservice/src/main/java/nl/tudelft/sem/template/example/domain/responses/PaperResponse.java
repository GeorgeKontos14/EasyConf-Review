package nl.tudelft.sem.template.example.domain.responses;

import java.util.List;

public class PaperResponse {
    String title;
    List<Integer> authors;

    Integer trackId;
    String _abstract;

    List<String> keywords;
    String replicationPackageLink;

    List<Integer> conflictsOfInterest;

    String paperDownloadLink;

    public PaperResponse(String title, List<Integer> authors, Integer trackId, String _abstract, List<String> keywords, String replicationPackageLink, List<Integer> conflictsOfInterest, String paperDownloadLink) {
        this.title = title;
        this.authors = authors;
        this.trackId = trackId;
        this._abstract = _abstract;
        this.keywords = keywords;
        this.replicationPackageLink = replicationPackageLink;
        this.conflictsOfInterest = conflictsOfInterest;
        this.paperDownloadLink = paperDownloadLink;
    }

    public String getTitle() {
        return title;
    }

    public String getAbstract() {
        return _abstract;
    }
}
