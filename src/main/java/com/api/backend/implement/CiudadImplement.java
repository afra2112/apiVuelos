package com.api.backend.implement;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.stream.Collectors;

import com.api.backend.service.CiudadService;
import com.api.backend.repository.CiudadRepository;
import com.api.backend.dto.CiudadDTO;
import com.api.backend.entity.Ciudad;

@Service
public class CiudadImplement implements CiudadService {

    @Autowired
    private CiudadRepository ciudadRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CiudadDTO crear(CiudadDTO dto) {
        Ciudad entidad = modelMapper.map(dto, Ciudad.class);
        Ciudad entidadGuardada = ciudadRepository.save(entidad);
        return modelMapper.map(entidadGuardada, CiudadDTO.class);
    }

    @Override
    public CiudadDTO actualizar(Long id, CiudadDTO dto) {
        Ciudad entidadExistente = ciudadRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ciudad no encontrado"));

        modelMapper.map(dto, entidadExistente);

        Ciudad entidadActualizada = ciudadRepository.save(entidadExistente);
        return modelMapper.map(entidadActualizada, CiudadDTO.class);
    }

    @Override
    public CiudadDTO buscarPorId(Long id) {
        Ciudad entidad = ciudadRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ciudad no encontrado"));
        return modelMapper.map(entidad, CiudadDTO.class);
    }

    @Override
    public List<CiudadDTO> listarTodos() {
        return ciudadRepository.findAll().stream()
            .map(entidad -> modelMapper.map(entidad, CiudadDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    public boolean eliminar(Long id) {
        if (!ciudadRepository.existsById(id)) {
            return false;
        }
        ciudadRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean existePorId(Long id) {
        return ciudadRepository.existsById(id);
    }

    @Override
    public long contar() {
        return ciudadRepository.count();
    }
}
