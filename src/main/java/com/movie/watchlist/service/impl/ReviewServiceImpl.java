package com.movie.watchlist.service.impl;

import com.movie.watchlist.dto.records.ReviewUpdateRequest;
import com.movie.watchlist.dto.request.ReviewRequest;
import com.movie.watchlist.dto.response.ReviewResponse;
import com.movie.watchlist.entity.entities.Movie;
import com.movie.watchlist.entity.entities.Reviews;
import com.movie.watchlist.entity.entities.User;
import com.movie.watchlist.enums.ActiveStatus;
import com.movie.watchlist.exception.error.InvalidOperationException;
import com.movie.watchlist.exception.error.ResourceNotFoundException;
import com.movie.watchlist.mapper.ReviewMapper;
import com.movie.watchlist.repositories.MovieRepository;
import com.movie.watchlist.repositories.ReviewRepository;
import com.movie.watchlist.repositories.UserRepository;
import com.movie.watchlist.service.interfaces.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final ReviewMapper reviewMapper;

    @Override
    public ReviewResponse submitReview(
            Long userId,
            Long movieId,
            ReviewRequest request
    ) {
        log.info("Started add/update review process for user ID: {} and movie ID: {}.", userId, movieId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Review operation failed: User not found. User ID: {}", userId);
                    return new ResourceNotFoundException(
                            "User not found with ID: " + userId
                    );
                });

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> {
                    log.warn("Review operation failed: Movie not found. Movie ID: {}", movieId);
                    return new ResourceNotFoundException(
                            "Movie not found with ID: " + movieId
                    );
                });

        Reviews review = reviewRepository
                .findByUserIdAndMovieIdAndActiveStatus(userId, movieId, ActiveStatus.ACTIVE)
                .orElseGet(() -> {
                    log.debug("No existing review found. Creating a new review. User ID: {}, Movie ID: {}", userId, movieId);
                    Reviews newReview = reviewMapper.toEntity(request);
                    newReview.setUser(user);
                    newReview.setMovie(movie);
                    return newReview;
                });

        if (review.getId() != null) {
            log.debug("Existing review found (Review ID: {}). Updating content (upsert).", review.getId());
            reviewMapper.updateEntity(
                    new ReviewUpdateRequest(
                            request.getRating(),
                            request.getContent()
                    ),
                    review
            );
        }

        Reviews savedReview = reviewRepository.save(review);
        log.info("Review saved successfully. Review ID: {}", savedReview.getId());

        return reviewMapper.toResponse(savedReview);
    }

    @Override
    public ReviewResponse updateReview(
            Long userId,
            Long movieId,
            ReviewUpdateRequest request
    ) {
        log.info("Started review update process for user ID: {} and movie ID: {}.", userId, movieId);

        Reviews review = reviewRepository
                .findByUserIdAndMovieIdAndActiveStatus(userId, movieId, ActiveStatus.ACTIVE)
                .orElseThrow(() -> {
                    log.warn("Review to update not found. User ID: {}, Movie ID: {}", userId, movieId);
                    return new ResourceNotFoundException(
                            "Review not found for this user and movie"
                    );
                });

        reviewMapper.updateEntity(request, review);
        Reviews savedReview = reviewRepository.save(review);

        log.info("Review updated successfully. Review ID: {}", savedReview.getId());

        return reviewMapper.toResponse(savedReview);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsForMovie(
            Long movieId,
            int page,
            int size
    ) {
        log.info("Fetching reviews for movie ID: {}. Page: {}, Size: {}", movieId, page, size);

        if (page < 0) {
            log.warn("Invalid page number provided: {}", page);
            throw new InvalidOperationException("Page index cannot be negative");
        }
        if (size < 1 || size > 100) {
            log.warn("Invalid page size provided: {}", size);
            throw new InvalidOperationException("Page size must be between 1 and 100");
        }
        if (!movieRepository.existsById(movieId)) {
            log.warn("Movie for which reviews were requested was not found. Movie ID: {}", movieId);
            throw new ResourceNotFoundException(
                    "Film not found: " + movieId
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Page<Reviews> reviewPage =
                reviewRepository.findByMovieIdAndActiveStatus(movieId, ActiveStatus.ACTIVE, pageable);

        log.info("Found {} total reviews for movie ID: {}.", reviewPage.getTotalElements(), movieId);

        return reviewPage.map(reviewMapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteReview(Long userId, Long movieId) {
        log.info("Deleting review written by user ID: {} for movie ID: {} (soft delete).", userId, movieId);

        Reviews review = reviewRepository
                .findByUserIdAndMovieIdAndActiveStatus(userId, movieId,ActiveStatus.ACTIVE)
                .orElseThrow(() -> {
                    log.warn("Review to delete not found. User ID: {}, Movie ID: {}", userId, movieId);
                    return new ResourceNotFoundException(
                            "This review not found for this user and movie"
                    );
                });

        review.setActiveStatus(ActiveStatus.INACTIVE);
        log.info("Review successfully set to inactive status. Review ID: {}", review.getId());
    }

    @Override
    public ReviewResponse getMyReview(Long userId, Long movieId) {
        Reviews review = reviewRepository
                .findByUserIdAndMovieIdAndActiveStatus(userId, movieId, ActiveStatus.ACTIVE)
                .orElseThrow(() -> {
                    log.warn("My review not found. User ID: {}, Movie ID: {}", userId, movieId);
                    return new ResourceNotFoundException(
                            "This review not found for this user and movie"
                    );
                });

        return reviewMapper.toResponse(review);
    }
}
