package nl.tudelft.sem.template.example.domain.repositories;

import nl.tudelft.sem.template.model.Review;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@ComponentScan(basePackages = {"nl.tudelft.sem.template.example"})
public interface ReviewRepository extends JpaRepository<Review, Integer> {

}
