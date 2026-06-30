package com.job.smarthire.controller;


/*
 * Annotations
 * class level  - @Controller(@Responsebody) or @Restcontroller
 * class/method level()  - @RequestMapping,("/user")
 * Method level(request types) -  @GetMapping("/getUser"),@PostMapping("/add"),@PutMapping,@DeleteMapping,@PathMapping
 * Method params(payload) - @RequestParam,@PathVariable,@RequestBody
 * validation - @valid,@validate
 * */

import com.job.smarthire.dtos.UserRequestDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    //save the user into database - POST
    //Update user - PUT
    //DELETE - DELETE
    //GET - fetchAll/fetchById/
    @PostMapping("/add")
    public String addUser(@RequestBody @Valid  UserRequestDto requestDto) {

        //validate the request   - validation on UserRequestDto
        //@NotNull
        //@NotBlank
        //@Email
        //@min @max

        System.out.println(requestDto);
        System.out.println("fetching email address from requestDTO");

        System.out.println("Saving the data into database.....");

        return "User added successfully";
    }

    @PutMapping("/update/{id}")
    public String updateUser(@RequestBody UserRequestDto requestDto, @PathVariable(name = "id") long userId) {

        System.out.println("updating the user " + userId);
        System.out.println(requestDto);

        return "updated successfully";
    }

    @DeleteMapping("/remove/{id}")
    public String removeUser(@PathVariable(name = "id") long userId){

        System.out.println("removing the user....");

       return "user removed successfully";
    }

    @GetMapping("/fetchAll")
    public String getAllUsers(){

        System.out.println("fetching all users.....");

        return "user list";
    }




    @GetMapping("/fetch/{id}")
    public String getUser(@PathVariable(name = "id") long userId){

        System.out.println("fetching the user ..... "+ userId);

        return "user details";
    }



}
