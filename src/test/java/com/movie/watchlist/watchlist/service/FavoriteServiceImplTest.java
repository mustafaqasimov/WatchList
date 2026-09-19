package com.movie.watchlist.watchlist.service;

import com.movie.watchlist.dto.response.FavoriteResponse;
import com.movie.watchlist.entity.entities.Favorite;
import com.movie.watchlist.entity.entities.Movie;
import com.movie.watchlist.entity.entities.User;
import com.movie.watchlist.enums.ActiveStatus;
import com.movie.watchlist.exception.error.ResourceAlreadyExistsException;
import com.movie.watchlist.exception.error.ResourceNotFoundException;
import com.movie.watchlist.mapper.FavoriteMapper;
import com.movie.watchlist.repositories.FavoriteRepository;
import com.movie.watchlist.repositories.UserRepository;
import com.movie.watchlist.service.impl.FavoriteServiceImpl;
import com.movie.watchlist.service.impl.MovieImportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceImplTest {

    @Mock
    private FavoriteRepository favoriteRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MovieImportService movieImportService;
    @Mock
    private FavoriteMapper favoriteMapper;

    @InjectMocks
    private FavoriteServiceImpl favoriteService;

    private static final Long USER_ID = 1L;
    private static final Long TMDB_ID = 550L;

    // ---------- addFavorite ----------

    @Test
    void addFavorite_savesAndReturnsDto_whenMovieNotAlreadyFavorited() {
        User user = mock(User.class);
        Movie movie = mock(Movie.class);
        Favorite savedFavorite = mock(Favorite.class);
        FavoriteResponse expectedResponse = mock(FavoriteResponse.class);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(movieImportService.getOrImportMovie(TMDB_ID)).thenReturn(movie);
        when(favoriteRepository.existsByUserAndMovie(user, movie)).thenReturn(false);
        when(favoriteRepository.save(any(Favorite.class))).thenReturn(savedFavorite);
        when(favoriteMapper.toDTO(savedFavorite)).thenReturn(expectedResponse);

        FavoriteResponse result = favoriteService.addFavorite(USER_ID, TMDB_ID);

        assertThat(result).isEqualTo(expectedResponse);
        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    void addFavorite_throwsResourceNotFound_whenUserDoesNotExist() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> favoriteService.addFavorite(USER_ID, TMDB_ID));

        verifyNoInteractions(movieImportService, favoriteRepository);
    }

    @Test
    void addFavorite_throwsResourceAlreadyExists_whenMovieAlreadyFavorited() {
        User user = mock(User.class);
        Movie movie = mock(Movie.class);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(movieImportService.getOrImportMovie(TMDB_ID)).thenReturn(movie);
        when(favoriteRepository.existsByUserAndMovie(user, movie)).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class,
                () -> favoriteService.addFavorite(USER_ID, TMDB_ID));

        verify(favoriteRepository, never()).save(any());
    }

    // ---------- getFavorites ----------

    @Test
    void getFavorites_returnsMappedPage_whenUserExists() {
        Pageable pageable = PageRequest.of(0, 10);
        Favorite favorite = mock(Favorite.class);
        FavoriteResponse response = mock(FavoriteResponse.class);
        Page<Favorite> favoritePage = new PageImpl<>(List.of(favorite), pageable, 1);

        when(userRepository.existsById(USER_ID)).thenReturn(true);
        when(favoriteRepository.findAllByUserIdAndActiveStatus(USER_ID, ActiveStatus.ACTIVE, pageable)).thenReturn(favoritePage);
        when(favoriteMapper.toDTO(favorite)).thenReturn(response);

        Page<FavoriteResponse> result = favoriteService.getFavorites(USER_ID, pageable);

        assertThat(result.getContent()).containsExactly(response);
    }

    @Test
    void getFavorites_throwsResourceNotFound_whenUserDoesNotExist() {
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.existsById(USER_ID)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> favoriteService.getFavorites(USER_ID, pageable));

        verify(favoriteRepository, never()).findAllByUserIdAndActiveStatus(any(), any(), any());
    }

    // ---------- removeFavorite ----------

    @Test
    void removeFavorite_deletesFavorite_whenItExists() {
        Favorite favorite = mock(Favorite.class);
        when(favoriteRepository.findByUserIdAndMovieTmdbIdAndActiveStatus(USER_ID, TMDB_ID, ActiveStatus.ACTIVE))
                .thenReturn(Optional.of(favorite));

        favoriteService.removeFavorite(USER_ID, TMDB_ID);

        verify(favoriteRepository).save(favorite);
    }

    @Test
    void removeFavorite_throwsResourceNotFound_whenFavoriteDoesNotExist() {
        when(favoriteRepository.findByUserIdAndMovieTmdbIdAndActiveStatus(USER_ID, TMDB_ID, ActiveStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> favoriteService.removeFavorite(USER_ID, TMDB_ID));

        verify(favoriteRepository, never()).delete(any());
    }
}