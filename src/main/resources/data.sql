--users Table
INSERT INTO users (id, email, password, nickname, last_login, connected, marketing_agreed, role, activated)
values (
        11111,
        'admin@elice.com',
        '{bcrypt}$2a$10$27OCqEzbbIBf0HD6d6rJ4uftk6t9Eta.spFInZ6VhJPzQpgQXw4TK', -- 1234
        'nickname1',
        '2024-07-31 15:30:00',
        TRUE,
        TRUE,
        'ROLE_ADMIN',
        TRUE);

INSERT INTO users (id, email, password, nickname, last_login, connected, marketing_agreed, role, activated)
values (
        22222,
        'test@elice.com',
        '{bcrypt}$2a$10$27OCqEzbbIBf0HD6d6rJ4uftk6t9Eta.spFInZ6VhJPzQpgQXw4TK', -- 1234
        'nickname2',
        '2024-07-31 15:30:00',
        TRUE,
        TRUE,
        'ROLE_USER',
        TRUE);

INSERT INTO users (id, email, password, nickname, last_login, connected, marketing_agreed, role, activated)
values (
        33333,
        'xzxy6036@naver.com',
        '{bcrypt}$2a$10$27OCqEzbbIBf0HD6d6rJ4uftk6t9Eta.spFInZ6VhJPzQpgQXw4TK', -- 1234
        'blackjack96',
        '2024-07-31 15:30:00',
        TRUE,
        TRUE,
        'ROLE_USER',
        TRUE);

-- user profile image table
INSERT INTO USERS_PROFILE_IMAGE (id, user_id, image_url)
values (
        1,
        33333,
        'https://storage.googleapis.com/spatz-user-profile1/80e49c30-7295-4043-b8ad-ef7beb125ebd');
