package com.user_service.user_service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@AllArgsConstructor
@Value
@Builder
@Jacksonized
public class SignupResponse {

    private String status;   // e.g. "SUCCESS"
    private String message;  // e.g. "User registered successfully"
    private UserData data;   // nested object

    @Value
    @AllArgsConstructor
    @Builder         // ✅ Add this
    @Jacksonized     // ✅ Add this too for proper Jackson support
    public static class UserData {
        private String userId;   // ULID
        private String username;
        private String email;
    }
}
