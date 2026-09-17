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
                "spring.datasource.url=jdbc:postgresql://localhost:5433/test2",
                "spring.datasource.username=test2",
                "spring.datasource.password=test2",
                "spring.jpa.hibernate.ddl-auto=create",
                "spring.jpa.show-sql=true"
        }
)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest_2 {
    private final EntityManager em;
    private final UserService service;
    @Test
    void testSaveUser() {
        UserDTO userDTO = makeUserDto("some@email2.com", "Пётр2", "Иванов2");

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
        service.saveUser(makeUserDto("some2@email.com", "Пётр2", "Иванов2"));
        service.saveUser(makeUserDto("test2@mail.com", "Сергей2", "Петров2"));
        service.saveUser(makeUserDto("example2@example.com", "Сергеев2", "Антон2"));

        TypedQuery<User> query = em.createQuery("Select u from User u ", User.class);
        List<User> users = query.getResultList();

        assertThat(users, hasSize(3));

        assertThat(users, hasItem(allOf(
                hasProperty("email", equalTo("some2@email.com")),
                hasProperty("firstName", equalTo("Пётр2")),
                hasProperty("lastName", equalTo("Иванов2")),
                hasProperty("state", equalTo(UserState.ACTIVE))
        )));

        assertThat(users, hasItem(allOf(
                hasProperty("email", equalTo("test2@mail.com")),
                hasProperty("firstName", equalTo("Сергей2")),
                hasProperty("lastName", equalTo("Петров2"))
        )));

        assertThat(users, hasItem(allOf(
                hasProperty("email", equalTo("example2@example.com")),
                hasProperty("firstName", equalTo("Сергеев2")),
                hasProperty("lastName", equalTo("Антон2"))
        )));
    }
}
