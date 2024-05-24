package com.example.registerapp.controller;


import com.example.registerapp.dto.*;
import com.example.registerapp.entity.User;
import com.example.registerapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RequestDto requestDto){
            return new ResponseEntity<>(userService.register(requestDto), HttpStatus.OK);
    }
//    @PutMapping("/verify-account")
//    public ResponseEntity<String> verifyAccount(@RequestParam String email, @RequestParam String otp){
//        return new ResponseEntity<>(userService.verifyAccount(email,otp), HttpStatus.OK);
//    }
@PutMapping("/verify-account")
public ResponseEntity<String> verifyAccount(@RequestBody OtpRequest otpRequest){
    boolean isVerified = userService.verifyOtp(otpRequest.getEmail(), otpRequest.getOtp());
    if (isVerified) {
        return new ResponseEntity<>("OTP verified successfully.", HttpStatus.OK);
    } else {
        return new ResponseEntity<>("Invalid OTP.", HttpStatus.BAD_REQUEST);
    }
}


    @PutMapping("/regenerate-otp")
    public ResponseEntity<String> regenerateOtp(@RequestParam String email){
        return new ResponseEntity<>(userService.regenerateOtp(email), HttpStatus.OK);
    }
    @PutMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        try {
            String message = userService.login(loginDto);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    @GetMapping("/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable("email") String email){
        User user = userService.findByEmail(email);
        return new ResponseEntity<>(user, HttpStatus.OK);

    }
    @PutMapping("/{email}")
    public ResponseEntity<String> updateProfile(@PathVariable("email") String email, @RequestBody UpdateUserRequest updateUserRequest){
        return  new ResponseEntity<>(userService.update(email,updateUserRequest), HttpStatus.OK);
    }
    @PutMapping("/changepass/{email}")
    public ResponseEntity<String> changePassword(@PathVariable("email") String email, @RequestBody ChangePasswordRequest changePasswordRequest){
        return  new ResponseEntity<>(userService.changePass(email,changePasswordRequest), HttpStatus.OK);
    }

}
