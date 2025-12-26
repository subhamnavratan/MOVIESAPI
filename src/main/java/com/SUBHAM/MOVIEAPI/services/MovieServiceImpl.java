package com.SUBHAM.MOVIEAPI.services;


import com.SUBHAM.MOVIEAPI.dto.MovieDto;
import com.SUBHAM.MOVIEAPI.dto.MoviePageResponse;
import com.SUBHAM.MOVIEAPI.entities.Movie;
import com.SUBHAM.MOVIEAPI.exceptions.FileExistsException;
import com.SUBHAM.MOVIEAPI.exceptions.MovieNotFoundException;
import com.SUBHAM.MOVIEAPI.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final FileService fileService;

    @Value("${project.poster}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;

    public MovieServiceImpl(MovieRepository movieRepository, FileService fileService) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
    }

    //  CREATE
    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Poster file is required");
        }

        if (Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))) {
            throw new FileExistsException("File already exists! Please use another name");
        }

        String uploadedFileName = fileService.uploadFile(path, file);
        movieDto.setPoster(uploadedFileName);

        Movie movie = new Movie(
                null,
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                uploadedFileName
        );

        Movie savedMovie = movieRepository.save(movie);
        String posterUrl = baseUrl + "/file/" + uploadedFileName;

        return new MovieDto(
                savedMovie.getMovieId(),
                savedMovie.getTitle(),
                savedMovie.getDirector(),
                savedMovie.getStudio(),
                savedMovie.getMovieCast(),
                savedMovie.getReleaseYear(),
                savedMovie.getPoster(),
                posterUrl
        );
    }

    //  READ (by TITLE)
    @Override
    public MovieDto getMovie(String title) {

        Movie movie = movieRepository.findByTitle(title)
                .orElseThrow(() ->
                        new MovieNotFoundException("Movie not found with title = " + title));

        return new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                baseUrl + "/file/" + movie.getPoster()
        );
    }

    //  READ ALL
    @Override
    public List<MovieDto> getAllMovies() {

        List<Movie> movies = movieRepository.findAll();
        List<MovieDto> response = new ArrayList<>();

        for (Movie movie : movies) {
            response.add(new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    baseUrl + "/file/" + movie.getPoster()
            ));
        }
        return response;
    }

    // UPDATE (by TITLE)
    @Override
    public MovieDto updateMovie(String title, MovieDto movieDto, MultipartFile file)
            throws IOException {

        Movie existingMovie = movieRepository.findByTitle(title)
                .orElseThrow(() ->
                        new MovieNotFoundException("Movie not found with title = " + title));

        String fileName = existingMovie.getPoster();

        if (file != null && !file.isEmpty()) {
            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
            fileName = fileService.uploadFile(path, file);
        }

        Movie movie = new Movie(
                existingMovie.getMovieId(), // keep same ID internally
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                fileName
        );

        Movie updatedMovie = movieRepository.save(movie);

        return new MovieDto(
                updatedMovie.getMovieId(),
                updatedMovie.getTitle(),
                updatedMovie.getDirector(),
                updatedMovie.getStudio(),
                updatedMovie.getMovieCast(),
                updatedMovie.getReleaseYear(),
                updatedMovie.getPoster(),
                baseUrl + "/file/" + updatedMovie.getPoster()
        );
    }

    //  DELETE (by TITLE)
    @Override
    public String deleteMovie(String title) throws IOException {

        Movie movie = movieRepository.findByTitle(title)
                .orElseThrow(() ->
                        new MovieNotFoundException("Movie not found with title = " + title));

        Files.deleteIfExists(Paths.get(path + File.separator + movie.getPoster()));
        movieRepository.delete(movie);

        return "Movie deleted with title = " + title;
    }

    //  PAGINATION
    @Override
    public MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize) {
     //Pageable is an interface that represents pagination + sorting information for a database query.
      //Pageable does NOT store page numbers alone — it represents a range of row indexes to fetch from the database.
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Movie> moviePage = movieRepository.findAll(pageable);
         //Page<Movie> means a paginated result set of Movie entities with metadata.
        List<MovieDto> movieDtos = new ArrayList<>();
        for (Movie movie : moviePage.getContent()) {
            movieDtos.add(new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    baseUrl + "/file/" + movie.getPoster()
            ));
        }

        return new MoviePageResponse(
                movieDtos,
                pageNumber,
                pageSize,
                moviePage.getTotalElements(),
                moviePage.getTotalPages(),
                moviePage.isLast()
        );
    }

    // PAGINATION + SORTING
    @Override
    public MoviePageResponse getAllMoviesWithPaginationAndSorting(
            Integer pageNumber, Integer pageSize, String sortBy, String dir) {

        Sort sort = dir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        dir.equalsIgnoreCase("asc");
/*checks sort direction without case sensitivity Sort.by(sortBy)
→tells Spring which field to sort by.ascending() / .descending()
→ defines the order of sorting  */
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Movie> moviePage = movieRepository.findAll(pageable);

        List<MovieDto> movieDtos = new ArrayList<>();
        for (Movie movie : moviePage.getContent()) {
            movieDtos.add(new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    baseUrl + "/file/" + movie.getPoster()
            ));
        }

        return new MoviePageResponse(
                movieDtos,
                pageNumber,
                pageSize,
                moviePage.getTotalElements(),
                moviePage.getTotalPages(),
                moviePage.isLast()
        );
    }
}
