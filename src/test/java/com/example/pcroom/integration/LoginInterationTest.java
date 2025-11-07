package com.example.pcroom.integration;

import com.example.pcroom.domain.RemainTime;
import com.example.pcroom.domain.Role;
import com.example.pcroom.domain.User;
import com.example.pcroom.infrastructure.RemainTimeRepository;
import com.example.pcroom.infrastructure.UserRepository;
import com.example.pcroom.presentation.LoginRequestDto;
import com.example.pcroom.presentation.user.UserRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class LoginInterationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RemainTimeRepository remainTimeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 중복 확인")
    public void joinTest() throws Exception {

        UserRequestDto userRequestDto = new UserRequestDto("sskij", "1234", "seungwoo", "01082112923");

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("seungwoo"));

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 실패")
    public void loginTest() throws Exception {

        User user = new User("sskij", passwordEncoder.encode("1234"), "seungwoo", "01082112923", Role.USER);
        userRepository.save(user);

        RemainTime remainTime = new RemainTime(user);
        remainTime.addRemainTime(3600L);
        remainTimeRepository.save(remainTime);

        LoginRequestDto correctAccount = new LoginRequestDto("sskij", "1234", 1);
        LoginRequestDto wrongAccount1 = new LoginRequestDto("wrong", "1234", 2);
        LoginRequestDto wrongAccount2 = new LoginRequestDto("sskij", "wrong", 3);

        MvcResult result = mockMvc.perform(post("/login") // 로그인 성공
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correctAccount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("seungwoo"))
                .andDo(print())
                .andReturn();

        mockMvc.perform(post("/login") // username 틀린 경우
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongAccount2)))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/login") // password 틀린 경우
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongAccount1)))
                .andExpect(status().isUnauthorized());


    }
}
