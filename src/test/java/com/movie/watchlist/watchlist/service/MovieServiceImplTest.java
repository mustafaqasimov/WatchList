package com.movie.watchlist.watchlist.service;

import com.movie.watchlist.dto.request.MovieRequest;
import com.movie.watchlist.dto.response.MovieResponse;
import com.movie.watchlist.entity.entities.Movie;
import com.movie.watchlist.enums.ActiveStatus;
import com.movie.watchlist.exception.error.ResourceNotFoundException;
import com.movie.watchlist.mapper.MovieMapper;
import com.movie.watchlist.repositories.MovieRepository;
import com.movie.watchlist.service.impl.MovieServiceImpl;
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
class MovieServiceImplTest {

    @Mock
    private MovieRepository repository;
    @Mock
    private MovieMapper movieMapper;

    @InjectMocks
    private MovieServiceImpl movieService;

    private static final Long MOVIE_ID = 1L;

    // ---------- addMovies ----------

    @Test
    void addMovies_returnsEmptyList_whenInputIsNull() {
        List<MovieResponse> result = movieService.addMovies(null);

        assertThat(result).isEmpty();
        verifyNoInteractions(movieMapper, repository);
    }

    @Test
    void addMovies_returnsEmptyList_whenInputIsEmpty() {
        List<MovieResponse> result = movieService.addMovies(List.of());

        assertThat(result).isEmpty();
        verifyNoInteractions(movieMapper, repository);
    }

    @Test
    void addMovies_setsActiveStatus_whenMovieHasNoStatus() {
        MovieRequest request = mock(MovieRequest.class);
        Movie movieWithoutStatus = mock(Movie.class);
        when(movieWithoutStatus.getActiveStatus()).thenReturn(null);

        Movie savedMovie = mock(Movie.class);
        MovieResponse response = mock(MovieResponse.class);

        when(movieMapper.toEntityList(List.of(request))).thenReturn(List.of(movieWithoutStatus));
        when(repository.saveAll(List.of(movieWithoutStatus))).thenReturn(List.of(savedMovie));
        when(movieMapper.toDTOList(List.of(savedMovie))).thenReturn(List.of(response));

        List<MovieResponse> result = movieService.addMovies(List.of(request));

        assertThat(result).containsExactly(response);
        verify(movieWithoutStatus).setActiveStatus(ActiveStatus.ACTIVE);
    }

    @Test
    void addMovies_doesNotOverrideActiveStatus_whenAlreadySet() {
        MovieRequest request = mock(MovieRequest.class);
        Movie movieWithStatus = mock(Movie.class);
        when(movieWithStatus.getActiveStatus()).thenReturn(ActiveStatus.INACTIVE);

        when(movieMapper.toEntityList(List.of(request))).thenReturn(List.of(movieWithStatus));
        when(repository.saveAll(List.of(movieWithStatus))).thenReturn(List.of(movieWithStatus));
        when(movieMapper.toDTOList(any())).thenReturn(List.of(mock(MovieResponse.class)));

        movieService.addMovies(List.of(request));

        verify(movieWithStatus, never()).setActiveStatus(any());
    }

    // ---------- getMovieById ----------

    @Test
    void getMovieById_returnsDto_whenActiveMovieExists() {
        Movie movie = mock(Movie.class);
        MovieResponse response = mock(MovieResponse.class);

        when(repository.findByIdAndActiveStatus(MOVIE_ID, ActiveStatus.ACTIVE))
                .thenReturn(Optional.of(movie));
        when(movieMapper.toDTO(movie)).thenReturn(response);

        MovieResponse result = movieService.getMovieById(MOVIE_ID);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void getMovieById_throwsResourceNotFound_whenMovieDoesNotExistOrIsInactive() {
        when(repository.findByIdAndActiveStatus(MOVIE_ID, ActiveStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> movieService.getMovieById(MOVIE_ID));
    }

    // ---------- getPopularMovies ----------

    @Test
    void getPopularMovies_returnsMappedPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Movie movie = mock(Movie.class);
        MovieResponse response = mock(MovieResponse.class);
        Page<Movie> moviePage = new PageImpl<>(List.of(movie), pageable, 1);

        when(repository.findAllByPopularTrueAndActiveStatus(ActiveStatus.ACTIVE, pageable))
                .thenReturn(moviePage);
        when(movieMapper.toDTO(movie)).thenReturn(response);

        Page<MovieResponse> result = movieService.getPopularMovies(pageable);

        assertThat(result.getContent()).containsExactly(response);
    }

    // ---------- deleteMovie ----------

    @Test
    void deleteMovie_setsInactiveStatus_whenActiveMovieExists() {
        Movie movie = mock(Movie.class);
        when(repository.findByIdAndActiveStatus(MOVIE_ID, ActiveStatus.ACTIVE))
                .thenReturn(Optional.of(movie));

        movieService.deleteMovie(MOVIE_ID);

        verify(movie).setActiveStatus(ActiveStatus.INACTIVE);
    }

    @Test
    void deleteMovie_throwsResourceNotFound_whenMovieDoesNotExistOrAlreadyInactive() {
        when(repository.findByIdAndActiveStatus(MOVIE_ID, ActiveStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> movieService.deleteMovie(MOVIE_ID));
    }
}