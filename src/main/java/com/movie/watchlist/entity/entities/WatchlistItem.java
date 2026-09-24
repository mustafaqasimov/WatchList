package com.movie.watchlist.entity.entities;

import com.movie.watchlist.entity.base.BaseEntity;
import com.movie.watchlist.enums.Priority;
import com.movie.watchlist.enums.WatchStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "watchlist_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class WatchlistItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "watchlist_id", nullable = false)
    Watchlist watchlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    Movie movie;

    @Enumerated(EnumType.STRING)
    @Column(name = "watch_status", nullable = false)
    WatchStatus watchStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    Priority priority;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDateTime createdAt;
}
