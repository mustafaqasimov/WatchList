package com.movie.watchlist.service.impl;

import com.movie.watchlist.dto.records.WatchlistUpdateRequest;
import com.movie.watchlist.dto.request.WatchlistItemRequest;
import com.movie.watchlist.dto.request.WatchlistRequest;
import com.movie.watchlist.dto.response.WatchlistItemResponse;
import com.movie.watchlist.dto.response.WatchlistResponse;
import com.movie.watchlist.entity.entities.Movie;
import com.movie.watchlist.entity.entities.User;
import com.movie.watchlist.entity.entities.Watchlist;
import com.movie.watchlist.entity.entities.WatchlistItem;
import com.movie.watchlist.enums.ActiveStatus;
import com.movie.watchlist.enums.Priority;
import com.movie.watchlist.enums.WatchStatus;
import com.movie.watchlist.exception.error.InvalidOperationException;
import com.movie.watchlist.exception.error.ResourceNotFoundException;
import com.movie.watchlist.mapper.WatchlistMapper;
import com.movie.watchlist.repositories.MovieRepository;
import com.movie.watchlist.repositories.UserRepository;
import com.movie.watchlist.repositories.WatchlistItemRepository;
import com.movie.watchlist.repositories.WatchlistRepository;
import com.movie.watchlist.service.interfaces.WatchlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WatchlistServiceImpl implements WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final WatchlistItemRepository watchlistItemRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final WatchlistMapper watchlistMapper;

    @Override
    @Transactional
    public WatchlistResponse createWatchlist(Long userId, WatchlistRequest request) {
        if (watchlistRepository.existsByUserIdAndNameAndActiveStatus(userId, request.getName(), ActiveStatus.ACTIVE)) {
            throw new InvalidOperationException("This name is already in use");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Watchlist watchlist = watchlistMapper.toEntity(request);
        watchlist.setUser(user);

        Watchlist savedWatchlist = watchlistRepository.save(watchlist);
        log.info("New watchlist created. User ID: {}, Watchlist name: {}", userId, request.getName());
        return watchlistMapper.toWatchlistResponse(savedWatchlist);
    }

    @Override
    public List<WatchlistResponse> getUserWatchlists(Long userId) {
        return watchlistRepository.findByUserIdAndActiveStatus(userId, ActiveStatus.ACTIVE).stream()
                .map(watchlistMapper::toWatchlistResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteWatchlist(Long userId, Long watchlistId) {
        Watchlist watchlist = watchlistRepository.findByIdAndUserIdAndActiveStatus(watchlistId, userId, ActiveStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist not found or you are not the owner"));

        watchlist.setActiveStatus(ActiveStatus.INACTIVE);
        log.info("Watchlist deleted. Watchlist ID: {}", watchlistId);
    }

    @Override
    @Transactional
    public WatchlistItemResponse addMovieToWatchlist(Long userId, Long watchlistId, WatchlistItemRequest request) {
        Watchlist watchlist = watchlistRepository.findByIdAndUserIdAndActiveStatus(watchlistId, userId, ActiveStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist not found or you are not the owner"));

        if (watchlistItemRepository.existsByWatchlistIdAndMovieIdAndActiveStatus(watchlistId, request.getMovieId(), ActiveStatus.ACTIVE)) {
            throw new InvalidOperationException("This movie is already in the watchlist");
        }

        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));

        WatchlistItem item = watchlistMapper.toEntity(request);
        item.setWatchlist(watchlist);
        item.setMovie(movie);

        if (item.getWatchStatus() == null) item.setWatchStatus(WatchStatus.PLAN_TO_WATCH);
        if (item.getPriority() == null) item.setPriority(Priority.MEDIUM);

        WatchlistItem savedItem = watchlistItemRepository.save(item);
        log.info("Movie added to watchlist. Movie ID: {}, Watchlist ID: {}", movie.getId(), watchlistId);
        return watchlistMapper.toItemResponse(savedItem);
    }

    @Override
    @Transactional
    public WatchlistItemResponse updateMovieStatus(Long userId, Long watchlistId, Long movieId, WatchlistUpdateRequest request) {
        watchlistRepository.findByIdAndUserIdAndActiveStatus(watchlistId, userId, ActiveStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist not found or you are not the owner"));

        WatchlistItem item = watchlistItemRepository.findByWatchlistIdAndMovieIdAndActiveStatus(watchlistId, movieId, ActiveStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found in the watchlist"));

        watchlistMapper.updateItemFromRequest(request, item);

        WatchlistItem updatedItem = watchlistItemRepository.save(item);
        return watchlistMapper.toItemResponse(updatedItem);
    }

    @Override
    public Page<WatchlistItemResponse> getWatchlistItems(Long userId, Long watchlistId, int page, int size) {
        watchlistRepository.findByIdAndUserIdAndActiveStatus(watchlistId, userId, ActiveStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist not found or you are not the owner"));

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return watchlistItemRepository.findByWatchlistIdAndActiveStatus(watchlistId, ActiveStatus.ACTIVE, pageRequest)
                .map(watchlistMapper::toItemResponse);
    }

    @Override
    @Transactional
    public void removeMovieFromWatchlist(Long userId, Long watchlistId, Long movieId) {
        watchlistRepository.findByIdAndUserIdAndActiveStatus(watchlistId, userId, ActiveStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist not found or you are not the owner"));

        WatchlistItem item = watchlistItemRepository.findByWatchlistIdAndMovieIdAndActiveStatus(watchlistId, movieId, ActiveStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found in the watchlist"));

        item.setActiveStatus(ActiveStatus.INACTIVE);
        log.info("Movie removed from watchlist. Movie ID: {}, Watchlist ID: {}", movieId, watchlistId);
    }
}
