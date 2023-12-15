package nl.tudelft.sem.template.example.repositories;

import nl.tudelft.sem.template.model.Paper;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaperRepository extends JpaRepository<Paper, Integer> {

}
