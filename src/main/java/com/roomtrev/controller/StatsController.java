package com.roomtrev.controller;

import com.roomtrev.dto.RoomResponse;
import com.roomtrev.dto.StatsDto;
import com.roomtrev.security.AuthenticatedUser;
import com.roomtrev.service.RoomService;
import com.roomtrev.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;
    private final RoomService roomService;

    @GetMapping("/platform")
    public ResponseEntity<StatsDto> platformStats() {
        return ResponseEntity.ok(statsService.getPlatformStats());
    }

    @GetMapping("/host")
    public ResponseEntity<Map<String, Object>> hostStats(@AuthenticationPrincipal AuthenticatedUser auth) {
        return ResponseEntity.ok(statsService.getHostStats(auth.getId()));
    }

    @GetMapping("/featured")
    public ResponseEntity<List<RoomResponse>> featured() {
        return ResponseEntity.ok(roomService.getFeatured());
    }

    @GetMapping("/cities")
    public ResponseEntity<List<String>> cities() {
        return ResponseEntity.ok(roomService.getCities());
    }
}
