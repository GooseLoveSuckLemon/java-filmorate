package ru.yandex.practicum.filmorate.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.User.UserStorage;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(int filmId, int userId) {
        log.debug("Добавление лайка: на фильм {}, пользователя {}", filmId, userId);
        Film film = filmStorage.getFilmById(filmId);
        if (film.getLikes().contains(userId)) {
            log.warn("Пользователь {} уже лайкал фильм {}", userId, filmId);
            throw new IllegalStateException();
        }
        film.addLike(userId);
        log.info("Лайк добавлен: на фильм {}, пользователь {}", filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        log.debug("Попытка удалить лайк: с фильма {}, пользователя {}", filmId, userId);
        Film film = filmStorage.getFilmById(filmId);

        if (!film.getLikes().contains(userId)) {
            log.warn("Пользователь {} не лайкал фильм {}", userId, filmId);
            throw new IllegalStateException();
        }

        film.removeLike(userId);
        log.info("Лайк пользователя {} удален с фильма {}", userId, filmId);
    }

    public List<Film> getPopular(int count) {
        log.debug("Получение {} популярных фильмов", count);

        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparing((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
