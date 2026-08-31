package com.eventhub.event_management_system.controller;

import com.eventhub.event_management_system.dto.AdminUserResponse;
import com.eventhub.event_management_system.service.AdminUserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(
            AdminUserService adminUserService) {

        this.adminUserService = adminUserService;
    }

    @GetMapping
    public ResponseEntity<List<AdminUserResponse>>
    getAllUsers() {

        return ResponseEntity.ok(
                adminUserService.getAllUsers()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminUserResponse>
    getUserById(@PathVariable Long id) {

        return ResponseEntity.ok(
                adminUserService.getUserById(id)
        );
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<AdminUserResponse>
    activateUser(@PathVariable Long id) {

        return ResponseEntity.ok(
                adminUserService.activateUser(id)
        );
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<AdminUserResponse>
    deactivateUser(@PathVariable Long id) {

        return ResponseEntity.ok(
                adminUserService.deactivateUser(id)
        );
    }
}