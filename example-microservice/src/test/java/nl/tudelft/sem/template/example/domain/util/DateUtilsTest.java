package nl.tudelft.sem.template.example.domain.util;

import org.junit.jupiter.api.Test;

import static nl.tudelft.sem.template.example.domain.util.DateUtils.advanceOneWeek;
import static org.assertj.core.api.Assertions.assertThat;

public class DateUtilsTest {

    @Test
    public void advanceOneWeekTest() {
        assertThat(advanceOneWeek("2024-12-31")).isEqualTo("2025-01-07");
    }
}
