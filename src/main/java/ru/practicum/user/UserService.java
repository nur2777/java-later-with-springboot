package ru.practicum.user;

import java.util.List;

public interface UserService {
    List<UserDTO> getAllUsers();
    UserDTO saveUser(UserDTO userDTO);
}