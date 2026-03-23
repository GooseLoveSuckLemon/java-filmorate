package ru.yandex.practicum.filmorate.storage.Film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.Validation.FilmValidator;
import ru.yandex.practicum.filmorate.Validation.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;
    private final FilmValidator filmValidator = new FilmValidator();

    @Override
    public Film addFilm(Film film) {
        log.info("Добавление нового фильма: '{}'", film.getName());
        filmValidator.validateFilm(film);
        film.setFilmId(id++);
        films.put(film.getFilmId(), film);
        log.info("Фильм успешно добавлен с ID: {}", film.getFilmId());
        return film;
    }

    @Override
    public Film updateFilm(Film updatedFilm) {
        log.info("Обновление фильма с ID: {}", updatedFilm.getFilmId());
        if (!films.containsKey(updatedFilm.getFilmId())) {
            String errorMsg = "Фильм с ID " + updatedFilm.getFilmId() + " не найден";
            log.error(errorMsg);
            throw new NotFoundException(errorMsg);
        }
        filmValidator.validateFilm(updatedFilm);
        films.put(updatedFilm.getFilmId(), updatedFilm);
        log.info("Фильм с ID {} успешно обновлён", updatedFilm.getFilmId());
        return updatedFilm;
    }

    @Override
    public List<Film> getAllFilms() {
        int filmCount = films.size();
        log.info("Запрос всех фильмов. Найдено: {} записей", filmCount);
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(int id) {
        log.info("Запрос фильма с ID: {}", id);
        Film film = films.get(id);
        if (film == null) {
            String errorMsg = "Фильм с ID {} " + id + " не найден";
            log.error(errorMsg);
            throw new NotFoundException(errorMsg);
        }
        log.info("Фильм с ID: {}, найден {}", id, film.getName());
        return film;
    }
}


