package com.SUBHAM.MOVIEAPI.dto;

import java.util.List;
//A record is a special immutable data carrier class used to hold data.
//immutable
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public record MoviePageResponse(List<MovieDto> movieDtos,
                                Integer pageNumber,
                                Integer pageSize,
                                long totalElements,
                                int totalPages,
                                boolean isLast) {
}

