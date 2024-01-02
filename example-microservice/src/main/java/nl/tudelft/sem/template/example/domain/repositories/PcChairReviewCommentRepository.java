package nl.tudelft.sem.template.example.domain.repositories;

import nl.tudelft.sem.template.model.PcChairReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PcChairReviewCommentRepository extends JpaRepository<PcChairReviewComment, Integer> {
}
