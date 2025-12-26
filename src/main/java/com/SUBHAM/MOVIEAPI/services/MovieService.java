package com.SUBHAM.MOVIEAPI.services;

import com.SUBHAM.MOVIEAPI.dto.MovieDto;
import com.SUBHAM.MOVIEAPI.dto.MoviePageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MovieService {

    MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException;

    MovieDto getMovie(String title);

    List<MovieDto> getAllMovies();

    MovieDto updateMovie(String title, MovieDto movieDto, MultipartFile file) throws IOException;

    String deleteMovie(String title) throws IOException;

    MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize);

    MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize,
                                                           String sortBy, String dir);
}