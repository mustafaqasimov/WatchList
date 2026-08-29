package com.movie.watchlist.mapper;

import com.movie.watchlist.dto.request.RegisterRequest;
import com.movie.watchlist.dto.response.AuthResponse;
import com.movie.watchlist.entity.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "ROLE_USER")
    @Mapping(target = "password", source = "hashedPassword")
    User toEntity(RegisterRequest request, String hashedPassword);

    @Mapping(target = "accessToken", source = "accessToken")
    @Mapping(target = "refreshToken", source = "refreshToken")
    AuthResponse toAuthResponse(User user, String accessToken, String refreshToken);

}
