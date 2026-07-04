package com.movie.watchlist.mapper;

import com.movie.watchlist.dto.UserRequestDto;
import com.movie.watchlist.dto.UserResponseDto;
import com.movie.watchlist.entity.entities.User;
import com.movie.watchlist.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

     public User toEntity(UserRequestDto userRequestDto) {
         if(userRequestDto == null) return null;

         User user = new User();
         user.setUsername(userRequestDto.getEmail());
         user.setPassword(userRequestDto.getPassword());
         user.setEmail(userRequestDto.getEmail());

         user.setRole(Role.ROLE_USER);
         return user;
     }

     public UserResponseDto toDto(User user) {
         if (user == null) {
             return null;
         }

         UserResponseDto response = new UserResponseDto();
         response.setId(user.getId());
         response.setUsername(user.getUsername());
         response.setEmail(user.getEmail());
         response.setActiveStatus(user.getActiveStatus());

         return response;
     }
}
