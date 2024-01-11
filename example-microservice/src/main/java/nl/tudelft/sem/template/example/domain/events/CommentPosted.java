package nl.tudelft.sem.template.example.domain.events;

import lombok.Getter;
import nl.tudelft.sem.template.model.Comment;

@Getter
public class CommentPosted {
    private final Comment comment;

    public CommentPosted(Comment comment) {
        this.comment = comment;
    }
}
