package com.job.smarthire.controller;

/*
 * Annotations
 * class level  - @Controller(@Responsebody) or @Restcontroller   -- DONE
 * class/method level()  - @RequestMapping,("/user")  -- DONE
 * Method level(request types) -  @GetMapping("/getUser"),@PostMapping("/add"),@PutMapping,@DeleteMapping,@PathMapping(x)
 * Method params(payload) - @RequestParam(x),@PathVariable,@RequestBody
 * validation - @valid,@validate
 * */
import com.job.smarthire.dtos.UserRequestDto;
import com.job.smarthire.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {


    @Autowired    //inject the object
    UserService userService; //Dependency injection


    @PostMapping("/add")
    public String addUser(@RequestBody @Valid UserRequestDto requestDto) {

        userService.saveUser(requestDto);
        return "User added successfully";
    }

    @PutMapping("/update/{id}")
    public String updateUser(@RequestBody UserRequestDto requestDto, @PathVariable(name = "id") long userId) {

        System.out.println("updating the user " + userId);
        System.out.println(requestDto);

        return "updated successfully";
    }

    @DeleteMapping("/remove/{id}")
    public String removeUser(@PathVariable(name = "id") long userId) {

        System.out.println("removing the user....");

        return "user removed successfully";
    }

    @GetMapping("/fetchAll")
    public String getAllUsers() {

        System.out.println("fetching all users.....");

        return "user list is here";
    }


    @GetMapping("/fetch/{id}")
    public String getUser(@PathVariable(name = "id") long userId) {

        System.out.println("fetching the user ..... " + userId);

        return "user details";
    }


}
