package ru.practicum.user;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
//public class FakeUserRepository implements UserRepository {
public class FakeUserRepository {
    private static final List<User> FAKE_USERS = createManyFakeUsers(3);

    //@Override
    public List<User> findAll() {
        return FAKE_USERS;
    }

    //@Override
    public User save(User user) {
        throw new UnsupportedOperationException("Метод save() ещё не готов");
    }

    private static List<User> createManyFakeUsers(int count) {
        List<User> fakeUsers = new ArrayList<>();
        for (long id = 1; id <= count; id++) {
            fakeUsers.add(createFakeUser(id));
        }
        return Collections.unmodifiableList(fakeUsers);
    }

    private static User createFakeUser(long id) {
        User fakeUser = new User();
        fakeUser.setId(id);
        fakeUser.setEmail("mail" + id + "@example.com");
        fakeUser.setFirstName("Akakiy ");
        fakeUser.setLastName("Akakievich #" + id);
        return fakeUser;
    }
}