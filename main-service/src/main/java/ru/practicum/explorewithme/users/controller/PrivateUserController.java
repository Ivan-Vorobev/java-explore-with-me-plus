package ru.practicum.explorewithme.users.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/users")
public class PrivateUserController {

    private static final String USER_ID_PATH = "/{user-id}";
}
