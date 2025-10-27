package com.api.backend.service;

import java.util.List;
import com.api.backend.dto.PermisoDTO;
import org.springframework.stereotype.Service;

@Service
public interface PermisoService {

    PermisoDTO crear(PermisoDTO dto);

    PermisoDTO actualizar(Long id, PermisoDTO dto);

    PermisoDTO buscarPorId(Long id);

    List<PermisoDTO> listarTodos();

    boolean eliminar(Long id);

    boolean existePorId(Long id);

    long contar();
}