package wasteManagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @PostMapping(path = "/register")
    public void registration(){
    }

    @GetMapping(path = "/login")
    public void login(){
    }

}