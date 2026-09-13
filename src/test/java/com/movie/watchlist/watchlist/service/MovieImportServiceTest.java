package com.movie.watchlist.watchlist.service;

import com.movie.watchlist.dto.records.TmdbMovie;
import com.movie.watchlist.dto.records.TmdbMovieResponse;
import com.movie.watchlist.dto.request.MovieRequest;
import com.movie.watchlist.dto.response.MovieResponse;
import com.movie.watchlist.entity.entities.Movie;
import com.movie.watchlist.enums.ActiveStatus;
import com.movie.watchlist.exception.error.ResourceNotFoundException;
import com.movie.watchlist.mapper.MovieMapper;
import com.movie.watchlist.mapper.TmdbMovieMapper;
import com.movie.watchlist.repositories.MovieRepository;
import com.movie.watchlist.service.impl.MovieImportService;
import com.movie.watchlist.service.impl.TmdbMovieService;
import com.movie.watchlist.service.interfaces.MovieService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class MovieImportServiceTest {

    @Mock
    private TmdbMovieService tmdbMovieService;
    @Mock
    private TmdbMovieMapper tmdbMapper;
    @Mock
    private MovieService movieService;
    @Mock
    private MovieRepository movieRepository;
    @Mock
    private MovieMapper movieMapper;

    @InjectMocks
    private MovieImportService movieImportService;

    private static final long TMDB_ID = 550L;

    // ---------- importPopularMovies ----------

    @Test
    void importPopularMovies_returnsZero_whenTmdbResponseIsNull() {
        when(tmdbMovieService.getGenreMap()).thenReturn(Map.of());
        when(tmdbMovieService.getPopularMovies(1)).thenReturn(null);

        int result = movieImportService.importPopularMovies(1);

        assertThat(result).isZero();
        verifyNoInteractions(movieService);
    }

    @Test
    void importPopularMovies_returnsZero_whenResultsListIsEmpty() {
        TmdbMovieResponse response = mock(TmdbMovieResponse.class);
        when(response.results()).thenReturn(List.of());

        when(tmdbMovieService.getGenreMap()).thenReturn(Map.of());
        when(tmdbMovieService.getPopularMovies(1)).thenReturn(response);

        int result = movieImportService.importPopularMovies(1);

        assertThat(result).isZero();
        verifyNoInteractions(movieService);
    }

    @Test
    void importPopularMovies_returnsZero_whenAllMoviesAlreadyExistInDb() {
        TmdbMovie tmdbMovie = mock(TmdbMovie.class);
        when(tmdbMovie.id()).thenReturn(TMDB_ID);

        TmdbMovieResponse response = mock(TmdbMovieResponse.class);
        when(response.results()).thenReturn(List.of(tmdbMovie));

        when(tmdbMovieService.getGenreMap()).thenReturn(Map.of());
        when(tmdbMovieService.getPopularMovies(1)).thenReturn(response);
        when(movieRepository.findAllTmdbIdsIn(List.of(TMDB_ID))).thenReturn(Set.of(TMDB_ID));

        int result = movieImportService.importPopularMovies(1);

        assertThat(result).isZero();
        verify(movieService, never()).addMovies(any());
    }

    @Test
    void importPopularMovies_importsOnlyNewMovies_whenSomeAlreadyExist() {
        long newTmdbId = 551L;

        TmdbMovie existingMovie = mock(TmdbMovie.class);
        when(existingMovie.id()).thenReturn(TMDB_ID);

        TmdbMovie newMovie = mock(TmdbMovie.class);
        when(newMovie.id()).thenReturn(newTmdbId);

        TmdbMovieResponse response = mock(TmdbMovieResponse.class);
        when(response.results()).thenReturn(List.of(existingMovie, newMovie));

        MovieRequest newMovieRequest = mock(MovieRequest.class);
        MovieResponse savedResponse = mock(MovieResponse.class);

        when(tmdbMovieService.getGenreMap()).thenReturn(Map.of());
        when(tmdbMovieService.getPopularMovies(1)).thenReturn(response);
        when(movieRepository.findAllTmdbIdsIn(List.of(TMDB_ID, newTmdbId))).thenReturn(Set.of(TMDB_ID));
        when(tmdbMapper.toMovieRequest(eq(newMovie), anyMap())).thenReturn(newMovieRequest);
        when(movieService.addMovies(List.of(newMovieRequest))).thenReturn(List.of(savedResponse));

        int result = movieImportService.importPopularMovies(1);

        assertThat(result).isEqualTo(1);
        verify(newMovieRequest).setPopular(true);
        verify(tmdbMapper, never()).toMovieRequest(eq(existingMovie), anyMap());
    }

    // ---------- getOrImportMovie ----------

    @Test
    void getOrImportMovie_returnsExistingMovie_withoutReactivation_whenAlreadyActive() {
        Movie movie = mock(Movie.class);
        when(movie.getActiveStatus()).thenReturn(ActiveStatus.ACTIVE);
        when(movieRepository.findByTmdbId(TMDB_ID)).thenReturn(Optional.of(movie));

        Movie result = movieImportService.getOrImportMovie(TMDB_ID);

        assertThat(result).isEqualTo(movie);
        verify(movie, never()).setActiveStatus(any());
        verifyNoInteractions(tmdbMovieService);
    }

    @Test
    void getOrImportMovie_reactivatesMovie_whenFoundButInactive() {
        Movie movie = mock(Movie.class);
        when(movie.getActiveStatus()).thenReturn(ActiveStatus.INACTIVE);
        when(movieRepository.findByTmdbId(TMDB_ID)).thenReturn(Optional.of(movie));

        Movie result = movieImportService.getOrImportMovie(TMDB_ID);

        assertThat(result).isEqualTo(movie);
        verify(movie).setActiveStatus(ActiveStatus.ACTIVE);
        verify(movieRepository, never()).save(any());
    }

    @Test
    void getOrImportMovie_importsFromTmdb_whenNotFoundInDb() {
        TmdbMovie details = mock(TmdbMovie.class);
        MovieRequest request = mock(MovieRequest.class);
        Movie unsavedMovie = mock(Movie.class);
        Movie savedMovie = mock(Movie.class);
        when(savedMovie.getMovieName()).thenReturn("Fight Club");
        when(savedMovie.getId()).thenReturn(1L);

        when(movieRepository.findByTmdbId(TMDB_ID)).thenReturn(Optional.empty());
        when(tmdbMovieService.getMovieDetails(TMDB_ID)).thenReturn(details);
        when(tmdbMapper.toMovieRequest(details, Map.of())).thenReturn(request);
        when(movieMapper.toEntity(request)).thenReturn(unsavedMovie);
        when(movieRepository.save(unsavedMovie)).thenReturn(savedMovie);

        Movie result = movieImportService.getOrImportMovie(TMDB_ID);

        assertThat(result).isEqualTo(savedMovie);
        verify(request).setPopular(false);
        verify(unsavedMovie).setActiveStatus(ActiveStatus.ACTIVE);
    }

    @Test
    void getOrImportMovie_throwsResourceNotFound_whenTmdbDetailsAreNull() {
        when(movieRepository.findByTmdbId(TMDB_ID)).thenReturn(Optional.empty());
        when(tmdbMovieService.getMovieDetails(TMDB_ID)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> movieImportService.getOrImportMovie(TMDB_ID));

        verify(movieRepository, never()).save(any());
    }
}
