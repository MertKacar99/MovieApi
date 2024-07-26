package com.kirsehirfilix.movieApi.controller;

import com.kirsehirfilix.movieApi.Service.EmailService;
import com.kirsehirfilix.movieApi.auth.entities.ForgotPassword;
import com.kirsehirfilix.movieApi.auth.entities.User;
import com.kirsehirfilix.movieApi.auth.repositories.ForgotPasswordRepository;
import com.kirsehirfilix.movieApi.auth.repositories.UserRepository;
import com.kirsehirfilix.movieApi.dto.ChangePassword;
import com.kirsehirfilix.movieApi.dto.MailBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("/forgotPassword")
public class ForgotPasswordController {
    private final UserRepository userRepository;
    private  final EmailService emailService;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final PasswordEncoder passwordEncoder;

    public ForgotPasswordController(UserRepository userRepository, EmailService emailService, ForgotPasswordRepository forgotPasswordRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.passwordEncoder = passwordEncoder;
    }
    // send mail for verification
    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<String> verifyMail(@PathVariable String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()->new   UsernameNotFoundException("Lütfen geçerli mail giriniz"));
        int otp = otpGenerator();
        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("this is the OTP for your Forgot Password request: " + otp)
                .subject("OTP for Forgot Password request")
                .build();
        ForgotPassword fp = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() + 70 * 1000))
                .user(user)
                .build();
        emailService.sendSimpleMessage(mailBody);
        forgotPasswordRepository.save(fp);
        return ResponseEntity.ok("Email sent for veritification");
    }
    @PostMapping("/verifyOtp/{otp}/{email}")
    public  ResponseEntity<String> verifyOtp(@PathVariable Integer otp, @PathVariable String email){
        User user = userRepository.findByEmail(email).orElseThrow(()->new   UsernameNotFoundException("Lütfen geçerli mail giriniz"));

        ForgotPassword  fp =forgotPasswordRepository.findbyOtpAndUser(otp,user).orElseThrow(()-> new RuntimeException("Invalid OTP for mail: " +email
        ));

        if (fp.getExpirationTime().before(Date.from(Instant.now()))){
            forgotPasswordRepository.deleteById(fp.getFpid());
            return  new ResponseEntity<>("OTP has expired!" , HttpStatus.EXPECTATION_FAILED);
        }

        return ResponseEntity.ok("OTP verified");

    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePasswordHandler(@RequestBody ChangePassword changePassword,@PathVariable String email){
        if(!Objects.equals(changePassword.password(),changePassword.repeatPassword())){
            return  new ResponseEntity<>("Please Enter the password Again!", HttpStatus.EXPECTATION_FAILED);
        }
        String encodedPassword = passwordEncoder.encode(changePassword.password());
        userRepository.updatePassword(email,encodedPassword);
        return  ResponseEntity.ok("Password has been changed!");
    }

    private  Integer otpGenerator(){
        Random random = new Random();
        return  random.nextInt(100_000, 999_999);

    }
}
