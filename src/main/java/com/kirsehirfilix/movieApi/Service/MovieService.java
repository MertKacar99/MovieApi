package com.kirsehirfilix.movieApi.Service;

import com.kirsehirfilix.movieApi.dto.MovieDto;
import com.kirsehirfilix.movieApi.dto.MoviePageResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MovieService {
    MovieDto addMovie(MovieDto movieDto , MultipartFile file) throws IOException;

    MovieDto getMovie(Integer movideId);

    List<MovieDto> getAllMovies();

    MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException;


    String deleteMovie(Integer movieId) throws IOException;



    MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize);

    MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize,
                                                           String sortBy, String dir);


}

