package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.Film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.User.UserStorage;

import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmDbStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = (FilmDbStorage) filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(int filmId, int userId) {
        log.debug("Добавление лайка: на фильм {}, пользователя {}", filmId, userId);
        filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);

        filmStorage.addLike(filmId, userId);
        log.info("Лайк добавлен: на фильм {}, пользователь {}", filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        log.debug("Попытка удалить лайк: с фильма {}, пользователя {}", filmId, userId);
        filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);

        filmStorage.removeLike(filmId, userId);
        log.info("Лайк пользователя {} удален с фильма {}", userId, filmId);
    }

    public List<Film> getPopular(int count) {
        log.debug("Получение {} популярных фильмов", count);
        return filmStorage.getPopularFilms(count);
    }
}
