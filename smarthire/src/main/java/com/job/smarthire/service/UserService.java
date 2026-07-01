package com.job.smarthire.service;

import com.job.smarthire.dtos.UserRequestDto;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    //@Autowire
    //database/repository

    public void saveUser(UserRequestDto request){


        System.out.println("Save method from service layer");
    }




}
