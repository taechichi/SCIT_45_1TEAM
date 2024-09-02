

select * from shelter;

ALTER TABLE scit.status ADD status_name_ja varchar(10) NOT NULL;

-- 1. `favorite_id`가 프라이머리 키인지 확인합니다.
ALTER TABLE marker_favorites_test
    MODIFY COLUMN favorite_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY;


CREATE table marker_favorites_test AS
    SELECT  * from marker_favorites where 1=0;

INSERT INTO marker_favorites_test (member_id, hospital_id, shelter_id, nickname)
VALUES ('tsh0828', 1, NULL, '우리집 근처 자주가는 병원'),
       ('tsh082', NULL, 2, '제일 튼튼한 대피소'),
       ('sag', NULL, 2, '제일 튼튼한 대피소'),
       ('adg', NULL, 2, '제일 튼튼한 대피소'),
       ('d2', NULL, 2, '제일 튼튼한 대피소'),
       ('546', NULL, 2, '제일 튼튼한 대피소'),
       ('ad', NULL, 2, '제일 튼튼한 대피소'),
       ('dfadf', NULL, 2, '제일 튼튼한 대피소'),
       ('dgsg', NULL, 2, '제일 튼튼한 대피소'),
       ('3423', NULL, 2, '제일 튼튼한 대피소'),
       ('asdg23', NULL, 2, '제일 튼튼한 대피소'),
       ('sgsd66', NULL, 2, '제일 튼튼한 대피소'),
       ('346345325', NULL, 2, '제일 튼튼한 대피소'),
       ('34fadfas', NULL, 2, '제일 튼튼한 대피소'),
       ('23dfad66', NULL, 2, '제일 튼튼한 대피소'),
       ('adfa234ds', NULL, 2, '제일 튼튼한 대피소'),
       ('adhxcz', NULL, 2, '제일 튼튼한 대피소'),
       ('sdhfdss4', NULL, 2, '제일 튼튼한 대피소'),
       ('s1234235', NULL, 2, '제일 튼튼한 대피소'),
       ('adssbcbxz', NULL, 2, '제일 튼튼한 대피소'),
       ('fsadfasdf', NULL, 2, '제일 튼튼한 대피소'),
       ('2342313123', NULL, 2, '제일 튼튼한 대피소'),
       ('sa12', NULL, 2, '제일 튼튼한 대피소'),
       ('124dsads', NULL, 2, '제일 튼튼한 대피소'),
       ('1231', NULL, 2, '제일 튼튼한 대피소'),
       ('a12312', NULL, 2, '제일 튼튼한 대피소'),
       ('tsh082', NULL, 2, '제일 튼튼한 대피소'),
       ('tsh082', NULL, 2, '제일 튼튼한 대피소'),
       ('tsh082', NULL, 2, '제일 튼튼한 대피소'),
       ('tsh082', NULL, 2, '제일 튼튼한 대피소'),
       ('tsh082', NULL, 2, '제일 튼튼한 대피소'),
       ('tsh082', NULL, 2, '제일 튼튼한 대피소'),
       ('tsh082', NULL, 2, '제일 튼튼한 대피소'),
       ('tsh082', NULL, 2, '제일 튼튼한 대피소'),
       ('tsh082', NULL, 2, '제일 튼튼한 대피소'),
       ('tsh082', NULL, 2, '제일 튼튼한 대피소'),
       ('tsh082', NULL, 2, '제일 튼튼한 대피소'),
       ('tsh0828', null, 4, '가장 빨리 갈 수 있는 대피소'),
       ('tsh0828', null, 4, '가장 빨리 갈 수 있는 대피소'),
       ('tsh0828', null, 4, '가장 빨리 갈 수 있는 대피소'),
       ('tsh0828', null, 4, '가장 빨리 갈 수 있는 대피소'),
       ('tsh0828', null, 4, '가장 빨리 갈 수 있는 대피소'),
       ('tsh0828', null, 4, '가장 빨리 갈 수 있는 대피소'),
       ('tsh0828', null, 4, '가장 빨리 갈 수 있는 대피소'),
       ('tsh0828', null, 4, '가장 빨리 갈 수 있는 대피소'),
       ('tsh0828', null, 4, '가장 빨리 갈 수 있는 대피소'),
       ('tsh0828', null, 4, '가장 빨리 갈 수 있는 대피소'),
       ('tsh0828', null, 4, '가장 빨리 갈 수 있는 대피소'),
       ('tsh0828', null, 4, '가장 빨리 갈 수 있는 대피소'),
       ('tsh0828', null, 4, '가장 빨리 갈 수 있는 대피소'),
       ('tsh0828', null, 4, '가장 빨리 갈 수 있는 대피소'),
       ('tsh0828', null, 4, '가장 빨리 갈 수 있는 대피소'),
       ('tsh0828', null, 4, '가장 빨리 갈 수 있는 대피소'),
       ('tsh0828', null, 4, '가장 빨리 갈 수 있는 대피소');

select * from marker_favorites_test;