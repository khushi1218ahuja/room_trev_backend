package com.roomtrev.controller;

import com.roomtrev.dto.BookingRequest;
import com.roomtrev.dto.BookingResponse;
import com.roomtrev.security.AuthenticatedUser;
import com.roomtrev.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> create(
            @Valid @RequestBody BookingRequest req,
            @AuthenticationPrincipal AuthenticatedUser auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(req, auth.getId()));
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponse>> myBookings(@AuthenticationPrincipal AuthenticatedUser auth) {
        return ResponseEntity.ok(bookingService.getGuestBookings(auth.getId()));
    }

    @GetMapping("/host-bookings")
    public ResponseEntity<List<BookingResponse>> hostBookings(@AuthenticationPrincipal AuthenticatedUser auth) {
        return ResponseEntity.ok(bookingService.getHostBookings(auth.getId()));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BookingResponse> updateStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal AuthenticatedUser auth) {
        String status = body.get("status");
        return ResponseEntity.ok(bookingService.updateStatus(id, status, auth.getId(), auth.getRole()));
    }
}
