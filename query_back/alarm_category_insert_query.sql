INSERT INTO alarm_category(category_name, category_name_ja) VALUES('쪽지알림', 'メッセージ通知');
INSERT INTO alarm_category(category_name, category_name_ja) VALUES('친구신청알림', '友達申請通知');
INSERT INTO alarm_category(category_name, category_name_ja) VALUES('친구수락알림', '友達承認通知');
INSERT INTO alarm_category(category_name, category_name_ja) VALUES('긴급상태알림', '緊急状態通知');
INSERT INTO friend_member (
    member_id,
    friend_id,
    friend_yn,
    favorite_yn,
    request_dt,
    accept_dt
) VALUES (
             'yyy',          -- member_id
             'exampleId12',          -- friend_id (친구의 ID 값)
             true,           -- friend_yn (친구 여부)
             true,          -- favorite_yn (즐겨찾기 여부)
             null,          -- request_dt (친구 요청 시간)
             NULL            -- accept_dt (수락 시간, 수락 전이므로 NULL)
         );
