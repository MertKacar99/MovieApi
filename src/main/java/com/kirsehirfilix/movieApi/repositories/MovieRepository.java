package com.kirsehirfilix.movieApi.repositories;

import com.kirsehirfilix.movieApi.entity.Movie;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Integer> {
}
