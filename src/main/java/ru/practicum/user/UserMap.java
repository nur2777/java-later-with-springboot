package ru.practicum.user;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

/**
 * Класс маппер наследуемый от java.util.function.Function
 */
@Component
public class UserMap implements Function<User, UserDTO> {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());
    @Override
    public UserDTO apply(User user) {
        if (user == null) {
            return null;
        }
        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .registrationDate(formatter.format(user.getRegistrationDate()))
                .state(user.getState())
                .build();
    }

    // Обратный маппинг
    public User toUser(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        };

        User user = new User();
        user.setId(userDTO.getId());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setState(userDTO.getState());
        if (userDTO.getRegistrationDate() != null) {
            LocalDateTime localDateTime = LocalDateTime.parse(userDTO.getRegistrationDate(), formatter);
            ZoneId zoneId = ZoneId.systemDefault();
            Instant instant = localDateTime.atZone(zoneId).toInstant();
            user.setRegistrationDate(instant);
        }
        return user;
    }
}
