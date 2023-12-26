package nl.tudelft.sem.template.example.repositories;

import nl.tudelft.sem.template.example.domain.models.PcChair;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PcChairRepository extends JpaRepository<PcChair, Integer> {
}
