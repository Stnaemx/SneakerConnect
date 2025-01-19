package com.Sneaker.SneakerConnect.entity.userCreation;

import com.Sneaker.SneakerConnect.auth.dto.UserCreationDto;
import com.Sneaker.SneakerConnect.user.Role;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class UserCreationStrategyFactory {

    Map<Role, UserCreationStrategy<? extends UserCreationDto>> strategies;

    // initializes map of user creation strategies based on Role. spring will inject a List<UserCreationStrategy> of implementing classes
    public UserCreationStrategyFactory(List<UserCreationStrategy<? extends UserCreationDto>> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        UserCreationStrategy::getRole, // call the getRole method for each UserCreationStrategy implementation
                        Function.identity() // use the object as is
                ));
    }

    // ignore warning bc we know its safe
    @SuppressWarnings("unchecked")
    // returns a strategy based on T
    public <T extends UserCreationDto> UserCreationStrategy<T> getStrategy(Role role) {
        // compiler doesn't know which implementation (UserCreationStrategy<? extends UserCreationDto>) is being return so we explicit type cast it to T
        return (UserCreationStrategy<T>) strategies.get(role);
    }
}