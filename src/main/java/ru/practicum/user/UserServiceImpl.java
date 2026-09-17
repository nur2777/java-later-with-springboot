package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMap userMap;
    @Override
    public List<UserDTO> getAllUsers() {
        return repository.findAll().stream().map(userMap).toList();
    }

    @Override
    @Transactional
    public UserDTO saveUser(UserDTO userDTO) {
        User user = userMap.toUser(userDTO);
        return userMap.apply(repository.save(user));
    }
}