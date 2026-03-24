package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre.Genres;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/genres")
@Slf4j
public class GenreController {

    @GetMapping
    public ResponseEntity<List<Genres>> getAllGenres() {
        log.info("Получение всех доступных жанров");
        List<Genres> genres = Arrays.asList(Genres.values());
        log.debug("Доступно жанров: {}", genres.size());
        return ResponseEntity.ok(genres);
    }
}
