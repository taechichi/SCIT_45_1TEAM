-- 한국어, 日本語
INSERT INTO status (status_name, status_name_ja) VALUES ('평상시', '平常');
INSERT INTO status (status_name, status_name_ja) VALUES ('안전', '安全');
INSERT INTO status (status_name, status_name_ja) VALUES ('대피중', '避難中');
INSERT INTO status (status_name, status_name_ja) VALUES ('위험', '緊急');

INSERT INTO scit.marker_board
(member_id, shelter_id, hospital_id, contents, create_dt, delete_yn, delete_reason, delete_dt)
VALUES('sds', null, null, '삭제된 게시글 테스트 12', CURRENT_TIMESTAMP, 1, '그냥 심심해서 지웠음', now());

INSERT INTO scit.hospital
(hospital_id , hospital_name, longitude, latitude, lang_cd, business_hours)
values('adljfalknvo209483adsgadsga0985209385','테스트 대형 병원', '123', '123', 'kr', '두시까지');

INSERT INTO scit.shelter
(shelter_name, longitude, latitude, flood_yn, earthquake_yn, earth_yn, hightide_yn, fire_yn, inland_flooding_yn, tsunami_yn, volcano_yn, lang_cd)
VALUES('테스트용 대피소 경기장', '123', '123', 0, 1, 1, 1, 0, 0, 0, 0, 'kr');

