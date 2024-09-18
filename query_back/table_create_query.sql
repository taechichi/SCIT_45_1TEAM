CREATE TABLE status
(
    status_id   INTEGER     NOT NULL AUTO_INCREMENT,
    status_name VARCHAR(10) NOT NULL,
    status_name_ja VARCHAR(10) NOT NULL,
    PRIMARY KEY (status_id)
);

CREATE TABLE member
(
    member_id         VARCHAR(30)  NOT NULL,
    status_id         INTEGER      NOT NULL,
    password          VARCHAR(100) NOT NULL,
    nickname          VARCHAR(20)  NOT NULL,
    email             VARCHAR(50)  NOT NULL,
    gender            VARCHAR(1),
    phone             VARCHAR(20)  NOT NULL,
    nationality       VARCHAR(20)  NOT NULL,
    admin_yn          TINYINT(1) NOT NULL DEFAULT 0,
    withdraw          TINYINT(1) NOT NULL DEFAULT 0,
    last_login_dt     TIMESTAMP NULL,
    last_st_update_dt TIMESTAMP NULL,
    file_name         VARCHAR(50)  NOT NULL,
    st_message        VARCHAR(50) NULL,
    end_time          TIMESTAMP NULL,
    PRIMARY KEY (member_id),
    FOREIGN KEY (status_id) REFERENCES status (status_id)
);

CREATE TABLE hospital
(
    hospital_id    VARCHAR(500) NOT NULL,
    hospital_name  VARCHAR(200)  NOT NULL,
    longitude      VARCHAR(20)  NOT NULL,
    latitude       VARCHAR(20)  NOT NULL,
    lang_cd        VARCHAR(2)   NOT NULL,
    business_hours VARCHAR(30)  NULL,
    PRIMARY KEY (hospital_id)
);

CREATE TABLE shelter
(
    shelter_id         INTEGER     NOT NULL AUTO_INCREMENT,
    shelter_name       VARCHAR(500) NOT NULL,
    longitude          VARCHAR(20) NOT NULL,
    latitude           VARCHAR(20) NOT NULL,
    flood_yn           TINYINT(1) NOT NULL DEFAULT 0,
    earthquake_yn      TINYINT(1) NOT NULL DEFAULT 0,
    earth_yn           TINYINT(1) NOT NULL DEFAULT 0,
    hightide_yn        TINYINT(1) NOT NULL DEFAULT 0,
    fire_yn            TINYINT(1) NOT NULL DEFAULT 0,
    inland_flooding_yn TINYINT(1) NOT NULL DEFAULT 0,
    tsunami_yn         TINYINT(1) NOT NULL DEFAULT 0,
    volcano_yn         TINYINT(1) NOT NULL DEFAULT 0,
    lang_cd            VARCHAR(2)  NOT NULL,
    PRIMARY KEY (shelter_id)
);

CREATE TABLE tag_category
(
    tag_id   INTEGER     NOT NULL AUTO_INCREMENT,
    contents VARCHAR(30) NOT NULL,
    lang_cd  VARCHAR(2)  NOT NULL,
    PRIMARY KEY (tag_id)
);

CREATE TABLE realtime_comment
(
    comment_num INTEGER      NOT NULL AUTO_INCREMENT,
    reply_num   INTEGER NULL,
    nickname    VARCHAR(20)  NOT NULL,
    location    VARCHAR(100) NOT NULL,
    contents    VARCHAR(200) NOT NULL,
    create_dt   TIMESTAMP    NOT NULL DEFAULT NOW(),
    PRIMARY KEY (comment_num),
    FOREIGN KEY (reply_num) REFERENCES realtime_comment (comment_num)
);

CREATE TABLE marker_board
(
    board_id    INTEGER     NOT NULL AUTO_INCREMENT,
    member_id   VARCHAR(30) NOT NULL,
    shelter_id  Integer NULL,
    hospital_id VARCHAR(500) NULL,
    contents    VARCHAR(500) NULL,
    create_dt   TIMESTAMP   NOT NULL DEFAULT NOW(),
    delete_reason VARCHAR(50) NULL,
    delete_yn   TINYINT(1) NOT NULL DEFAULT 0,
    delete_dt   TIMESTAMP NULL,
    PRIMARY KEY (board_id),
    FOREIGN KEY (member_id) REFERENCES member (member_id),
    FOREIGN KEY (shelter_id) REFERENCES shelter (shelter_id),
    FOREIGN KEY (hospital_id) REFERENCES hospital (hospital_id)
);

CREATE TABLE guidebook_board
(
    guidebook_id INTEGER      NOT NULL AUTO_INCREMENT,
    member_id    VARCHAR(30)  NOT NULL,
    contents     VARCHAR(500) NOT NULL,
    create_dt    TIMESTAMP    NOT NULL DEFAULT NOW(),
    delete_yn    TINYINT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (guidebook_id),
    FOREIGN KEY (member_id) REFERENCES member (member_id)
);

CREATE TABLE comment_tag
(
    comment_tag_id INTEGER NOT NULL AUTO_INCREMENT,
    comment_num INTEGER NOT NULL,
    tag_id      Integer NOT NULL,
    PRIMARY KEY (comment_tag_id),
    FOREIGN KEY (comment_num) REFERENCES realtime_comment (comment_num),
    FOREIGN KEY (tag_id) REFERENCES tag_category (tag_id)
);

CREATE TABLE board_picture
(
    file_id      INTEGER       NOT NULL AUTO_INCREMENT,
    board_id     INTEGER NULL,
    guidebook_id INTEGER NULL,
    path         VARCHAR(5000) NOT NULL,
    ori_filename VARCHAR(30)   NOT NULL,
    new_filename VARCHAR(30)   NOT NULL,
    PRIMARY KEY (file_id),
    FOREIGN KEY (board_id) REFERENCES marker_board (board_id),
    FOREIGN KEY (guidebook_id) REFERENCES guidebook_board (guidebook_id)
);

CREATE TABLE alarm_category
(
    category_id   INTEGER     NOT NULL AUTO_INCREMENT,
    category_name       VARCHAR(10) NOT NULL,
    category_name_ja    VARCHAR(10) NOT NULL,
    PRIMARY KEY (category_id)
);

CREATE TABLE alarm
(
    alarm_id    INTEGER      NOT NULL AUTO_INCREMENT,
    category_id INTEGER      NOT NULL,
    member_id   VARCHAR(30)  NOT NULL,
    contents    VARCHAR(100) NOT NULL,
    alarm_dt    TIMESTAMP    NOT NULL DEFAULT NOW(),
    read_yn     TINYINT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (alarm_id),
    FOREIGN KEY (category_id) REFERENCES alarm_category (category_id),
    FOREIGN KEY (member_id) REFERENCES member (member_id)
);

CREATE TABLE friend_member
(
    relation_id INTEGER     NOT NULL AUTO_INCREMENT,
    member_id   VARCHAR(30) NOT NULL,
    friend_id   VARCHAR(30) NOT NULL,
    friend_yn   TINYINT(1) NOT NULL DEFAULT 0,
    favorite_yn TINYINT(1) NOT NULL DEFAULT 0,
    request_dt  TIMESTAMP NULL,
    accept_dt   TIMESTAMP NULL,
    PRIMARY KEY (relation_id),
    FOREIGN KEY (member_id) REFERENCES member (member_id),
    FOREIGN KEY (friend_id) REFERENCES member (member_id)
);

CREATE TABLE marker_favorites
(
    favorite_id INTEGER         NOT NULL AUTO_INCREMENT,
    member_id   VARCHAR(30)     NOT NULL,
    hospital_id VARCHAR(500)    NULL,
    shelter_id  Integer NULL,
    nickname    VARCHAR(20)     NULL,
    PRIMARY KEY (favorite_id),
    FOREIGN KEY (member_id) REFERENCES member (member_id),
    FOREIGN KEY (hospital_id) REFERENCES hospital (hospital_id),
    FOREIGN KEY (shelter_id) REFERENCES shelter (shelter_id)
);

CREATE TABLE message
(
    message_id INTEGER      NOT NULL AUTO_INCREMENT,
    sender_id     VARCHAR(30)  NOT NULL,
    receiver_id   VARCHAR(30)  NOT NULL,
    content       VARCHAR(500) NOT NULL,
    create_dt  TIMESTAMP    NOT NULL DEFAULT NOW(),
    delete_yn  TINYINT(1) NOT NULL DEFAULT 0,
    read_dt    TIMESTAMP,
    PRIMARY KEY (message_id),
    FOREIGN KEY (sender_id) REFERENCES member (member_id),
    FOREIGN KEY (receiver_id) REFERENCES member (member_id)
);
