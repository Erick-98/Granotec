package com.granotec.inventory_api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
    /*
    NO TOMAR EN CUENTA ESTE ARCHIVO
    * */
    @GetMapping("/ping")
    public String ping(){
        return "pong";
    }

}
