package com.example.registerapp.service;

import com.example.registerapp.dto.*;
import com.example.registerapp.entity.User;
import com.example.registerapp.repository.UserRepository;
import com.example.registerapp.util.EmailUtil;
import com.example.registerapp.util.OtpUtil;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class UserService {

    @Autowired
    private OtpUtil otpUtil;
    @Autowired
    private EmailUtil emailUtil;
    @Autowired
    private UserRepository userRepository;
    public String register(RequestDto requestDto) {
        String otp = otpUtil.generateOtp();
        try {
            emailUtil.sendOtpEmail(requestDto.getEmail(), otp);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send otp please try again");
        }
        User user = new User();
        PasswordEncoder passwordEncoder =new BCryptPasswordEncoder(8);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setName(requestDto.getName());
        user.setEmail(requestDto.getEmail());
//        user.setPassword(requestDto.getPassword());
        user.setPhone(requestDto.getPhone());
        user.setRole(0);
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);
        return "To verify this is your email account, we will send a confirmation code to this email. Please check your email to receive the verification code to activate your account";
    }

    public String verifyAccount(String email, String otp) {
        User user = userRepository.findByEmail(email).orElseThrow((() -> new RuntimeException("User not found with email")));
        if(user.getOtp().equals(otp)&& Duration.between(user.getOtpGeneratedTime(), LocalDateTime.now()).getSeconds()<(1*60)){
            user.setActive(true);
            userRepository.save(user);
            return "OTP verified you can login";
        }
        return "Please regenerate otp and try again";
    }

    public String regenerateOtp(String email) {
        User user = userRepository.findByEmail(email).orElseThrow((() -> new RuntimeException("User not found with email")));
        String otp = otpUtil.generateOtp();
        try {
            emailUtil.sendOtpEmail(email, otp);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send otp please try again");
        }
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);
        return "Email sent...Please check email to verify account within 1 minutes";
    }

    public String login(LoginDto loginDto) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(8);
        User user = userRepository.findByEmail(loginDto.getEmail()).orElseThrow((() -> new RuntimeException("User not found with email")));
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())){
           throw new RuntimeException("password is incorrect");
        } else if (!user.isActive()) {
            return "your account is not verified";
        }
        return "login successful";

    }

    public boolean verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email"));
        if (user.getOtp().equals(otp) &&
                Duration.between(user.getOtpGeneratedTime(), LocalDateTime.now()).getSeconds() < 60) {
            user.setActive(true);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public User findByEmail(String email) {
            return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not existed"));

    }

    public String  update(String email, UpdateUserRequest updateUserRequest) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email"));
        user.setName(updateUserRequest.getName());
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(8);
        user.setPassword(passwordEncoder.encode(updateUserRequest.getPassword()));
        user.setPhone(updateUserRequest.getPhone());
        userRepository.save(user);
        return "User updated successfull";
    }


    public String changePass(String email, ChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email"));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(8);
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getPassword()));

        userRepository.save(user);
        return "Password changed successfull. Now you can login";
    }
}
