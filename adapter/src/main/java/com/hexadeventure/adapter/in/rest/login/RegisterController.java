package com.hexadeventure.adapter.in.rest.login;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.hexadeventure.application.port.in.login.RegisterUseCase;

import java.security.Principal;

@RestController
public class RegisterController {
    private final RegisterUseCase registerUseCase;
    
    public RegisterController(RegisterUseCase registerUseCase) {
        this.registerUseCase = registerUseCase;
    }
    
    @PostMapping("/register")
    public ResponseEntity<Void> register(Principal principal, @RequestBody UserDTO user) {
        if(principal != null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        registerUseCase.register(user.toModel());
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/unregister")
    public ResponseEntity<Void> unregister(Principal principal) {
        registerUseCase.unregister(principal.getName());
        return ResponseEntity.ok().build();
    }
}
