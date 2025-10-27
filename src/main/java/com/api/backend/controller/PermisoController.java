package com.api.backend.controller;

import com.api.backend.dto.PermisoDTO;
import com.api.backend.entity.Permiso;
import com.api.backend.repository.PermisoRepository;
import com.api.backend.service.PermisoService;
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
@RequestMapping("/api/permisos")
public class PermisoController {

    @Autowired
    private PermisoService permisoService;

    @Autowired
    private PermisoRepository permisoRepository;

    @Autowired
    ModelMapper modelMapper;


    @GetMapping("/")
    public ResponseEntity<List<PermisoDTO>> listarTodos() {
        List<PermisoDTO> lista = permisoService.listarTodos();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermisoDTO> obtenerPorId(@PathVariable Long id) {
        PermisoDTO dto = permisoService.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/")
    public ResponseEntity<PermisoDTO> crear(@RequestBody PermisoDTO dto) {
        PermisoDTO creado = permisoService.crear(dto);
        return ResponseEntity.ok(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PermisoDTO> actualizar(@PathVariable Long id, @RequestBody PermisoDTO dto) {
        PermisoDTO actualizado = permisoService.actualizar(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PermisoDTO> actualizarParcial(
            @PathVariable Long id,
            @RequestBody Map<String, Object> cambios) {

        Permiso permiso = permisoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("no encontrado"));

        cambios.forEach((clave, valor) -> {
            Field campo = ReflectionUtils.findField(Permiso.class, clave);
            if (campo != null) {
                campo.setAccessible(true);
                ReflectionUtils.setField(campo, permiso, valor);
            }
        });

        Permiso actualizado = permisoRepository.save(permiso);
        return ResponseEntity.ok(modelMapper.map(actualizado, PermisoDTO.class));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        boolean eliminado = permisoService.eliminar(id);
        if (eliminado) {
            return ResponseEntity.ok("Permiso eliminado correctamente");
        } else {
            return ResponseEntity.badRequest().body("Permiso no encontrado");
        }
    }
}