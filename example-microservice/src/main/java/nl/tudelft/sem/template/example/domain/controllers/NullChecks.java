package nl.tudelft.sem.template.example.domain.controllers;

import java.util.List;
import nl.tudelft.sem.template.model.Review;

public class NullChecks {
    public static boolean nullCheck(Integer id) {
        return id == null || id < 0;
    }

    public static boolean nullCheck(Integer id1, Integer id2) {
        return nullCheck(id1) || nullCheck(id2);
    }

    public static boolean nullCheck(Integer id, Object o) {
        return nullCheck(id) || o == null;
    }

    public static boolean nullCheck(Integer id1, Integer id2, List<Review> list) {
        return nullCheck(id1, id2) || list == null || list.isEmpty();
    }

    public static boolean nullCheck(Integer id1, Integer id2, Object o) {
        return nullCheck(id1, id2) || o == null;
    }


}
