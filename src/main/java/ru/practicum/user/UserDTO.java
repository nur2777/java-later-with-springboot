package ru.practicum.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    /**
     * Идентификатор пользователя
     */
    private Long id;
    /**
     * Имя пользователя
     */
    private String firstName;
    /**
     * Фамилия пользователя
     */
    private String lastName;
    /**
     * Емейл пользователя
     */
    private String email;
    /**
     * Дата регистрации в формате yyyy.MM.dd, hh:mm:ss
     */
    private String registrationDate;
    /**
     * Статус пользователя
     */
    private UserState state;

//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
//    private LocalDate dateOfBirth;
}