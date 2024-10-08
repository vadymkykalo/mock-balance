package com.vadymkykalo.mockbalance.controller.api.v1;

import com.vadymkykalo.mockbalance.dto.UserBalancesDto;
import com.vadymkykalo.mockbalance.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    @PostMapping("/set-users-balance")
    public ResponseEntity<String> setUserBalances(@RequestBody @Valid @NotNull UserBalancesDto request) {
        userService.updateUserBalancesAsync(request.getUserIdBalanceValue());
        return ResponseEntity.accepted().body("Batch job started successfully, processing in background...");
    }
}
