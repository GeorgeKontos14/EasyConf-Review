package nl.tudelft.sem.template.example.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;


@Entity
@Table(name = "papers")
public class Paper {

    @Getter
    @Id
    private Integer id;

    @Getter
    @ElementCollection
    @CollectionTable(name = "listOfAuthors")
    private List<Integer> authors;

    /**
     * The final verdict on the paper, indicating whether it is accepted or rejected by the committee.
     */
    @Getter
    public enum FinalVerdictEnum {
        ACCEPTED("Accepted"),

        REJECTED("Rejected");

        private String value;

        FinalVerdictEnum(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        /**
         * Returns the FinalVerdictEnum result based on an equivalent String.
         *
         * @param value - a string which can be either 'Accepted' or 'Rejected'
         * @return the FinalVerdictEnum ACCEPTED or REJECTED, corresponding to the input
         */
        public static FinalVerdictEnum fromValue(String value) {
            for (FinalVerdictEnum b : FinalVerdictEnum.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }

    @Enumerated(EnumType.STRING)
    private FinalVerdictEnum finalVerdict;

    public Paper id(Integer id) {
        this.id = id;
        return this;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Paper authors(List<Integer> authors) {
        this.authors = authors;
        return this;
    }

    /**
     * Adds an author id to the list of this paper's authors.
     *
     * @param authorsItem - the author id we want to add
     * @return the updated Paper object
     */
    public Paper addAuthorsItem(Integer authorsItem) {
        if (this.authors == null) {
            this.authors = new ArrayList<>();
        }
        this.authors.add(authorsItem);
        return this;
    }

    public void setAuthors(List<Integer> authors) {
        this.authors = authors;
    }

    public Paper finalVerdict(FinalVerdictEnum finalVerdict) {
        this.finalVerdict = finalVerdict;
        return this;
    }

    public FinalVerdictEnum getFinalVerdict() {
        return finalVerdict;
    }

    public void setFinalVerdict(FinalVerdictEnum finalVerdict) {
        this.finalVerdict = finalVerdict;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Paper paper = (Paper) o;
        return Objects.equals(this.id, paper.id)
                && Objects.equals(this.authors, paper.authors)
                && Objects.equals(this.finalVerdict, paper.finalVerdict);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, authors, finalVerdict);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class PaperEntity {\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    authors: ").append(toIndentedString(authors)).append("\n");
        sb.append("    finalVerdict: ").append(toIndentedString(finalVerdict)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

