package com.user_service.user_service.controller;


import com.user_service.user_service.models.ResolveUsersRequest;
import com.user_service.user_service.models.ResolveUsersResponse;
import com.user_service.user_service.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/internal/user")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserService userService;

    @PostMapping("/resolve")
    public ResponseEntity<ResolveUsersResponse> resolveUserIds(@RequestBody ResolveUsersRequest request) {
        List<ResolveUsersResponse.UserMapping> results = userService.resolveUserIds(request.getUsernames());
        return ResponseEntity.ok(new ResolveUsersResponse(results));
    }
}
