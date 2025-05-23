package com.hexadeventure.adapter.in.rest.users;

import com.hexadeventure.adapter.in.rest.users.dto.out.UserInfoDTO;
import com.hexadeventure.application.port.in.users.UserUseCase;
import com.hexadeventure.model.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserController {
    private final UserUseCase userUseCase;
    
    public UserController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }
    
    @GetMapping("/userInfo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User  successfully"),
            @ApiResponse(responseCode = "400", description = "User not found"),
    })
    public ResponseEntity<UserInfoDTO> getUserInfo(Principal principal,
                                                   @Schema(description = "Not required and ignored " +
                                                                         "if user is logged in")
                                                   @RequestParam(required = false)
                                                   String userId) {
        User user = userUseCase.getUser(principal == null ? null : principal.getName(), userId);
        return ResponseEntity.ok(UserInfoDTO.fromModel(user, principal == null));
    }
}
