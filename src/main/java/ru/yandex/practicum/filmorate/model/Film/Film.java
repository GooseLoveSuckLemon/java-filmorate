package ru.yandex.practicum.filmorate.model.Film;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre.Genres;
import ru.yandex.practicum.filmorate.model.Mpa.MpaRating;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private int filmId;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Short duration;
    private Set<Genres> genres;
    private MpaRating rating;

    private Set<Integer> likes = new HashSet<>();

    public void addLike(int userId) {
        likes.add(userId);
    }

    public void removeLike(int userId) {
        likes.remove(userId);
    }
}
