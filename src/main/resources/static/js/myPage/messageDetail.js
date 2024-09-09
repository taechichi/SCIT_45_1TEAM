$(document).ready(function() {
    // 이벤트 리스너 등록
    $('#viewMessageListBtn').on('click', toBoardList);
    $('#deleteMessageBtn').on('click', deleteMessage);
});

// 목록으로 이동
function toBoardList() {
    // 목록 페이지로 연결
    // './' 로 처리하면 http://localhost:8888/my/message/ 로 이동하면서 상세보기 요청이 들어감 (맨 마지막 / 때문에)
    // http://localhost:8888/my/message 로 이동해야하기 때문에 한 단계 더 내려가서 다시 board 요청을 보낸 것임
    location.href = '../message';
}

// 쪽지 단건 삭제 (상세보기에서)
function deleteMessage() {
    let messageId = $(this).data('id');
    let flag = confirm('쪽지를 삭제 하시겠습니까?');

    if (flag) {
        $.ajax({
            url: '/my/message/' + messageId,
            type: 'DELETE',
            success: function() {
                alert('삭제가 성공적으로 되었습니다.');
            },
            error: function(xhr, status, error) {
                alert('Error updating deleteYn: ' + error);
            }
        });
        // 쪽지 메뉴로 재 연결
        location.href = '../message';
    }
}