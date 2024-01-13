package nl.tudelft.sem.template.example.domain.services;

import nl.tudelft.sem.template.example.domain.models.TrackPhase;
import nl.tudelft.sem.template.example.domain.repositories.TrackPhaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TrackPhaseService {
    private transient final TrackPhaseRepository trackPhaseRepository;

    public TrackPhaseService(TrackPhaseRepository trackPhaseRepository) {
        this.trackPhaseRepository = trackPhaseRepository;
    }

    /**
     * Saves a track phase object to the repository.
     * @param trackPhase the track phase object.
     */
    public void saveTrackPhase(TrackPhase trackPhase) {
        trackPhaseRepository.save(trackPhase);
    }

    /**
     * Method that gets all the papers of a track.
     * @param trackId the ID of the track.
     * @param restTemplate the rest Template.
     * @return the list of IDs of papers on the given track.
     */
    public Optional<List<Integer>> getTrackPapers(int trackId, RestTemplate restTemplate) {
        String submissionsUri = "localhost:8082/tracks/" + trackId + "/submissions";
        IntegerList response;
        try {
            response = restTemplate.getForObject(submissionsUri, IntegerList.class);
        } catch (Exception e) {
            return Optional.empty();
        }
        if (response == null)
            return Optional.empty();
        return Optional.ofNullable(response.ints);
    }

    //Had to create this class to make the response work.
    static class IntegerList {
        List<Integer> ints;

        public IntegerList() {
            ints = new ArrayList<>();
        }

        public IntegerList(List<Integer> list) {
            this.ints = list;
        }
    }

}
