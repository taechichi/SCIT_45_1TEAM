$(document).ready(function(){
    $('.updateFavoriteBtn').on('click', updateFavorite);
    $('.deleteFriendBtn').on('click', deleteFriend);
    $('#toRequestList').on('click', toRequestList);
});

function updateFavorite(){
    let relationId = $(this).data('id');
    let favoriteYn = $(this).data('favorite');
    let flagMessage = favoriteYn ? '즐겨찾기 해제 하시겠습니까?' : '즐겨찾기 추가 하시겠습니까?';
    let flag = confirm(flagMessage);

    if(flag){
        $.ajax({
            url: '/my/friend/' + relationId,
            type: 'PATCH',
            success: function() {
                if(favoriteYn){
                    alert('즐겨찾기 해제가 성공적으로 되었습니다.');
                } else {
                    alert('즐겨찾기 추가가 성공적으로 되었습니다.');
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

document.addEventListener('DOMContentLoaded', function() {
    const btnFriendId = document.getElementById('btnFriendId');
    const btnNickname = document.getElementById('btnNickname');
    const typeSelect = document.getElementById('typeSelect');
    const searchInput = document.getElementById('searchInput');

    btnFriendId.addEventListener('click', function() {
        typeSelect.value = 'friendId';
        searchInput.placeholder = '아이디를 입력하세요';
    });

    btnNickname.addEventListener('click', function() {
        typeSelect.value = 'nickname';
        searchInput.placeholder = '닉네임을 입력하세요';
    });
});

