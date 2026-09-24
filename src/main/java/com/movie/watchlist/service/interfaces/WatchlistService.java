package com.movie.watchlist.service.interfaces;

import com.movie.watchlist.dto.records.WatchlistUpdateRequest;
import com.movie.watchlist.dto.request.WatchlistItemRequest;
import com.movie.watchlist.dto.request.WatchlistRequest;
import com.movie.watchlist.dto.response.WatchlistItemResponse;
import com.movie.watchlist.dto.response.WatchlistResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface WatchlistService {
    WatchlistResponse createWatchlist(Long userId, WatchlistRequest request);
    List<WatchlistResponse> getUserWatchlists(Long userId);
    void deleteWatchlist(Long userId, Long watchlistId);

    WatchlistItemResponse addMovieToWatchlist(Long userId, Long watchlistId, WatchlistItemRequest request);
    WatchlistItemResponse updateMovieStatus(Long userId, Long watchlistId, Long movieId, WatchlistUpdateRequest request);
    Page<WatchlistItemResponse> getWatchlistItems(Long userId, Long watchlistId, int page, int size);
    void removeMovieFromWatchlist(Long userId, Long watchlistId, Long movieId);
}
