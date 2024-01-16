package nl.tudelft.sem.template.example.domain.services;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.example.domain.repositories.CommentRepository;
import nl.tudelft.sem.template.example.domain.repositories.PcChairReviewCommentRepository;
import nl.tudelft.sem.template.model.Comment;
import nl.tudelft.sem.template.model.Paper;
import nl.tudelft.sem.template.model.PcChairReviewComment;


public class CommentService {

    CommentRepository commentRepository;
    PcChairReviewCommentRepository pcChairReviewCommentRepository;
    ReviewService reviewService;

    /**
     * Comment service class constructor.
     *
     * @param commentRepository              reference to a CommentRepository
     * @param pcChairReviewCommentRepository reference to a PcChairReviewCommentRepository
     * @param reviewService                  reference to the reviewService
     */

    CommentService(CommentRepository commentRepository, PcChairReviewCommentRepository pcChairReviewCommentRepository,
                   ReviewService reviewService) {
        this.commentRepository = commentRepository;
        this.pcChairReviewCommentRepository = pcChairReviewCommentRepository;
        this.reviewService = reviewService;
    }

    /**
     * method for creating a simple comment object and saving it to the database.
     *
     * @param commentText    the text this comment should have
     * @param userId         the id of the comment author
     * @param paperId        the id of the paper this comment is left for, and null if inexistent
     * @param isConfidential the confidentiality status of this comment
     * @return a Comment object that is saved to the database
     */
    public Comment addComment(String commentText, Integer userId, Integer paperId, Boolean isConfidential) {
        Comment comment = new Comment();
        comment.setAuthorId(userId);
        comment.setText(commentText);
        comment.setPaperId(paperId);
        comment.setConfidential(isConfidential);
        return commentRepository.save(comment);
    }

    /**
     * Method for when a PcChair leaves a comment on a Review.
     *
     * @param comment  - a Comment object that is left by the PcChair
     * @param reviewId - the reviewId for the review this comment is left on
     * @return a PcChairReviewComment that is saved to the database
     */
    public PcChairReviewComment pcChairLeaveCommentOnReview(Comment comment, Integer reviewId) {
        PcChairReviewComment pcChairReviewComment = new PcChairReviewComment();
        pcChairReviewComment.setId(comment.getId());
        pcChairReviewComment.setReviewId(reviewId);
        return pcChairReviewCommentRepository.save(pcChairReviewComment);
    }

    public List<Comment> getCommentsForPaperId(int paperId) {
        return commentRepository.findCommentByPaperId(paperId);
    }

    /**
     * Method that allows a reviewer to see all the comments left on the papers
     * they have left a review on.
     *
     * @param reviewerId - an arbitrary reviewer Id
     * @return the List of Comment objects this reviewer can see
     */
    public List<Comment> readOtherComments(int reviewerId) {
        List<Integer> papersOfReviewer = reviewService.findAllPapersByReviewerId(reviewerId);
        List<Comment> totalCommentsReviewerCanSee = new ArrayList<>();
        for (int paperId : papersOfReviewer) {
            List<Comment> commentsForPaper = getCommentsForPaperId(paperId);
            totalCommentsReviewerCanSee.addAll(commentsForPaper);
        }
        return totalCommentsReviewerCanSee;
    }

}
