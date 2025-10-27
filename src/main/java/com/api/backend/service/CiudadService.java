package com.api.backend.service;

import java.util.List;
import com.api.backend.dto.CiudadDTO;
import org.springframework.stereotype.Service;

@Service
public interface CiudadService {

    CiudadDTO crear(CiudadDTO dto);

    CiudadDTO actualizar(Long id, CiudadDTO dto);

    CiudadDTO buscarPorId(Long id);

    List<CiudadDTO> listarTodos();

    boolean eliminar(Long id);

    boolean existePorId(Long id);

    long contar();
}
