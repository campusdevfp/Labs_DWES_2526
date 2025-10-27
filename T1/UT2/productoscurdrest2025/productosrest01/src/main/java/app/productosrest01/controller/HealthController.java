package app.productosrest01.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController

public class HealthController {

    @GetMapping("/api/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "productoscrudrest");
        status.put("timestamp", LocalDateTime.now().toString());
        return status;
    }
}