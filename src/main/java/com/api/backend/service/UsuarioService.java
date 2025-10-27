package com.api.backend.service;

import java.util.List;
import com.api.backend.dto.UsuarioDTO;
import org.springframework.stereotype.Service;

@Service
public interface UsuarioService {

    UsuarioDTO crear(UsuarioDTO dto);

    UsuarioDTO actualizar(Long id, UsuarioDTO dto);

    UsuarioDTO buscarPorId(Long id);

    List<UsuarioDTO> listarTodos();

    boolean eliminar(Long id);

    boolean existePorId(Long id);

    long contar();
}