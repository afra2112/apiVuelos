package com.api.backend.controller;

import com.api.backend.dto.AuthLoginRequest;
import com.api.backend.dto.AuthRegistroRequest;
import com.api.backend.dto.AuthResponse;
import com.api.backend.implement.UserDetailServiceImplement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    UserDetailServiceImplement userDetailServiceImplement;

    @PostMapping("/registro")
    public ResponseEntity<AuthResponse> registrarse(@RequestBody @Valid AuthRegistroRequest authRegistroRequest){
        return new ResponseEntity<>(this.userDetailServiceImplement.crearUsuario(authRegistroRequest), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthLoginRequest request){
        return new ResponseEntity<>(this.userDetailServiceImplement.loginUser(request), HttpStatus.OK);
    }
}