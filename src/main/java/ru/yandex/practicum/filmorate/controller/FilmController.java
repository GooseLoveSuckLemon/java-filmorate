package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.backend.FilmBackend;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j

public class FilmController {
    private final FilmBackend filmBackend;

    public FilmController(FilmBackend filmBackend) {
        this.filmBackend = filmBackend;
    }

    @PostMapping
    public ResponseEntity<Film> addFilm(@Validated @RequestBody Film film) {
        log.info("Добавление фильма: {}", film.getName());
        Film savedFilm = filmBackend.addFilm(film);
        log.info("Фильм '{}' успешно добавлен, ID: {}", film.getName(), savedFilm.getId());
        return ResponseEntity.status(201).body(savedFilm);
    }


    @PutMapping
    public ResponseEntity<Film> updateFilm(@Validated @RequestBody Film film) {
        if (film.getId() <= 0) {
            log.error("ID фильма не указан или некорректен");
            throw new IllegalArgumentException("ID фильма должен быть указан");
        }
        log.info("Обновление фильма с ID: {}", film.getId());
        Film updateFilm = filmBackend.updateFilm(film);
        log.info("Фильм с ID {} успешно обновлён", film.getId());
        return ResponseEntity.ok(updateFilm);
    }

    @GetMapping
    public ResponseEntity<List<Film>> getFilms() {
        log.info("Получение всех фильмов");
        List<Film> films = filmBackend.getAllFilms();
        log.debug("Возвращено {} фильмов", films.size());
        return ResponseEntity.ok(films);
    }
}
