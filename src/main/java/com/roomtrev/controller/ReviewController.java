package com.roomtrev.controller;

import com.roomtrev.dto.ReviewRequest;
import com.roomtrev.dto.ReviewResponse;
import com.roomtrev.security.AuthenticatedUser;
import com.roomtrev.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<ReviewResponse>> getRoomReviews(@PathVariable Integer roomId) {
        return ResponseEntity.ok(reviewService.getRoomReviews(roomId));
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> create(
            @Valid @RequestBody ReviewRequest req,
            @AuthenticationPrincipal AuthenticatedUser auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(req, auth.getId()));
    }
}
