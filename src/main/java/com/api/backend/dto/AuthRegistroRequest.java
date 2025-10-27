package com.api.backend.dto;

public record AuthRegistroRequest (
        String email,
        String password,
        String nombre
        //demas campos que se agreguen despues
){
}