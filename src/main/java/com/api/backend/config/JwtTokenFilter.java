package com.api.backend.config;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    public JwtTokenFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (token != null){
            token = token.substring(7);
            DecodedJWT tokenDecodificado = jwtUtils.validarToken(token);

            String username = jwtUtils.obtenerUsername(tokenDecodificado);
            String permisosString = jwtUtils.obtenerClaimEspecifico(tokenDecodificado, "permisos").asString();
            Collection<? extends GrantedAuthority> permisos = AuthorityUtils.commaSeparatedStringToAuthorityList(permisosString);

            //llamo al contexto de spring security, le asigo el authentication afirmando que es valido el token y se guarda ese contexto en el context hodler
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, permisos);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("🟢 AUTH CONTEXT => " + SecurityContextHolder.getContext().getAuthentication());
        }

        filterChain.doFilter(request, response);
    }
}