package com.token.auth.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.token.auth.model.User;
import com.token.auth.repository.UserRepository;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter{
	
	private UserRepository userRepository;

	public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
		super(authenticationManager);
		this.userRepository = userRepository;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		System.out.println("JwtAuthorizationFilter -> doFilterInternal");
		
		String header = request.getHeader(JwtProperties.HEADER_STRING);
		
		if(header == null || !header.startsWith(JwtProperties.TOKEN_PREFIX)) {
			chain.doFilter(request, response);
			return;
		}
		
		Authentication authentication = getUserNameAndPasswordAuthentication(request);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		chain.doFilter(request, response);
	}

	private Authentication getUserNameAndPasswordAuthentication(HttpServletRequest request) {
		System.out.println("JwtAuthorizationFilter -> getUserNameAndPasswordAuthentication");
		
		String token = request.getHeader(JwtProperties.HEADER_STRING).replace(JwtProperties.TOKEN_PREFIX,"");
		
		if(token != null) {
			 // parse the token and validate it
            String userName = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET.getBytes()))
                    .build()
                    .verify(token)
                    .getSubject();
            
            if (userName != null) {
            	User user = userRepository.findByUsername(userName);
        		MyUserDetails myUserDetails = new MyUserDetails(user);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userName, null, myUserDetails.getAuthorities());
                return auth;
            }
		}
		
		return null;
	}

}