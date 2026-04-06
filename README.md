# Filmorate Database Schema

## Схема базы данных (https://dbdiagram.io/d/69c2e7b6fb2db18e3bf5d658)

## Описание таблиц

`users` Хранит информацию о пользователях (email, логин, имя, дата рождения) 
`films` Хранит информацию о фильмах (название, описание, дата релиза, продолжительность) 

`mpa_ratings` Рейтинги MPA (G, PG, PG-13, R, NC-17) 
`genres` Жанры фильмов (Комедия, Драма, Боевик и т.д.)
`friendship_statuses` Статусы дружбы (UNCONFIRMED, CONFIRMED) 

`friendships` Связывает пользователей в дружбу с указанием статуса
`film_genres` Связывает фильмы с жанрами (many-to-many)
`likes` Хранит лайки пользователей к фильмам


## Примеры SQL-запросов

### Получение всех фильмов с жанрами и рейтингом

```sql
SELECT 
    f.film_id,
    f.name,
    f.description,
    f.release_date,
    f.duration,
    m.mpa_name,
    GROUP_CONCAT(g.genre_name ORDER BY g.genre_id SEPARATOR ', ') AS genres
FROM films f
LEFT JOIN mpa_ratings m ON f.mpa_id = m.mpa_id
LEFT JOIN film_genres fg ON f.film_id = fg.film_id
LEFT JOIN genres g ON fg.genre_id = g.genre_id
GROUP BY f.film_id
ORDER BY f.film_id;
