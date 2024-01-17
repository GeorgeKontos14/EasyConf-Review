package nl.tudelft.sem.template.example.domain.validator;

import static org.assertj.core.api.Assertions.assertThat;

import nl.tudelft.sem.template.example.domain.builder.CheckSubjectBuilder;
import nl.tudelft.sem.template.example.domain.services.PaperService;
import nl.tudelft.sem.template.example.domain.services.ReviewService;
import nl.tudelft.sem.template.example.domain.services.TrackPhaseService;
import nl.tudelft.sem.template.example.domain.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;

public class ChainManagerTest {
    private CheckSubjectBuilder builder;
    private ChainManager sut;

    @BeforeEach
    public void setup() {
        TrackPhaseService trackPhaseService = Mockito.mock(TrackPhaseService.class);
        UserService userService = Mockito.mock(UserService.class);
        ReviewService reviewService = Mockito.mock(ReviewService.class);
        PaperService paperService = Mockito.mock(PaperService.class);
        sut = new ChainManager(userService, paperService, reviewService, trackPhaseService);
        builder = new CheckSubjectBuilder();
    }

    @Test
    public void shouldAddParameterTest() {
        assertThat(sut.shouldAddParameterValidator(builder.build())).isFalse();
        builder.setUserId(1);
        assertThat(sut.shouldAddParameterValidator(builder.build())).isTrue();
        builder = new CheckSubjectBuilder();
        builder.setTrack(1);
        assertThat(sut.shouldAddParameterValidator(builder.build())).isTrue();
        builder = new CheckSubjectBuilder();
        builder.setGoodEnumValues(Arrays.asList("a", "b"));
        assertThat(sut.shouldAddParameterValidator(builder.build())).isTrue();
        builder = new CheckSubjectBuilder();
        builder.setEnumValue("a");
        assertThat(sut.shouldAddParameterValidator(builder.build())).isTrue();
    }

    @Test
    public void shouldAddUserValidatorTest() {
        assertThat(sut.shouldAddUserValidator(builder.build())).isFalse();
        builder.setUserId(1);
        assertThat(sut.shouldAddParameterValidator(builder.build())).isTrue();
    }

    @Test
    public void shouldAddDatabaseObjectValidatorTest() {
        assertThat(sut.shouldAddDatabaseObjectValidator(builder.build())).isFalse();
        builder.setPaperIds(Arrays.asList(1,2,3));
        assertThat(sut.shouldAddDatabaseObjectValidator(builder.build())).isTrue();
    }


}
