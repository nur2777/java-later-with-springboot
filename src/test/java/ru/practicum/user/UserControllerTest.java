package ru.practicum.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private UserDTO userDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        userDto = new UserDTO(
                1L,
                "john.doe@mail.com",
                "John",
                "Doe",
                "2022.07.03 19:55:00",
                UserState.ACTIVE
        );
    }

    @Test
    void saveNewUser() throws Exception {
        when(userService.saveUser(any()))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.firstName", is(userDto.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(userDto.getLastName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void getAllusers() throws Exception {
        List<UserDTO> userDTOs = new java.util.ArrayList<>(List.of(userDto));
        userDTOs.add(new UserDTO(
                2L,
                "sam.toor@mail.com",
                "Sam",
                "toor",
                "2022.06.23 12:15:30",
                UserState.ACTIVE));

        userDTOs.add(new UserDTO(
                3L,
                "Ralf.boom@mail.com",
                "Ralf",
                "boom",
                "2022.05.27 17:17:30",
                UserState.ACTIVE));
        when(userService.getAllUsers()).thenReturn(userDTOs);

        mvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(3)))  // проверяем размер массива
                .andExpect(jsonPath("$[0].id", is(userDTOs.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].firstName", is(userDTOs.get(0).getFirstName())))
                .andExpect(jsonPath("$[0].lastName", is(userDTOs.get(0).getLastName())))
                .andExpect(jsonPath("$[0].email", is(userDTOs.get(0).getEmail())))
                .andExpect(jsonPath("$[1].id", is(userDTOs.get(1).getId()), Long.class))
                .andExpect(jsonPath("$[1].firstName", is(userDTOs.get(1).getFirstName())))
                .andExpect(jsonPath("$[1].lastName", is(userDTOs.get(1).getLastName())))
                .andExpect(jsonPath("$[1].email", is(userDTOs.get(1).getEmail())))
                .andExpect(jsonPath("$[2].id", is(userDTOs.get(2).getId()), Long.class))
                .andExpect(jsonPath("$[2].firstName", is(userDTOs.get(2).getFirstName())))
                .andExpect(jsonPath("$[2].lastName", is(userDTOs.get(2).getLastName())))
                .andExpect(jsonPath("$[2].email", is(userDTOs.get(2).getEmail())));
    }
}