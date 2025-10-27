package com.api.backend.controller;

import com.api.backend.dto.CiudadDTO;
import com.api.backend.entity.Ciudad;
import com.api.backend.repository.CiudadRepository;
import com.api.backend.service.CiudadService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.modelmapper.ModelMapper;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ciudades")
public class CiudadController {

    @Autowired
    private CiudadService ciudadService;

    @Autowired
    private CiudadRepository ciudadRepository;

    @Autowired
    ModelMapper modelMapper;

    @GetMapping("/")
    public ResponseEntity<List<CiudadDTO>> listarTodos() {
        List<CiudadDTO> lista = ciudadService.listarTodos();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CiudadDTO> obtenerPorId(@PathVariable Long id) {
        CiudadDTO dto = ciudadService.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/")
    public ResponseEntity<CiudadDTO> crear(@RequestBody CiudadDTO dto) {
        CiudadDTO creado = ciudadService.crear(dto);
        return ResponseEntity.ok(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CiudadDTO> actualizar(@PathVariable Long id, @RequestBody CiudadDTO dto) {
        CiudadDTO actualizado = ciudadService.actualizar(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CiudadDTO> actualizarParcial(
            @PathVariable Long id,
            @RequestBody Map<String, Object> cambios) {

        Ciudad ciudad = ciudadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ciudad no encontrado"));

        cambios.forEach((clave, valor) -> {
            Field campo = ReflectionUtils.findField(Ciudad.class, clave);
            if (campo != null) {
                campo.setAccessible(true);
                ReflectionUtils.setField(campo, ciudad, valor);
            }
        });

        Ciudad actualizado = ciudadRepository.save(ciudad);
        return ResponseEntity.ok(modelMapper.map(actualizado, CiudadDTO.class));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        boolean eliminado = ciudadService.eliminar(id);
        if (eliminado) {
            return ResponseEntity.ok("Ciudad eliminado correctamente");
        } else {
            return ResponseEntity.badRequest().body("Ciudad no encontrado");
        }
    }
}
