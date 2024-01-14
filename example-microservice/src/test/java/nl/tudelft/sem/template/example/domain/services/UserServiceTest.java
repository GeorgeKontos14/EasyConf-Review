package nl.tudelft.sem.template.example.domain.services;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
public class UserServiceTest {
    private UserService sut;

    @Test
    public void validateUserTest() {
        sut = new UserService();
        assertThat(sut.validateUser(-1)).isFalse();
        assertThat(sut.validateUser(1)).isTrue();
        assertThat(sut.validateUser(0)).isTrue();
    }
}
