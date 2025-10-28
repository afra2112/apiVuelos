package com.api.backend.controller;

import com.api.backend.dto.RolDTO;
import com.api.backend.entity.Rol;
import com.api.backend.repository.RolRepository;
import com.api.backend.service.RolService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.modelmapper.ModelMapper;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/api/admin/roles")
public class RolController {

    @Autowired
    private RolService rolService;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    ModelMapper modelMapper;


    @GetMapping("/")
    public ResponseEntity<List<RolDTO>> listarTodos() {
        List<RolDTO> lista = rolService.listarTodos();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolDTO> obtenerPorId(@PathVariable Long id) {
        RolDTO dto = rolService.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/")
    public ResponseEntity<RolDTO> crear(@RequestBody RolDTO dto) {
        RolDTO creado = rolService.crear(dto);
        return ResponseEntity.ok(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RolDTO> actualizar(@PathVariable Long id, @RequestBody RolDTO dto) {
        RolDTO actualizado = rolService.actualizar(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RolDTO> actualizarParcial(
            @PathVariable Long id,
            @RequestBody Map<String, Object> cambios) {

        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("no encontrado"));

        cambios.forEach((clave, valor) -> {
            Field campo = ReflectionUtils.findField(Rol.class, clave);
            if (campo != null) {
                campo.setAccessible(true);
                ReflectionUtils.setField(campo, rol, valor);
            }
        });

        Rol actualizado = rolRepository.save(rol);
        return ResponseEntity.ok(modelMapper.map(actualizado, RolDTO.class));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        boolean eliminado = rolService.eliminar(id);
        if (eliminado) {
            return ResponseEntity.ok("Rol eliminado correctamente");
        } else {
            return ResponseEntity.badRequest().body("Rol no encontrado");
        }
    }
}