package nl.tudelft.sem.template.example.domain.repositories;

import nl.tudelft.sem.template.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

}
