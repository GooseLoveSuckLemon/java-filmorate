package ru.yandex.practicum.filmorate.Validation;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.Validation.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film.Film;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FilmValidator {
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public void validateFilm(Film film) {
        log.debug("Начало валидации фильма: ID={}, название='{}'", film.getFilmId(), film.getName());
        List<String> errors = new ArrayList<>();

        if (film.getName() == null || film.getName().isBlank()) {
            String errorMsg = "Название не может быть пустым";
            errors.add(errorMsg);
            log.warn("Ошибка валидации фильма ID={}: {}", film.getFilmId(), errorMsg);
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            String errorMsg = "Превышена максимальная длина описания — 200 символов";
            errors.add(errorMsg);
            log.warn("Ошибка валидации фильма ID={}: {}", film.getFilmId(), errorMsg);
        }

        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            String errorMsg = "Дата релиза не может быть раньше 28 декабря 1895 года";
            errors.add(errorMsg);
            log.warn("Ошибка валидации фильма ID={}: {}", film.getFilmId(), errorMsg);
        }

        if (film.getDuration() != null && film.getDuration() <= 0) {
            String errorMsg = "Продолжительность фильма должна быть больше нуля";
            errors.add(errorMsg);
            log.warn("Ошибка валидации фильма ID={}: {}", film.getFilmId(), errorMsg);
        }

        if (film.getGenres() != null && film.getGenres().size() > 0) {
            log.debug("Фильм имеет {} жанров", film.getGenres().size());
        }

        if (film.getRating() != null) {
            log.debug("Фильм имеет рейтинг: {}", film.getRating().getCode());
        }

        if (!errors.isEmpty()) {
            log.error("Валидация фильма ID={} не пройдена. Количество ошибок: {}", film.getFilmId(), errors.size());
            throw new ValidationException(errors);
        }
        log.debug("Валидация фильма ID={} успешно завершена", film.getFilmId());
    }
}
