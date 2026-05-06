package com.hd.FinanceTracker.user.mappers;

import com.hd.FinanceTracker.user.dto.AuthResponseDto;
import com.hd.FinanceTracker.user.dto.LoginRequestDto;
import com.hd.FinanceTracker.user.dto.RegisterRequestDto;
import com.hd.FinanceTracker.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(RegisterRequestDto registerRequestDto);
    User toEntity(LoginRequestDto loginRequestDto);
    AuthResponseDto toAuthDto(User user);
    RegisterRequestDto toDto(User user);
}
