package com.api.backend.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    @Value("${clave.secreta.jwt}")
    public String key;

    @Value("${issue.generator}")
    public String issueGenerator;

    public String crearToken(Authentication authentication){
        Algorithm algoritmo = Algorithm.HMAC256(key);
        String username = authentication.getPrincipal().toString();
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        return JWT.create()
                .withIssuer(issueGenerator)
                .withSubject(username)
                .withClaim("permisos", authorities)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1800000))
                .withJWTId(UUID.randomUUID().toString())
                .withNotBefore(new Date(System.currentTimeMillis()))
                .sign(algoritmo);
    }

    public DecodedJWT validarToken(String token){
        try{
            Algorithm algoritmo = Algorithm.HMAC256(key);
            JWTVerifier verificador = JWT.require(algoritmo).withIssuer(issueGenerator).build();
            return verificador.verify(token);
        }catch (JWTVerificationException e){
            throw new JWTVerificationException("Token invalido");
        }
    }

    public String obtenerUsername(DecodedJWT tokenDecodificado){
        return tokenDecodificado.getSubject();
    }

    public Claim obtenerClaimEspecifico(DecodedJWT tokenDecodificado, String nombreClaim){
        return tokenDecodificado.getClaim(nombreClaim);
    }

    public Map<String, Claim> obtenerTOdosLosClaims(DecodedJWT tokenDecodificado){
        return tokenDecodificado.getClaims();
    }
}