package com.job.smarthire.service;

import com.job.smarthire.dtos.UserRequestDto;
import com.job.smarthire.dtos.UserResponseDto;
import com.job.smarthire.entity.UserEntity;
import com.job.smarthire.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    //@Autowire
    //database/repository
    //what is the depedency injection

    @Autowired
    UserRepository userRepository;

    public UserResponseDto saveUser(UserRequestDto request) {

        System.out.println("UserService::saveUser -- saving user");
        //mapping request DTO -> ENTITY
        UserEntity userEntity = new UserEntity();
        userEntity.setName(request.getName());
        userEntity.setEmail(request.getEmail());
        userEntity.setMobile(request.getMobile());
        userEntity.setPassword(request.getPassword());

        //save(Entity t) method -> from CrudRepository
        UserEntity save = userRepository.save(userEntity);

        //map ENTITY -> response DTO
        UserResponseDto userResponseDto = new UserResponseDto();

        userResponseDto.setId(save.getId());
        userResponseDto.setName(save.getName());
        userResponseDto.setEmail(save.getEmail());
        userResponseDto.setMobile(save.getMobile());
        userResponseDto.setPassword(save.getPassword());

        return userResponseDto;
    }


}
