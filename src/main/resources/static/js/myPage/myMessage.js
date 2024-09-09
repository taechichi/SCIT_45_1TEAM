$(document).ready(function() {
    // 이벤트 리스너 등록
    $('#deleteMessageBtn').on('click', deleteSelectedMessage);
    $('#selectAll').on('click', toggleSelectAll);
});

// '전체 선택' 체크박스 토글 기능
function toggleSelectAll() {
    let isChecked = $(this).is(':checked');
    $('.messageCheckbox').prop('checked', isChecked);
}

// 쪽지 다건 일괄 삭제 (쪽지 목록의 체크박스로 선택해서 삭제)
function deleteSelectedMessage() {
    let selectedMessageIdList = [];

    // 체크된 메시지 ID를 배열에 수집
    $('.messageCheckbox:checked').each(function() {
        selectedMessageIdList.push($(this).val());
    });

    // 선택된 메시지가 없을 경우 경고
    if (selectedMessageIdList.length === 0) {
        alert('삭제할 항목을 선택하세요.');
        return;
    }

    // 삭제 확인 메시지
    if (confirm('선택한 메시지를 삭제하시겠습니까?')) {
        // 서버에 삭제 요청
        $.ajax({
            url: '/my/message/list', // 서버에서 처리할 URL
            type: 'DELETE',
            contentType: 'application/json',
            data: JSON.stringify(selectedMessageIdList), // 선택된 메시지 ID 전송
            success: function(response) {
                alert('메시지가 삭제되었습니다.');
                location.reload(); // 삭제 후 페이지를 새로고침하거나 테이블을 업데이트
            },
            error: function(xhr, status, error) {
                alert('삭제 중 오류가 발생했습니다: ' + error);
            }
        });
    }
}