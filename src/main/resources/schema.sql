CREATE TABLE IF NOT EXISTS filmorate_user
(
    user_id
        IDENTITY
    PRIMARY
    KEY,
    user_login
    varchar
(
    20
) ,
    name varchar
(
    50
),
    birthday date,
    email varchar
(
    50
)
    );

CREATE TABLE IF NOT EXISTS filmorate_friendship
(
    from_user
    integer,
    to_user
    integer,
    status_id
    integer,
    PRIMARY
    KEY
(
    from_user,
    to_user
),
    UNIQUE
(
    from_user,
    to_user
)
    );

CREATE TABLE IF NOT EXISTS filmorate_friendship_status
(
    status_id
    INTEGER
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    status_name
    varchar
    UNIQUE
);

CREATE TABLE IF NOT EXISTS filmorate_film
(
    film_id
    INTEGER
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    title
    varchar
(
    50
),
    description varchar
(
    200
),
    release_date date,
    duration_minutes integer,
    rating_mpa integer,
    UNIQUE
(
    title,
    description,
    release_date,
    duration_minutes,
    rating_mpa
)
    );

CREATE TABLE IF NOT EXISTS filmorate_film_genre
(
    genre_id
    INTEGER,
    film_id
    INTEGER,
    PRIMARY
    KEY
(
    genre_id,
    film_id
),
    UNIQUE
(
    genre_id,
    film_id
)
    );

CREATE TABLE IF NOT EXISTS filmorate_genre
(
    genre_id
    INTEGER
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    genre
    varchar
(
    50
) UNIQUE
    );

CREATE TABLE IF NOT EXISTS filmorate_mpa_rating
(
    rating_id
    INTEGER
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    PRIMARY
    KEY,
    rating
    varchar
(
    50
) UNIQUE
    );

CREATE TABLE IF NOT EXISTS filmorate_like
(
    film_id
    integer,
    user_id
    integer,
    PRIMARY
    KEY
(
 film_id,
 user_id
    ),
    UNIQUE
        (
         film_id,
         user_id
            )
);

ALTER TABLE filmorate_like
    ADD FOREIGN KEY (user_id) REFERENCES filmorate_user (user_id) ON DELETE CASCADE;

ALTER TABLE filmorate_like
    ADD FOREIGN KEY (film_id) REFERENCES filmorate_film (film_id) ON DELETE CASCADE;

ALTER TABLE filmorate_film
    ADD FOREIGN KEY (rating_mpa) REFERENCES filmorate_mpa_rating (rating_id) ON DELETE CASCADE;

ALTER TABLE filmorate_friendship
    ADD FOREIGN KEY (from_user) REFERENCES filmorate_user (user_id) ON DELETE CASCADE;

ALTER TABLE filmorate_friendship
    ADD FOREIGN KEY (to_user) REFERENCES filmorate_user (user_id) ON DELETE CASCADE;

ALTER TABLE filmorate_friendship
    ADD FOREIGN KEY (status_id) REFERENCES filmorate_friendship_status (status_id) ON DELETE CASCADE;

ALTER TABLE filmorate_film_genre
    ADD FOREIGN KEY (film_id) REFERENCES filmorate_film (film_id) ON DELETE CASCADE;

ALTER TABLE filmorate_film_genre
    ADD FOREIGN KEY (genre_id) REFERENCES filmorate_genre (genre_id) ON DELETE CASCADE;
