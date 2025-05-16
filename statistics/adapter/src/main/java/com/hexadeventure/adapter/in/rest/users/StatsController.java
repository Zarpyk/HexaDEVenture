package com.hexadeventure.adapter.in.rest.users;

import com.hexadeventure.adapter.in.rest.users.dto.out.AverageDistanceDTO;
import com.hexadeventure.adapter.in.rest.users.dto.out.AverageTimeDTO;
import com.hexadeventure.adapter.in.rest.users.dto.out.WinRateDTO;
import com.hexadeventure.application.port.in.users.StatsUseCase;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatsController {
    private final StatsUseCase statsUseCase;
    
    public StatsController(StatsUseCase statsUseCase) {
        this.statsUseCase = statsUseCase;
    }
    
    @GetMapping("/winRate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "User not found")
    })
    public ResponseEntity<WinRateDTO> getWinRate(@RequestParam String userId) {
        double winRate = statsUseCase.getWinRate(userId);
        return ResponseEntity.ok(new WinRateDTO(winRate));
    }
    
    @GetMapping("/averageTime")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "User not found")
    })
    public ResponseEntity<AverageTimeDTO> getAverageTime(@RequestParam String userId) {
        double averageTime = statsUseCase.getAverageTime(userId);
        return ResponseEntity.ok(new AverageTimeDTO(averageTime));
    }
    
    @GetMapping("/averageDistance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "User not found")
    })
    public ResponseEntity<AverageDistanceDTO> getAverageDistance(@RequestParam String userId) {
        double averageDistance = statsUseCase.getAverageDistance(userId);
        return ResponseEntity.ok(new AverageDistanceDTO(averageDistance));
    }
}
