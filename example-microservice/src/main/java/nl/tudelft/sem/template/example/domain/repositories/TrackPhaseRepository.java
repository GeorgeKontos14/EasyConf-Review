package nl.tudelft.sem.template.example.domain.repositories;

import nl.tudelft.sem.template.example.domain.models.TrackPhase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackPhaseRepository extends JpaRepository<TrackPhase, Integer> {
}
