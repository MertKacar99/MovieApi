package com.kirsehirfilix.movieApi.dto;

import java.util.List;

import java.util.List;

public record MoviePageResponse(List<MovieDto> movieDtos,
                                Integer pageNumber,
                                Integer pageSize,
                                long totalElements,
                                int totalPages,
                                boolean isLast) {
}

