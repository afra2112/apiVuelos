package com.api.backend.implement;

import com.api.backend.config.JwtUtils;
import com.api.backend.config.enums.RolesEnum;
import com.api.backend.dto.AuthLoginRequest;
import com.api.backend.dto.AuthRegistroRequest;
import com.api.backend.dto.AuthResponse;
import com.api.backend.entity.Permiso;
import com.api.backend.entity.Rol;
import com.api.backend.entity.Usuario;
import com.api.backend.repository.RolRepository;
import com.api.backend.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;


@Service
public class UserDetailServiceImplement implements UserDetailsService {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository userRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public UserDetails loadUserByUsername(String email) {

        Usuario userEntity = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("El usuario " + email + " no existe."));
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        userEntity.getRoles().forEach(rol -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(rol.getNombre().name()))));
        userEntity.getRoles().stream().flatMap(rol -> rol.getPermisos().stream()).forEach(permiso -> authorityList.add(new SimpleGrantedAuthority(permiso.getNombre())));

        return new User(userEntity.getEmail(), userEntity.getPassword(), userEntity.isEnabled(), userEntity.isAccountNoExpired(), userEntity.isCredentialNoExpired(), userEntity.isAccountNoLocked(), authorityList);
    }

    public AuthResponse loginUser(AuthLoginRequest authLoginRequest) {

        String username = authLoginRequest.email();
        String password = authLoginRequest.password();

        Authentication authentication = this.authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtils.crearToken(authentication);

        Usuario usuario = userRepository.findByEmail(username).orElseThrow();
        List<String> roles = usuario.getRoles().stream().map(rol -> rol.getNombre().name()).toList();
        List<String> permisos = usuario.getRoles().stream().flatMap(rol -> rol.getPermisos().stream().map(Permiso::getNombre)).toList();

        return new AuthResponse(username, "Usuario Logueado Correctamente", token, roles, permisos);
    }

    public AuthResponse crearUsuario(AuthRegistroRequest authRegistroRequest) {

        String username = authRegistroRequest.email();
        String password = authRegistroRequest.password();
        String nombre = authRegistroRequest.nombre();
        List<Rol> roles = List.of(rolRepository.findByNombre(RolesEnum.USUARIO).orElseThrow());

        Usuario usuario = new Usuario();
        usuario.setNombres(nombre);
        usuario.setEmail(username);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRoles(roles);

        Usuario usuarioCreado = userRepository.save(usuario);

        List<SimpleGrantedAuthority> permisos = new ArrayList<>();
        usuario.getRoles().forEach(rol -> permisos.add(new SimpleGrantedAuthority("ROLE_".concat(rol.getNombre().name()))));
        usuarioCreado.getRoles().stream().flatMap(rol -> rol.getPermisos().stream()).forEach(permiso ->  permisos.add(new SimpleGrantedAuthority(permiso.getNombre())));

        Authentication authentication = new UsernamePasswordAuthenticationToken(usuarioCreado.getEmail(), usuarioCreado.getPassword(), permisos);
        String token = jwtUtils.crearToken(authentication);

        return new AuthResponse(usuarioCreado.getEmail(), "Usuario creado correctamente.", token, roles.stream().map(rol -> rol.getNombre().name()).toList(), permisos.stream().map(SimpleGrantedAuthority::toString).toList());
    }

    public Authentication authenticate(String username, String password) {
        UserDetails userDetails = this.loadUserByUsername(username);

        if (userDetails == null) {
            throw new BadCredentialsException("Email o contrasena incorrectos");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Contrasena incorrecta");
        }

        return new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities());
    }
}