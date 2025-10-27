package com.api.backend.implement;

import com.api.backend.config.enums.RolesEnum;
import com.api.backend.entity.Rol;
import com.api.backend.repository.RolRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.stream.Collectors;

import com.api.backend.service.UsuarioService;
import com.api.backend.repository.UsuarioRepository;
import com.api.backend.dto.UsuarioDTO;
import com.api.backend.entity.Usuario;
import org.springframework.stereotype.Service;

@Service
public class UsuarioImplement implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UsuarioDTO crear(UsuarioDTO dto) {
        List<Rol> roles = dto.getRoles().stream().map(rol -> rolRepository.findByNombre(RolesEnum.valueOf(rol)).orElseThrow()).toList();
        Usuario entidad = new Usuario();
        entidad.setEmail(dto.getEmail());
        entidad.setPassword(passwordEncoder.encode(dto.getPassword()));
        entidad.setRoles(roles);
        entidad.setNombres(dto.getNombre());
        Usuario entidadGuardada = usuarioRepository.save(entidad);
        return modelMapper.map(entidadGuardada, UsuarioDTO.class);
    }

    @Override
    public UsuarioDTO actualizar(Long id, UsuarioDTO dto) {
        Usuario entidadExistente = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        modelMapper.map(dto, entidadExistente);

        Usuario entidadActualizada = usuarioRepository.save(entidadExistente);
        return modelMapper.map(entidadActualizada, UsuarioDTO.class);
    }

    @Override
    public UsuarioDTO buscarPorId(Long id) {
        Usuario entidad = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return modelMapper.map(entidad, UsuarioDTO.class);
    }

    @Override
    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
            .map(entidad -> modelMapper.map(entidad, UsuarioDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    public boolean eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            return false;
        }
        usuarioRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean existePorId(Long id) {
        return usuarioRepository.existsById(id);
    }

    @Override
    public long contar() {
        return usuarioRepository.count();
    }
}