package nl.tudelft.sem.template.example.domain.validator;

import nl.tudelft.sem.template.example.domain.builder.CheckSubjectBuilder;
import nl.tudelft.sem.template.example.domain.services.PaperService;
import nl.tudelft.sem.template.example.domain.services.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DatabaseObjectValidatorTest {

    private CheckSubjectBuilder builder;
    private DatabaseObjectValidator sut;

    @BeforeEach
    public void setup() {
        PaperService paperService = Mockito.mock(PaperService.class);
        ReviewService reviewService = Mockito.mock(ReviewService.class);
        sut = new DatabaseObjectValidator(paperService, reviewService);
        builder = new CheckSubjectBuilder();
        Mockito.when(paperService.isExistingPaper(1)).thenReturn(false);
        Mockito.when(paperService.isExistingPaper(2)).thenReturn(true);
        Mockito.when(paperService.isExistingPaper(3)).thenReturn(false);
        Mockito.when(reviewService.existsReview(1)).thenReturn(false);
        Mockito.when(reviewService.existsReview(2)).thenReturn(true);
        Mockito.when(reviewService.existsReview(3)).thenReturn(false);
    }

    @Test
    public void handleExceptionTest() {
        builder.setPaperIds(Arrays.asList(1, 2));
        assertThrows(ValidatorException.class,
                () -> sut.handle(builder.build()));
        builder.setPaperIds(Arrays.asList(1, 3));
        builder.setReviewIds(Arrays.asList(1,2));
        assertThrows(ValidatorException.class,
                () -> sut.handle(builder.build()));
    }

    @Test
    public void handleOkTest() {
        try {
            assertThat(sut.handle(builder.build())).isTrue();
            ParameterValidator next = Mockito.mock(ParameterValidator.class);
            sut.setNext(next);
            assertThat(sut.handle(builder.build())).isFalse();
        } catch (ValidatorException e) {
            e.printStackTrace();
        }
    }

}
