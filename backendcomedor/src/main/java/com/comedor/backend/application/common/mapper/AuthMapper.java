package com.comedor.backend.application.common.mapper;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;

import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AuthMapper {
    private final ModelMapper modelMapper;

}
