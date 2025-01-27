--liquibase formatted SQL
--changeset orkhoian:2
INSERT INTO users (name, username, password, api_key)
VALUES ('Aleksei Orkhoian', 'alex@example.com', '$2a$12$u6R8AN8th.xlHjEBYImWq.8lzobPZ6X2AAYHBldAw36MgYRaRo1P2', '${nulab-api-key-aleksei}'),
       ('John Doe', 'johndoe@gmail.com', '$2a$10$mA8xUqoCuELtEnJnqJrAiegOEU5QTxjuos9t1hZonQkPFIS/.NjYC', '${nulab-api-key-john}'),
       ('Bob Smith', 'mikesmith@yahoo.com', '$2a$10$RoV/CWD9cLcYMuv18PX9Xe9stypd2zk0CUHIw3ul6YlhD8M5z9EsK', null);

INSERT INTO tasks (title, description, status, expiration_date)
VALUES ('Buy cheese', null, 'TODO', '2024-01-29 12:00:00'),
       ('Do homework', 'Math, Physics, Literature', 'IN_PROGRESS', '2024-01-31 00:00:00'),
       ('Clean rooms', null, 'DONE', null),
       ('Call Mike', 'Ask about a meeting', 'TODO', '2024-02-01 00:00:00');

INSERT INTO users_tasks (task_id, user_id)
VALUES (1, 2),
       (2, 2),
       (3, 2),
       (4, 1);

INSERT INTO users_roles (user_id, role)
VALUES (1, 'ROLE_ADMIN'),
       (1, 'ROLE_USER'),
       (2, 'ROLE_USER');