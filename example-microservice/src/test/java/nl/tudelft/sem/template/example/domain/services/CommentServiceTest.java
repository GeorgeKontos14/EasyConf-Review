package nl.tudelft.sem.template.example.domain.services;

import nl.tudelft.sem.template.example.domain.models.PcChair;
import nl.tudelft.sem.template.example.domain.repositories.CommentRepository;
import nl.tudelft.sem.template.example.domain.repositories.PcChairReviewCommentRepository;
import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.PcChairReviewComment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    private CommentService commentService;
    private PcChairReviewCommentRepository pcChairReviewCommentRepository;
    private CommentRepository commentRepository;

    @BeforeEach
    void setup()
    {
        pcChairReviewCommentRepository = Mockito.mock(PcChairReviewCommentRepository.class);
        commentRepository = Mockito.mock(CommentRepository.class);
        commentService = new CommentService(commentRepository, pcChairReviewCommentRepository);
    }

    @Test
    void addCommentTest() {
        String text = "Comment1";
        int author = 12;
        int paper = 123;
        boolean confidence = false;
        Comment goodComment = new Comment();
        goodComment.setText(text);
        goodComment.setConfidential(true);
        goodComment.setAuthorId(author);
        goodComment.setPaperId(paper);
        goodComment.setConfidential(confidence);
        goodComment.setId(3);
        when(commentRepository.save(any())).thenReturn(goodComment);
        Comment comment = commentService.addComment(text, author, paper, confidence);
        verify(commentRepository, times(1)).save(any());
        assertThat(comment).isEqualTo(goodComment);
    }

    @Test
    void pcChairLeaveCommentOnReview() {
        Comment comment = new Comment();
        comment.setId(5);
        PcChairReviewComment pcChairReviewComment = new PcChairReviewComment();
        pcChairReviewComment.setReviewId(2);
        pcChairReviewComment.setId(5);
        when(pcChairReviewCommentRepository.save(any())).thenReturn(pcChairReviewComment);
        PcChairReviewComment obj = commentService.pcChairLeaveCommentOnReview(comment, 2);
        assertThat(obj).isEqualTo(pcChairReviewComment);

    }
}