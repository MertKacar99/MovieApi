package com.kirsehirfilix.movieApi.auth.repositories;

import com.kirsehirfilix.movieApi.auth.entities.ForgotPassword;
import com.kirsehirfilix.movieApi.auth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword,Integer> {
        @Query("select fp  from  ForgotPassword  fp where fp.otp = ?1 and  fp.user = ?2")
        Optional<ForgotPassword> findbyOtpAndUser(Integer otp, User User);

}
