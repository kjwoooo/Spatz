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
        'admin',
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
        'user',
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
        'user',
        TRUE);
