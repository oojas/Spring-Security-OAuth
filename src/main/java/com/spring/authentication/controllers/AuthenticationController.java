package com.spring.authentication.controllers;



import com.spring.authentication.Modals.AuthenticationRequest;
import com.spring.authentication.Modals.AuthenticationResponse;
import com.spring.authentication.Modals.RegisterRequest;
import com.spring.authentication.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/*
* This class is basically to create endpoints which can be hit to register and authenticate the email pass.
* */

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/jwtAuth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    @PostMapping(value = "/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest registerRequest
    ){
        return ResponseEntity.ok(authenticationService.register(registerRequest));
    }

    @PostMapping(value = "/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest authReq
    ){
     return ResponseEntity.ok(authenticationService.authenticate(authReq));
    }
}
