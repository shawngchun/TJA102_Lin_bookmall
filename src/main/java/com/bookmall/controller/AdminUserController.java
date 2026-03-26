package com.bookmall.controller;

import com.bookmall.entity.BkmlUser;
import com.bookmall.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')") // 確保只有管理員能呼叫這些 API
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    // 取得所有使用者
    @GetMapping
    public ResponseEntity<List<BkmlUser>> listAllUsers() {
        return ResponseEntity.ok(adminUserService.getAllUsers());
    }

    // 修改使用者角色
    @PatchMapping("/{id}/role")
    public ResponseEntity<?> changeRole(@PathVariable Integer id, @RequestBody Map<String, String> payload) {
        String newRole = payload.get("role");
        try {
            BkmlUser updatedUser = adminUserService.updateUserRole(id, newRole);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 刪除使用者
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeUser(@PathVariable Integer id) {
        try {
            adminUserService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "使用者已成功刪除"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<BkmlUser>> searchUsers(@RequestParam String keyword) {
        return ResponseEntity.ok(adminUserService.searchUsersByEmail(keyword));
    }
}