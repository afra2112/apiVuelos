package com.api.backend.implement;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.stream.Collectors;

import com.api.backend.service.PermisoService;
import com.api.backend.repository.PermisoRepository;
import com.api.backend.dto.PermisoDTO;
import com.api.backend.entity.Permiso;
import org.springframework.stereotype.Service;

@Service
public class PermisoImplement implements PermisoService {

    @Autowired
    private PermisoRepository permisoRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PermisoDTO crear(PermisoDTO dto) {
        Permiso entidad = modelMapper.map(dto, Permiso.class);
        Permiso entidadGuardada = permisoRepository.save(entidad);
        return modelMapper.map(entidadGuardada, PermisoDTO.class);
    }

    @Override
    public PermisoDTO actualizar(Long id, PermisoDTO dto) {
        Permiso entidadExistente = permisoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Permiso no encontrado"));

        modelMapper.map(dto, entidadExistente);

        Permiso entidadActualizada = permisoRepository.save(entidadExistente);
        return modelMapper.map(entidadActualizada, PermisoDTO.class);
    }

    @Override
    public PermisoDTO buscarPorId(Long id) {
        Permiso entidad = permisoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Permiso no encontrado"));
        return modelMapper.map(entidad, PermisoDTO.class);
    }

    @Override
    public List<PermisoDTO> listarTodos() {
        return permisoRepository.findAll().stream()
            .map(entidad -> modelMapper.map(entidad, PermisoDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    public boolean eliminar(Long id) {
        if (!permisoRepository.existsById(id)) {
            return false;
        }
        permisoRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean existePorId(Long id) {
        return permisoRepository.existsById(id);
    }

    @Override
    public long contar() {
        return permisoRepository.count();
    }
}