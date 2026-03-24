package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa.MpaRating;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
public class MpaController {

    @GetMapping
    public ResponseEntity<List<MpaRating>> getAllMpa() {
        log.info("Получение всех рейтингов MPA");
        List<MpaRating> ratings = Arrays.asList(MpaRating.values());
        log.debug("Доступно рейтингов: {}", ratings.size());
        return ResponseEntity.ok(ratings);
    }
}