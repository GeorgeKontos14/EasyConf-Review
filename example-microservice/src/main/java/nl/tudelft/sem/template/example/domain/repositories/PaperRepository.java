package nl.tudelft.sem.template.example.domain.repositories;

import nl.tudelft.sem.template.model.Paper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PaperRepository extends JpaRepository<Paper, Integer> {

}
