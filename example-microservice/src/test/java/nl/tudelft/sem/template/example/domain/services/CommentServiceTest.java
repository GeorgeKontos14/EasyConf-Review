package nl.tudelft.sem.template.example.domain.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import nl.tudelft.sem.template.example.domain.repositories.CommentRepository;
import nl.tudelft.sem.template.example.domain.repositories.PcChairReviewCommentRepository;
import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.PcChairReviewComment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


class CommentServiceTest {

    private CommentService commentService;
    private PcChairReviewCommentRepository pcChairReviewCommentRepository;
    private CommentRepository commentRepository;

    private ReviewService reviewService;

    @BeforeEach
    void setup() {
        pcChairReviewCommentRepository = Mockito.mock(PcChairReviewCommentRepository.class);
        commentRepository = Mockito.mock(CommentRepository.class);
        reviewService = Mockito.mock(ReviewService.class);
        commentService = new CommentService(commentRepository, pcChairReviewCommentRepository, reviewService);
    }

    Comment buildComment(Integer id, String text, int author, int paper, boolean confidencial) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setText(text);
        comment.setAuthorId(author);
        comment.setPaperId(paper);
        comment.setConfidential(confidencial);
        return comment;
    }

    @Test
    void addCommentTest() {
        Comment goodComment = buildComment(null, "Comment1", 12, 123, false);
        when(commentRepository.save(any())).thenReturn(goodComment);
        Comment comment = commentService.addComment("Comment1", 12, 123, false);
        verify(commentRepository, times(1)).save(goodComment);
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
        Mockito.verify(pcChairReviewCommentRepository).save(pcChairReviewComment);
    }

    @Test
    void readOtherComments() {
        Comment c1 = buildComment(1, "wow", 12, 1, true);
        Comment c2 = buildComment(2, "hello", 12, 2, false);
        Comment c3 = buildComment(3, "zoe", 11, 1, true);
        Mockito.when(reviewService.findAllPapersByReviewerId(anyInt())).thenReturn(List.of(1, 2));
        Mockito.when(commentRepository.findCommentByPaperId(1)).thenReturn(List.of(c1, c3));
        Mockito.when(commentRepository.findCommentByPaperId(2)).thenReturn(List.of(c2));
        List<Comment> ans = commentService.readOtherComments(1);
        assertThat(ans).isEqualTo(List.of(c1, c3, c2));
    }
}