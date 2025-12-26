package com.SUBHAM.MOVIEAPI.repositories;


import com.SUBHAM.MOVIEAPI.entities.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface MovieRepository extends JpaRepository<Movie, Integer> {
        Optional<Movie> findByTitle(String title);
    }
