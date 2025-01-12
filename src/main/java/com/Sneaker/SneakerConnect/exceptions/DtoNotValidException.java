package com.Sneaker.SneakerConnect.exceptions;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Data
@RequiredArgsConstructor
public class DtoNotValidException extends RuntimeException {

    private final Set<String> errorMessages;
}
