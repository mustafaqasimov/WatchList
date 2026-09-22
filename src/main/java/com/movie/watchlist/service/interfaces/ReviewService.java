package com.movie.watchlist.service.interfaces;

import com.movie.watchlist.dto.records.ReviewUpdateRequest;
import com.movie.watchlist.dto.request.ReviewRequest;
import com.movie.watchlist.dto.response.ReviewResponse;
import org.springframework.data.domain.Page;

public interface ReviewService {

    ReviewResponse submitReview(Long userId, Long movieId, ReviewRequest request);

    Page<ReviewResponse> getReviewsForMovie(Long movieId, int page, int size);

    ReviewResponse updateReview(Long userId, Long movieId, ReviewUpdateRequest request);

    void deleteReview(Long userId, Long movieId);

    ReviewResponse getMyReview(Long id, Long movieId);
}
