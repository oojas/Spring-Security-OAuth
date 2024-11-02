package com.spring.authentication.services;

import com.spring.authentication.Modals.Role;
import com.spring.authentication.Modals.User;
import com.spring.authentication.Modals.AuthenticationRequest;
import com.spring.authentication.Modals.AuthenticationResponse;
import com.spring.authentication.Modals.RegisterRequest;
import com.spring.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    public AuthenticationResponse register(RegisterRequest registerRequest) {
        User user=User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .pass(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER)
                .build();
        repository.save(user);
        var jwtToken=jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authReq) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authReq.getEmail(),
                authReq.getPassword()
        ));
        // if we get to this line this means that user is authenticated.
        User user=repository.findByEmail(authReq.getEmail()).orElseThrow();
        var jwtToken=jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
