package com.tc.training.smallFinance.controllers;

import com.tc.training.smallFinance.dtos.inputs.LoginInputDto;
import com.tc.training.smallFinance.dtos.outputs.LoginOutputDto;
import com.tc.training.smallFinance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @PutMapping("/updatePassword")
    public ResponseEntity updatePassword(@RequestParam String userName, @RequestParam  String password){
        userService.updatePassword(userName,password);
        return ResponseEntity.ok("password successfully changed");
    }

    @PutMapping("/uploadImage")
    public ResponseEntity uploadImage(@RequestParam MultipartFile file1,@RequestParam MultipartFile file2,@RequestParam MultipartFile file3, @RequestParam String userName) throws IOException {
        userService.uploadImage(file1,file2,file3,userName);
        return ResponseEntity.ok("password successfully changed");
    }

    @GetMapping(value = "/getImages",produces = MediaType.IMAGE_JPEG_VALUE )
    public @ResponseBody byte[] getImage(@RequestParam String userName){
        return userService.getImage(userName);
    }

    @PostMapping("/login")
    public LoginOutputDto login(@RequestBody LoginInputDto loginInputDto){
        return userService.login(loginInputDto);
    }



}
