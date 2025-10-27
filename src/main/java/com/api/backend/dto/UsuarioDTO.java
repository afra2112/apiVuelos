package com.api.backend.dto;

import com.api.backend.entity.Rol;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UsuarioDTO {


    private Long idUsuario;

    private String email;

    private String password;

    private String nombre;

    private List<String> roles = new ArrayList<>();
}