package nl.tudelft.sem.template.example.domain.repositories;

import nl.tudelft.sem.template.example.domain.models.Reviewer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewerRepository extends JpaRepository<Reviewer, Integer> {
}
