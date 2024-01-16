package nl.tudelft.sem.template.example.domain.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import nl.tudelft.sem.template.example.domain.builder.CheckSubject;
import nl.tudelft.sem.template.example.domain.builder.CheckSubjectBuilder;
import nl.tudelft.sem.template.example.domain.services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class UserValidatorTest {

    @Test
    public void userValidatorTest() {
        UserService userService = Mockito.mock(UserService.class);
        UserValidator sut = new UserValidator(userService);
        CheckSubjectBuilder builder = new CheckSubjectBuilder();
        try {
            assertThat(sut.handle(builder.build())).isTrue();
            BaseValidator next = Mockito.mock(BaseValidator.class);
            sut.setNext(next);
            Mockito.when(next.checkNext(any(CheckSubject.class))).thenReturn(false);
            assertThat(sut.handle(builder.build())).isFalse();
            Mockito.when(userService.validateUser(1)).thenReturn(true);
            builder.setUserId(1);
            assertThat(sut.handle(builder.build())).isFalse();
            sut.setNext(null);
            assertThat(sut.handle(builder.build())).isTrue();
        } catch (ValidatorException ignored) {

        }
    }

}
