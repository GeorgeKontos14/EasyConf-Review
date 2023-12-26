package nl.tudelft.sem.template.example.repositories;

import nl.tudelft.sem.template.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

}
