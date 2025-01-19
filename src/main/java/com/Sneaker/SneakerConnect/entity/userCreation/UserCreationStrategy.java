package com.Sneaker.SneakerConnect.entity.userCreation;

import com.Sneaker.SneakerConnect.auth.dto.UserCreationDto;
import com.Sneaker.SneakerConnect.entity.User;
import com.Sneaker.SneakerConnect.user.Role;

// implementing class must specify the Dto they are using to build their user
public interface UserCreationStrategy<T extends UserCreationDto> {

    User createUser(T request);
    Role getRole(); // Returns the role this strategy supports
}
