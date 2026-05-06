package com.hd.FinanceTracker.user.dto;

import com.hd.FinanceTracker.user.entity.Role;
import jakarta.validation.constraints.*;

import java.time.LocalDate;


public record RegisterRequestDto(

  @NotBlank(message = "Name is required")
  String name,
  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  String email,
  @NotBlank(message = "Password is required")
  @Size(min = 8, message = "Password must be at least 8 characters")
  String password,
  @NotNull(message="Date of birth is required")
  @Past(message="Date of birth must be in the past")
  LocalDate dob
){}

