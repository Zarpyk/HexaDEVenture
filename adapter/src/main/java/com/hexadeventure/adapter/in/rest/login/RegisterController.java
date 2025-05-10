package com.hexadeventure.adapter.in.rest.login;

import com.hexadeventure.application.port.in.login.RegisterUseCase;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class RegisterController {
    private final RegisterUseCase registerUseCase;
    
    public RegisterController(RegisterUseCase registerUseCase) {
        this.registerUseCase = registerUseCase;
    }
    
    @PostMapping("/register")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user data"),
            @ApiResponse(responseCode = "401", description = "User already logged in"),
            @ApiResponse(responseCode = "409", description = "User already exists"),
    })
    public ResponseEntity<Void> register(Principal principal, @RequestBody UserDTO user) {
        if(principal != null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        registerUseCase.register(user.toModel(), user.password());
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/unregister")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User unregistered successfully"),
            @ApiResponse(responseCode = "401", description = "User not logged in")
    })
    public ResponseEntity<Void> unregister(Principal principal) {
        registerUseCase.unregister(principal.getName());
        return ResponseEntity.ok().build();
    }
}
