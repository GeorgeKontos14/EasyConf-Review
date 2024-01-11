package nl.tudelft.sem.template.example.domain.repositories;

import nl.tudelft.sem.template.example.domain.models.PcChair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PcChairRepository extends JpaRepository<PcChair, Integer> {
}
