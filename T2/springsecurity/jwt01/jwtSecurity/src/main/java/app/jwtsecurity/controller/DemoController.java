package app.jwtsecurity.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class DemoController {

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<String> user(Principal principal) {
        return ResponseEntity.ok("Hello " + principal.getName() + " (USER content)");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> admin(Principal principal) {
        return ResponseEntity.ok("Hello " + principal.getName() + " (ADMIN content)");
    }
}