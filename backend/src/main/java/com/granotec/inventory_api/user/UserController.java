package com.granotec.inventory_api.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

//    private final UserRepository userRepository;
//
//    @GetMapping
//    public List<UserResponse> changePassoword(){
//        return userRepository.findAll()
//                .stream()
//                .map(user -> new UserResponse(user.getName(),user.getEmail()))
//                .toList();
//    }
}
