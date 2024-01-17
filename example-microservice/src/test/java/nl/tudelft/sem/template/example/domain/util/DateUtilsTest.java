package nl.tudelft.sem.template.example.domain.util;

import static nl.tudelft.sem.template.example.domain.util.DateUtils.advanceOneWeek;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class DateUtilsTest {

    @Test
    public void advanceOneWeekTest() {
        assertThat(advanceOneWeek("2024-12-31")).isEqualTo("2025-01-07");
    }
}
