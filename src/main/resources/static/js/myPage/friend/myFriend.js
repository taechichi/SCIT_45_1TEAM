$(document).ready(function(){
    $('.updateFavoriteBtn').on('click', updateFavorite);
    $('.deleteFriendBtn').on('click', deleteFriend);
    $('#toRequestList').on('click', toRequestList);
});

function updateFavorite(){
    let relationId = $(this).data('id');
    let favoriteYn = $(this).data('favorite');
    let flagMessage = favoriteYn ? 'お気に入りを解除しますか。' : 'お気に入りを追加しますか。';
    let flag = confirm(flagMessage);

    if(flag){
        $.ajax({
            url: '/my/friend/' + relationId,
            type: 'PATCH',
            success: function() {
                if(favoriteYn){
                    alert('お気に入りの解除が正常に完了しました。');
                } else {
                    alert('お気に入りの追加が正常に完了しました。');
                }
                location.reload(); // 수정 후 페이지를 새로고침하거나 테이블을 업데이트
            },
            error: function(xhr, status, error) {
                alert('즐겨찾기 상태 수정에 실패했습니다.');
            }
        })
    }

}

function deleteFriend(){
    let friendId = $(this).data('id');
    let flag = confirm('친구 삭제하시겠습니까?');

    if (flag){
        $.ajax({
            url: '/my/friend/' + friendId,
            type: 'DELETE',
            success: function() {
                alert('친구 삭제가 성공적으로 되었습니다.');
                location.reload(); // 삭제 후 페이지를 새로고침하거나 테이블을 업데이트
            },
            error: function(xhr, status, error) {
                alert('친구 삭제에 실패했습니다.');
            }
        })
    }

}

function toRequestList() {
    // 나에게 온 친구 신청 목록 페이지로 연결
    location.href = './friend/request';
}