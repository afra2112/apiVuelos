package com.api.backend.dto;

import java.util.List;

public record AuthResponse (
        String email,
        String mensaje,
        String token,
        List<String> roles,
        List<String> permisos
){}