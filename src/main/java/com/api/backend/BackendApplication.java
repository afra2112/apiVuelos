package com.api.backend;

import com.api.backend.config.enums.CiudadesEnum;
import com.api.backend.config.enums.RolesEnum;
import com.api.backend.entity.Ciudad;
import com.api.backend.entity.Permiso;
import com.api.backend.entity.Rol;
import com.api.backend.entity.Usuario;
import com.api.backend.repository.CiudadRepository;
import com.api.backend.repository.PermisoRepository;
import com.api.backend.repository.RolRepository;
import com.api.backend.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

    @Bean
    CommandLineRunner seeder(CiudadRepository ciudadRepository, PermisoRepository permisoRepo, RolRepository rolRepo, UsuarioRepository usuarioRepo, PasswordEncoder encoder) {
        return args -> {
            //ciudades
            if (ciudadRepository.findAll().isEmpty()){
                for (CiudadesEnum ciudadEnum : CiudadesEnum.values()) {
                    Ciudad ciudad = new Ciudad();
                    ciudad.setNombre(ciudadEnum);
                    ciudad.setDescripcion(ciudadEnum.getDescripcion());
                    ciudad.setCodigoCiudad(ciudadEnum.getCodigo());
                    ciudadRepository.save(ciudad);
                }
            }

            //rol admin con sus permisos
            if (rolRepo.findByNombre(RolesEnum.ADMIN).isEmpty()){
                List<Permiso> permisosAdmin = List.of(
                        new Permiso(null, "usuarios.ver"),
                        new Permiso(null, "usuarios.crear"),
                        new Permiso(null, "usuarios.editar"),
                        new Permiso(null, "usuarios.eliminar")
                );
                permisoRepo.saveAll(permisosAdmin);

                Rol admin = new Rol();
                admin.setNombre(RolesEnum.ADMIN);
                admin.setPermisos(permisosAdmin);
                rolRepo.save(admin);
            }

            //rol usuario con sus permisos
            if (rolRepo.findByNombre(RolesEnum.USUARIO).isEmpty()){
                List<Permiso> permisosUsuario = List.of(
                        permisoRepo.findByNombre("usuarios.ver")
                );

                Rol usuario = new Rol();
                usuario.setNombre(RolesEnum.USUARIO);
                usuario.setPermisos(permisosUsuario);
                rolRepo.save(usuario);
            }

            //usuario admin seeder
            if(usuarioRepo.findByEmail("andres@gmail.com").isEmpty()){
                Usuario u = new Usuario();
                u.setNombres("Andres Ramirez");
                u.setEmail("andres@gmail.com");
                u.setPassword(encoder.encode("andres1234"));
                u.setRoles(List.of(rolRepo.findByNombre(RolesEnum.ADMIN).orElseThrow()));
                usuarioRepo.save(u);
            }

            if(usuarioRepo.findByEmail("felipe@gmail.com").isEmpty()){
                Usuario u = new Usuario();
                u.setNombres("Felipe Agudelo");
                u.setEmail("felipe@gmail.com");
                u.setPassword(encoder.encode("felipe1234"));
                u.setRoles(List.of(rolRepo.findByNombre(RolesEnum.USUARIO).orElseThrow()));
                usuarioRepo.save(u);
            }
        };
    }
}
