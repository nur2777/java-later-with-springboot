package ru.practicum.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.Later;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        classes = Later.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "spring.datasource.url=jdbc:postgresql://localhost:5432/test",
                "spring.datasource.username=test",
                "spring.datasource.password=test",
                "spring.jpa.hibernate.ddl-auto=create",
                "spring.jpa.show-sql=true"
        }
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {
    private final EntityManager em;
    private final UserService service;
    @Test
    void testSaveUser() {
        UserDTO userDTO = makeUserDto("some@email.com", "Пётр", "Иванов");

        service.saveUser(userDTO);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDTO.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getFirstName(), equalTo(userDTO.getFirstName()));
        assertThat(user.getLastName(), equalTo(userDTO.getLastName()));
        assertThat(user.getEmail(), equalTo(userDTO.getEmail()));
        assertThat(user.getState(), equalTo(userDTO.getState()));
        assertThat(user.getRegistrationDate(), notNullValue());
    }

    private UserDTO makeUserDto(String email, String firstName, String lastName) {
        UserDTO dto = new UserDTO();
        dto.setEmail(email);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setState(UserState.ACTIVE);

        return dto;
    }

    @Test
    void testGetAllUsers() {
        service.saveUser(makeUserDto("some@email.com", "Пётр", "Иванов"));
        service.saveUser(makeUserDto("test@mail.com", "Сергей", "Петров"));
        service.saveUser(makeUserDto("example@example.com", "Сергеев", "Антон"));

        TypedQuery<User> query = em.createQuery("Select u from User u ", User.class);
        List<User> users = query.getResultList();

        assertThat(users, hasSize(3));

        assertThat(users, hasItem(allOf(
                hasProperty("email", equalTo("some@email.com")),
                hasProperty("firstName", equalTo("Пётр")),
                hasProperty("lastName", equalTo("Иванов")),
                hasProperty("state", equalTo(UserState.ACTIVE))
        )));

        assertThat(users, hasItem(allOf(
                hasProperty("email", equalTo("test@mail.com")),
                hasProperty("firstName", equalTo("Сергей")),
                hasProperty("lastName", equalTo("Петров"))
        )));

        assertThat(users, hasItem(allOf(
                hasProperty("email", equalTo("example@example.com")),
                hasProperty("firstName", equalTo("Сергеев")),
                hasProperty("lastName", equalTo("Антон"))
        )));
    }
}
