package com.eventhub.event_management_system.dto;




import com.eventhub.event_management_system.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private Long userId;
    private String name;
    private String email;
    private Role role;
}
