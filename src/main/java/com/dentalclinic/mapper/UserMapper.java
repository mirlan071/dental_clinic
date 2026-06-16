package com.dentalclinic.mapper;

import com.dentalclinic.controller.dto.auth.RegisterRequest;
import com.dentalclinic.domain.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    default User toUserEntity(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(User.Role.valueOf(request.getRole()));
        return user;
    }
}
