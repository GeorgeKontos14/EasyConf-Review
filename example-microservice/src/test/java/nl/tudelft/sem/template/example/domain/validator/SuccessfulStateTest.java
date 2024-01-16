package nl.tudelft.sem.template.example.domain.validator;

import static org.assertj.core.api.Assertions.assertThat;

import nl.tudelft.sem.template.example.domain.builder.CheckSubjectBuilder;
import org.junit.jupiter.api.Test;

public class SuccessfulStateTest {

    @Test
    public void handleTest() {
        SuccessfulState sut = new SuccessfulState();
        CheckSubjectBuilder builder = new CheckSubjectBuilder();
        try {
            assertThat(sut.handle(builder.build())).isTrue();
        } catch (ValidatorException ignored) {

        }
    }
}
