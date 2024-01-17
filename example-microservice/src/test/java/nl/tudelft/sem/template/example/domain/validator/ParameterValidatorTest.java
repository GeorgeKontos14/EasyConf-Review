package nl.tudelft.sem.template.example.domain.validator;

import static org.assertj.core.api.Assertions.assertThat;

import nl.tudelft.sem.template.example.domain.builder.CheckSubjectBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;

public class ParameterValidatorTest {
    private ParameterValidator sut;
    private CheckSubjectBuilder builder;

    @BeforeEach
    public void setup() {
        sut = new ParameterValidator();
        builder = new CheckSubjectBuilder();
    }

    @Test
    public void handleExceptionTest() {
        builder.setInputParameters(Arrays.asList(1,null));
        Assertions.assertThrows(ValidatorException.class,
                () -> sut.handle(builder.build()));
        builder = new CheckSubjectBuilder();
        builder.setGoodEnumValues(Arrays.asList("b", "c"));
        builder.setEnumValue("a");
        Assertions.assertThrows(ValidatorException.class,
                () -> sut.handle(builder.build()));
        builder = new CheckSubjectBuilder();
        builder.setUserId(-1);
        Assertions.assertThrows(ValidatorException.class,
                () -> sut.handle(builder.build()));
        builder = new CheckSubjectBuilder();
        builder.setTrack(-1);
        Assertions.assertThrows(ValidatorException.class,
                () -> sut.handle(builder.build()));
    }

    @Test
    public void handleOkTest() {
        try {
            assertThat(sut.handle(builder.build())).isTrue();
        } catch (ValidatorException ignored) {

        }
        ParameterValidator next = Mockito.mock(ParameterValidator.class);
        sut.setNext(next);
        try {
            Mockito.when(next.checkNext(builder.build())).thenReturn(false);
            assertThat(sut.handle(builder.build())).isFalse();
        } catch (ValidatorException ignored) {

        }
    }

    @Test
    public void areInputParametersValidNullTest() {
        assertThat(sut.areInputParametersValid(builder.build())).isTrue();
    }

    @Test
    public void isEnumStringValidNullTest() {
        builder.setGoodEnumValues(Arrays.asList("1", "2"));
        assertThat(sut.isEnumStringValid(builder.build())).isTrue();
    }

    @Test
    public void isTrackIdPositiveTest() {
        assertThat(sut.isTrackIdPositive(builder.build())).isTrue();
        builder.setTrack(1);
        assertThat(sut.isTrackIdPositive(builder.build())).isTrue();
        builder.setTrack(0);
        assertThat(sut.isTrackIdPositive(builder.build())).isTrue();
        builder.setTrack(-1);
        assertThat(sut.isTrackIdPositive(builder.build())).isFalse();
    }
}
