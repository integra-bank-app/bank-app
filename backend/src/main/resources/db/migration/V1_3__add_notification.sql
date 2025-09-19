CREATE TABLE notification
(
    id      UUID NOT NULL,
    user_id UUID,
    type    SMALLINT,
    message VARCHAR(255),
    CONSTRAINT pk_notification PRIMARY KEY (id)
);

ALTER TABLE notification
    ADD CONSTRAINT FK_NOTIFICATION_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);