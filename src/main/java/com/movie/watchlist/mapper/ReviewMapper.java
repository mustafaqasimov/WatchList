package com.movie.watchlist.mapper;

import com.movie.watchlist.dto.records.ReviewUpdateRequest;
import com.movie.watchlist.dto.request.ReviewRequest;
import com.movie.watchlist.dto.response.ReviewResponse;
import com.movie.watchlist.entity.entities.Reviews;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(source = "user.userName", target = "authorName")
    @Mapping(source = "movie.id", target = "movieId")
    ReviewResponse toResponse(Reviews review);

    Reviews toEntity(ReviewRequest reviewRequest);

    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "movie", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(
            ReviewUpdateRequest request,
            @MappingTarget Reviews review
    );
}
