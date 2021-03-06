package com.token.auth.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.token.auth.dto.LoginRequest;


public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		System.out.println("JwtAuthenticationFilter -> attemptAuthentication");
		
		LoginRequest loginRequest = null;
		
		try {
			loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				loginRequest.getUserName(), 
				loginRequest.getPassword(), 
				new ArrayList<>());

		Authentication auth = authenticationManager.authenticate(authenticationToken);
		
		return auth;
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
		System.out.println("JwtAuthenticationFilter -> successfulAuthentication");
		
		// Grab principal
		MyUserDetails myUserDetails = (MyUserDetails) authResult.getPrincipal();
		
		// Create JWT Token
        String token = JWT.create()
                .withSubject(myUserDetails.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(JwtProperties.SECRET.getBytes()));
        
        response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX +  token);
	}

}
