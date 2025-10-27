package com.api.backend.implement;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.stream.Collectors;

import com.api.backend.service.RolService;
import com.api.backend.repository.RolRepository;
import com.api.backend.dto.RolDTO;
import com.api.backend.entity.Rol;
import org.springframework.stereotype.Service;

@Service
public class RolImplement implements RolService {

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public RolDTO crear(RolDTO dto) {
        Rol entidad = modelMapper.map(dto, Rol.class);
        Rol entidadGuardada = rolRepository.save(entidad);
        return modelMapper.map(entidadGuardada, RolDTO.class);
    }

    @Override
    public RolDTO actualizar(Long id, RolDTO dto) {
        Rol entidadExistente = rolRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        modelMapper.map(dto, entidadExistente);

        Rol entidadActualizada = rolRepository.save(entidadExistente);
        return modelMapper.map(entidadActualizada, RolDTO.class);
    }

    @Override
    public RolDTO buscarPorId(Long id) {
        Rol entidad = rolRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        return modelMapper.map(entidad, RolDTO.class);
    }

    @Override
    public List<RolDTO> listarTodos() {
        return rolRepository.findAll().stream()
            .map(entidad -> modelMapper.map(entidad, RolDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    public boolean eliminar(Long id) {
        if (!rolRepository.existsById(id)) {
            return false;
        }
        rolRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean existePorId(Long id) {
        return rolRepository.existsById(id);
    }

    @Override
    public long contar() {
        return rolRepository.count();
    }
}