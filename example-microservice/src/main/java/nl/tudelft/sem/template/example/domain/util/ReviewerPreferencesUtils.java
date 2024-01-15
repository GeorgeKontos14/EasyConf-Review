package nl.tudelft.sem.template.example.domain.util;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.template.example.domain.models.PreferenceEntity;
import nl.tudelft.sem.template.model.ReviewerPreferences;


public class RevPreftils {

    /**
     * Converts a list of Preference Entities to ReviewerPreferences objects.
     *
     * @param entities the list of entities.
     * @return the list of objects.
     */
    public static List<ReviewerPreferences> convert(List<PreferenceEntity> entities) {
        List<ReviewerPreferences> result = new ArrayList<>();
        for (PreferenceEntity e : entities) {
            result.add(e.toPreferences());
        }
        return result;
    }
}
