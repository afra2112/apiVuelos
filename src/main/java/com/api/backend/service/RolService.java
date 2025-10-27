package com.api.backend.service;

import java.util.List;
import com.api.backend.dto.RolDTO;
import org.springframework.stereotype.Service;

@Service
public interface RolService {

    RolDTO crear(RolDTO dto);

    RolDTO actualizar(Long id, RolDTO dto);

    RolDTO buscarPorId(Long id);

    List<RolDTO> listarTodos();

    boolean eliminar(Long id);

    boolean existePorId(Long id);

    long contar();
}