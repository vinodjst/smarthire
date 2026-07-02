package com.job.smarthire.controller;

import com.job.smarthire.dtos.UserRequestDto;
import com.job.smarthire.dtos.UserResponseDto;
import com.job.smarthire.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/add")
    public UserResponseDto addUser(@RequestBody @Valid UserRequestDto requestDto) {
        return userService.saveUser(requestDto);
    }

}
