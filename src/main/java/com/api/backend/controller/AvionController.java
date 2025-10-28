package com.api.backend.controller;

import com.api.backend.dto.AvionDTO;
import com.api.backend.dto.AvionSimpleDTO;
import com.api.backend.repository.AvionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/aviones")
public class AvionController {

    @Autowired
    AvionRepository avionRepository;

    @Autowired
    ModelMapper modelMapper;

    @GetMapping("/")
    public ResponseEntity<List<AvionSimpleDTO>> listarAviones(){
        return ResponseEntity.ok(avionRepository.findAll().stream().map(avion -> modelMapper.map(avion, AvionSimpleDTO.class)).collect(Collectors.toList()));
    }
}
