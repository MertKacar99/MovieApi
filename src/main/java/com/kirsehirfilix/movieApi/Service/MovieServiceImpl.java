package com.kirsehirfilix.movieApi.Service;

import com.kirsehirfilix.movieApi.dto.MovieDto;
import com.kirsehirfilix.movieApi.dto.MoviePageResponse;
import com.kirsehirfilix.movieApi.entity.Movie;
import com.kirsehirfilix.movieApi.exceptions.FileExistsException;
import com.kirsehirfilix.movieApi.exceptions.MovieNotFoundException;
import com.kirsehirfilix.movieApi.repositories.MovieRepository;
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

    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {
        // 1. Dosyayı yükle
        if (Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))) {
            throw new FileExistsException("File already exists");
        }
        String uploadFileName = fileService.uploadFile(path, file);

        // 2. 'poster' alanının değerini dosya adı olarak ayarla
        movieDto.setPoster(uploadFileName);

        // 3. DTO'yu Movie nesnesine eşle
        Movie movie = new Movie(
                null,
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster()
        );

        // 4. Film nesnesini kaydet --> kaydedilen Film nesnesi
        Movie savedMovie = movieRepository.save(movie);

        // 5. Poster URL'sini oluştur
        String posterUrl = baseUrl + "/file/" + uploadFileName;

        // 6. Movie nesnesini DTO nesnesine eşle ve geri döndür
        MovieDto response = new MovieDto(
                savedMovie.getMovieId(),
                savedMovie.getTitle(),
                savedMovie.getDirector(),
                savedMovie.getStudio(),
                savedMovie.getMovieCast(),
                savedMovie.getReleaseYear(),
                savedMovie.getPoster(),
                posterUrl
        );

        return response;
    }

    @Override
    public MovieDto getMovie(Integer movieId) {
        // 1. Veritabanında belirtilen ID'ye sahip filmi kontrol et ve varsa verilerini getir
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Film Bulunamadı! Film id:" + movieId));

        // 2. Poster URL'si oluştur
        String posterUrl = baseUrl + "/file/" + movie.getPoster();

        // 3. MovieDto nesnesine eşle ve geri döndür
        MovieDto response = new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );

        return response;
    }

    @Override
    public List<MovieDto> getAllMovies() {
        // 1. Veritabanından tüm filmleri getir
        List<Movie> movies = movieRepository.findAll();

        List<MovieDto> movieDtos = new ArrayList<>();
        // 2. Liste üzerinde ITERATE ederek her film için posterUrl oluştur
        for (Movie movie : movies) {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto movieDto = new MovieDto(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDtos.add(movieDto);
        }
        // 3. MovieDto nesnesine eşle ve geri döndür
        return movieDtos;
    }

    @Override
    public MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException {
        // 1. Belirtilen movieId'ye sahip film nesnesi var mı diye kontrol edilir.
        Movie mv = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Film Bulunamadı! Film id:" + movieId));

        // 2. Eğer dosya null ise hiçbir şey yapma.
        // Eğer dosya null değilse, mevcut kayıt ile ilişkili olan dosyayı sil ve yeni dosyayı yükle.
        String fileName = mv.getPoster();
        if (file != null) {
            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
            fileName = fileService.uploadFile(path, file);
        }
        // 3. movieDto'nun poster değerini, adımdaki işleme göre ayarla.
        movieDto.setPoster(fileName);

        // 4. movieDto'yu movie nesnesine eşle.
        Movie movie = new Movie(
                mv.getMovieId(),
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster()
        );

        // 5. Film nesnesini kaydet ve film nesnesini döndür.
        Movie updatedMovie = movieRepository.save(movie);

        // 6. Poster URL'si oluştur.
        String posterUrl = baseUrl + "/file/" + fileName;

        // 7. MovieDto'ya eşle ve onu döndür.
        MovieDto response = new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );

        return response;
    }

    @Override
    public String deleteMovie(Integer movieId) throws IOException {
        // 1. DB'de film nesnesi var mı diye kontrol edilir.
        Movie mv = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException("Film Bulunamadı! Film id:" + movieId));

        // 2. Bu nesneye ilişkili dosyayı SİL.
        Files.deleteIfExists(Paths.get(path + File.separator + mv.getPoster()));

        // 3. Film nesnesini SİL.
        movieRepository.delete(mv);

        return "Film başarıyla silindi, Film Id: " + movieId;
    }


     @Override
     public MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize) {
         Pageable pageable = PageRequest.of(pageNumber, pageSize);

         Page<Movie> moviePages = movieRepository.findAll(pageable);
         List<Movie> movies = moviePages.getContent();

         List<MovieDto> movieDtos = new ArrayList<>();

         // 2. iterate through the list, generate posterUrl for each movie obj,
         // and map to MovieDto obj
         for(Movie movie : movies) {
             String posterUrl = baseUrl + "/file/" + movie.getPoster();
             MovieDto movieDto = new MovieDto(
                     movie.getMovieId(),
                     movie.getTitle(),
                     movie.getDirector(),
                     movie.getStudio(),
                     movie.getMovieCast(),
                     movie.getReleaseYear(),
                     movie.getPoster(),
                     posterUrl
             );
             movieDtos.add(movieDto);
         }


         return new MoviePageResponse(movieDtos, pageNumber, pageSize,
                 moviePages.getTotalElements(),
                 moviePages.getTotalPages(),
                 moviePages.isLast());
     }

     @Override
     public MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize,
                                                                   String sortBy, String dir) {
         Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                 : Sort.by(sortBy).descending();

         Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

         Page<Movie> moviePages = movieRepository.findAll(pageable);
         List<Movie> movies = moviePages.getContent();

         List<MovieDto> movieDtos = new ArrayList<>();

         // 2. iterate through the list, generate posterUrl for each movie obj,
         // and map to MovieDto obj
         for(Movie movie : movies) {
             String posterUrl = baseUrl + "/file/" + movie.getPoster();
             MovieDto movieDto = new MovieDto(
                     movie.getMovieId(),
                     movie.getTitle(),
                     movie.getDirector(),
                     movie.getStudio(),
                     movie.getMovieCast(),
                     movie.getReleaseYear(),
                     movie.getPoster(),
                     posterUrl
             );
             movieDtos.add(movieDto);
         }


         return new MoviePageResponse(movieDtos, pageNumber, pageSize,
                 moviePages.getTotalElements(),
                 moviePages.getTotalPages(),
                 moviePages.isLast());
     }

 }
