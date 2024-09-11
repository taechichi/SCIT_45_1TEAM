$(document).ready(function(){
    $('.acceptFriendBtn').on('click', acceptFriend);
    $('.rejectFriendBtn').on('click', rejectFriend);
    $('#toFriendList').on('click', toFriendList);
});

function acceptFriend(){
    let relationId = $(this).data('id');
    let flag = confirm('친구 신청을 수락하시겠습니까?');

    if(flag){
        $.ajax({
            url: '/my/friend/accept/' + relationId,
            type: 'POST',
            success: function() {
                location.reload(); // 수정 후 페이지를 새로고침하거나 테이블을 업데이트
            },
            error: function(xhr, status, error) {
                alert('친구 신청 수락에 실패했습니다.');
            }
        })
    }

}

function rejectFriend(){
    let relationId = $(this).data('id');
    let flag = confirm('친구 신청을 거절하시겠습니까?');

    if (flag){
        $.ajax({
            url: '/my/friend/reject/' + relationId,
            type: 'POST',
            success: function() {
                location.reload(); // 삭제 후 페이지를 새로고침하거나 테이블을 업데이트
            },
            error: function(xhr, status, error) {
                alert('친구 신청 거절에 실패했습니다.');
            }
        })
    }

}

function toFriendList() {
    // 친구 목록 페이지로 연결
    location.href = './';
}
