package app.ejemplo02.dto;

import java.util.Date;
import java.util.List;

public record SessionInfo(
        String sessionId,
        String username,
        List<String> roles,
        Date creationTime,
        Date lastAccessedTime,
        boolean isAuthenticated
) {}

