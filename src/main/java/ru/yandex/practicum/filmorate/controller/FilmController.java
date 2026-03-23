package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.Film.FilmStorage;

import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j

public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @PostMapping
    public ResponseEntity<Film> addFilm(@Validated @RequestBody Film film) {
        log.info("Добавление фильма: {}", film.getName());
        Film savedFilm = filmStorage.addFilm(film);
        log.info("Фильм '{}' успешно добавлен, ID: {}", film.getName(), savedFilm.getFilmId());
        return ResponseEntity.status(201).body(savedFilm);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Film> updateFilm(@PathVariable int id, @Validated @RequestBody Film film) {
        log.info("Обновление фильма с ID: {}", id);
        film.setFilmId(id);
        Film updateFilm = filmStorage.updateFilm(film);
        log.info("Фильм с ID {} успешно обновлён", id);
        return ResponseEntity.ok(updateFilm);
    }

    @GetMapping
    public ResponseEntity<List<Film>> getFilms() {
        log.info("Получение всех фильмов");
        List<Film> films = filmStorage.getAllFilms();
        log.debug("Возвращено {} фильмов", films.size());
        return ResponseEntity.ok(films);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilmId(@PathVariable int id) {
        log.info("Получение фильма с ID: {}", id);
        Film film = filmStorage.getFilmById(id);
        log.debug("получено {} фильмов", film);
        return ResponseEntity.ok(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> addLke(@PathVariable int id, @PathVariable int userId) {
        log.info("Добавление лайка пользователя {} к фильму {}", userId, id);
        filmService.addLike(id, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> removeLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Удаление лайка пользователя {} у фильма {}", userId, id);
        filmService.removeLike(id, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Получение {} популярных фильмов", count);
        List<Film> popularFilms = filmService.getPopular(count);
        return ResponseEntity.ok(popularFilms);
    }
}
