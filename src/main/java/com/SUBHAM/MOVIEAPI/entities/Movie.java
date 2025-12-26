package com.SUBHAM.MOVIEAPI.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer movieId;

    @Column(nullable = false, length = 200)
    @NotBlank(message = "Please provide movie's title!")
    private String title;

    @Column(nullable = false)
    @NotBlank(message = "Please provide movie's director!")
    private String director;

    @Column(nullable = false)
    @NotBlank(message = "Please provide movie's studio!")
    private String studio;

    @ElementCollection
    /*“This is not an entity, but a collection of basic values (String, Integer, etc.)
    that should be stored in a separate table.”*/
    @CollectionTable(name = "movie_cast")
    /*Specifies the table name for the collection
    This table is linked to the parent entity via a foreign key */
    private Set<String> movieCast;

    @Column(nullable = false)
    private Integer releaseYear;

    @Column(nullable = false)
    @NotBlank(message = "Please provide movie's poster!")
    private String poster;

}