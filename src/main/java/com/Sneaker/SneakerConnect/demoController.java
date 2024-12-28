package com.Sneaker.SneakerConnect;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/demo")
@PreAuthorize("hasRole('OWNER')")
public class demoController {

    @GetMapping
//    @PreAuthorize("hasAuthority('Admin: Create')")
    public String get() {
        return "get: Admin access";
    }

    @PostMapping
//    @PreAuthorize("hasAuthority('Admin: Read')")
    public String post() {
        return "Post: Admin access";
    }

    @PutMapping
//    @PreAuthorize("hasAuthority('Admin: Update')")
    public String update() {
        return "Update: Admin access";
    }

    @DeleteMapping
//    @PreAuthorize("hasAuthority('Admin: Delete')")
    public String delete() {
        return "Delete: Admin access";
    }
}
