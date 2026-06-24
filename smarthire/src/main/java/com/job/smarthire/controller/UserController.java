package com.job.smarthire.controller;


/*
* Annotations
* class level  - @Controller(@Responsebody) or @Restcontroller
* class/method level()  - @RequestMapping,("/user")
* Method level(request types) -  @GetMapping("/getUser"),@PostMapping("/add"),@PutMapping,@DeleteMapping,@PathMapping
* Method params(payload) - @RequestParam,@PathVariable,@RequestBody
* validation - @valid,@validate
* */

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    //save the user into database
    @PostMapping("/add")
    public String addUser(){

        System.out.println("Saving the data into database.....");

        return "User added successfully";
    }

}
