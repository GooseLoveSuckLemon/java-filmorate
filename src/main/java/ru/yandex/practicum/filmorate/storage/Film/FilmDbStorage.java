package ru.yandex.practicum.filmorate.storage.Film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.Validation.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import java.util.LinkedHashSet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Comparator;
import java.util.stream.Collectors;

@Repository("filmStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    private String getPlaceholders(int count) {
        return "?" + ", ?".repeat(count - 1);
    }

    private Object[] getFilmId(List<Film> films) {
        return films.stream().map(Film::getFilmId).toArray();
    }

    private void saveGenres(int filmId, Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) return;

        List<Genre> sortedGenres = new ArrayList<>(genres);
        sortedGenres.sort(Comparator.comparingInt(Genre::getId));

        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

        jdbcTemplate.batchUpdate(sql, sortedGenres, sortedGenres.size(),
                (ps, genre) -> {
                    ps.setInt(1, filmId);
                    ps.setInt(2, genre.getId());
                });
    }

    private void loadGenresForFilms(List<Film> films) {
        if (films.isEmpty()) {
            return;
        }

        String sql = "SELECT fg.film_id, g.genre_id, g.genre_name " +
                "FROM film_genres fg " +
                "JOIN genres g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id IN (" + getPlaceholders(films.size()) + ") " +
                "ORDER BY g.genre_id ASC";

        Map<Integer, Set<Genre>> genresFilm = new HashMap<>();
        jdbcTemplate.query(sql, rs -> {
            int filmId = rs.getInt("film_id");
            Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("genre_name"));
            genresFilm.computeIfAbsent(filmId, k -> new HashSet<>()).add(genre);
        }, getFilmId(films));

        for (Film film : films) {
            Set<Genre> genres = genresFilm.getOrDefault(film.getFilmId(), new HashSet<>());
            List<Genre> sorted = new ArrayList<>(genres);
            sorted.sort(Comparator.comparingInt(Genre::getId));
            film.setGenres(new LinkedHashSet<>(sorted));
        }
    }

    private void loadLikesForFilms(List<Film> films) {
        if (films.isEmpty()) {
            return;
        }

        String sql = "SELECT film_id, user_id FROM likes WHERE film_id IN (" + getPlaceholders(films.size()) + ")";

        Map<Integer, Set<Integer>> likesByFilm = new HashMap<>();
        jdbcTemplate.query(sql, rs -> {
            int filmId = rs.getInt("film_id");
            int userId = rs.getInt("user_id");
            likesByFilm.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
        }, getFilmId(films));

        for (Film film : films) {
            film.setLikes(likesByFilm.getOrDefault(film.getFilmId(), new HashSet<>()));
        }
    }

    private void loadGenresAndLikes(Film film) {
        String genreSql = "SELECT g.genre_id, g.genre_name FROM film_genres fg " +
                "JOIN genres g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = ? " +
                "ORDER BY g.genre_id ASC";
        List<Genre> genres = jdbcTemplate.query(genreSql, (rs, rowNum) ->
                        new Genre(rs.getInt("genre_id"), rs.getString("genre_name")),
                film.getFilmId());
        film.setGenres(new HashSet<>(genres));

        String likesSql = "SELECT user_id FROM likes WHERE film_id = ?";
        List<Integer> likes = jdbcTemplate.query(likesSql, (rs, rowNum) -> rs.getInt("user_id"), film.getFilmId());
        film.setLikes(new HashSet<>(likes));
    }

    private void validateGenresExist(Set<Genre> genres) {
        List<Integer> genreIds = genres.stream().map(Genre::getId).collect(Collectors.toList());
        String sql = "SELECT COUNT(*) FROM genres WHERE genre_id IN (" + getPlaceholders(genreIds.size()) + ")";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, genreIds.toArray());

        if (count == null || count != genreIds.size()) {
            throw new NotFoundException("Один или несколько жанров не найдены");
        }
    }

    @Override
    public Film addFilm(Film film) {
        if (film.getMpa() != null) {
            String checkMpaSql = "SELECT COUNT(*) FROM rating_mpa WHERE rating_id = ?";
            Integer mpaCount = jdbcTemplate.queryForObject(checkMpaSql, Integer.class, film.getMpa().getId());
            if (mpaCount == 0) {
                throw new NotFoundException("MPA рейтинг с ID " + film.getMpa().getId() + " не найден");
            }
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            validateGenresExist(film.getGenres());
        }

        String sql = "INSERT INTO films (film_name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setObject(5, film.getMpa() != null ? film.getMpa().getId() : null);
            return ps;
        }, keyHolder);

        film.setFilmId(keyHolder.getKey().intValue());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            saveGenres(film.getFilmId(), film.getGenres());
        }

        return getFilmById(film.getFilmId());
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET film_name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE film_id = ?";
        int updated = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getFilmId()
        );

        if (updated == 0) {
            throw new NotFoundException("Фильм с ID " + film.getFilmId() + " не найден");
        }

        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getFilmId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            saveGenres(film.getFilmId(), film.getGenres());
        }

        return getFilmById(film.getFilmId());
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT f.*, r.rating_id as mpa_id, r.rating_name as mpa_name " +
                "FROM films f " +
                "LEFT JOIN rating_mpa r ON f.rating_id = r.rating_id";
        List<Film> films = jdbcTemplate.query(sql, new FilmRowMapper());

        if (!films.isEmpty()) {
            loadGenresForFilms(films);
            loadLikesForFilms(films);
        }

        return films;
    }

    @Override
    public Film getFilmById(int filmId) {
        String sql = "SELECT f.*, r.rating_id as mpa_id, r.rating_name as mpa_name " +
                "FROM films f " +
                "LEFT JOIN rating_mpa r ON f.rating_id = r.rating_id " +
                "WHERE f.film_id = ?";
        List<Film> films = jdbcTemplate.query(sql, new FilmRowMapper(), filmId);

        if (films.isEmpty()) {
            throw new NotFoundException("Фильм с ID " + filmId + " не найден");
        }

        Film film = films.get(0);
        loadGenresAndLikes(film);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            List<Genre> sortedGenres = new ArrayList<>(film.getGenres());
            sortedGenres.sort(Comparator.comparingInt(Genre::getId));
            film.setGenres(new LinkedHashSet<>(sortedGenres));
        }

        return film;
    }

    public void addLike(int filmId, int userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT f.*, r.rating_id as mpa_id, r.rating_name as mpa_name, COUNT(l.user_id) as likes_count " +
                "FROM films f " +
                "LEFT JOIN rating_mpa r ON f.rating_id = r.rating_id " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY likes_count DESC " +
                "LIMIT ?";
        List<Film> films = jdbcTemplate.query(sql, new FilmRowMapper(), count);

        if (!films.isEmpty()) {
            loadGenresForFilms(films);
            loadLikesForFilms(films);
        }

        return films;
    }

    private class FilmRowMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            Film film = new Film();
            film.setFilmId(rs.getInt("film_id"));
            film.setName(rs.getString("film_name"));
            film.setDescription(rs.getString("description"));
            java.sql.Date releaseDate = rs.getDate("release_date");
            if (releaseDate != null) {
                film.setReleaseDate(releaseDate.toLocalDate());
            }
            film.setDuration(rs.getInt("duration"));

            int mpaId = rs.getInt("mpa_id");
            if (!rs.wasNull()) {
                film.setMpa(new MpaRating(mpaId, rs.getString("mpa_name")));
            }

            return film;
        }
    }
}
