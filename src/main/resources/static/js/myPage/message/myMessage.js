$(document).ready(function () {
// 검색 유형이 변경될 때 placeholder 업데이트
    $('#type').on('change', function() {
        var selectedType = $(this).val();

        if (selectedType === 'senderId') {
            $('#searchWord').attr('placeholder', '작성자로 검색하세요');
        } else if (selectedType === 'content') {
            $('#searchWord').attr('placeholder', '내용으로 검색하세요');
        }
    });

    // 검색 버튼 클릭 시 폼 제출
    $('#searchBtn').on('click', function() {
        pagingFormSubmit(1); // 여기서 pagingFormSubmit 함수 호출
    });

    // 버튼 클릭 시 활성화 효과
    $('#writeMessageBtn').on('click', function() {
        location.href = '/my/message/write'; // 글쓰기 페이지로 이동
    });

    $('#deleteMessageBtn').on('click', function() {
        let selectedMessageIdList = [];
        $('.messageCheckbox:checked').each(function () {
            selectedMessageIdList.push($(this).val());
        });

        if (selectedMessageIdList.length === 0) {
            alert('삭제할 항목을 선택하세요.');
            return;
        }

        if (confirm('선택한 메시지를 삭제하시겠습니까?')) {
            $.ajax({
                url: '/my/message/list',
                type: 'DELETE',
                contentType: 'application/json',
                data: JSON.stringify(selectedMessageIdList),
                success: function (response) {
                    alert('메시지가 삭제되었습니다.');
                    location.reload();
                },
                error: function (xhr, status, error) {
                    alert('삭제 중 오류가 발생했습니다: ' + error);
                }
            });
        }
    });

    // '전체 선택' 체크박스 토글 기능
    $('#selectAll').on('click', function() {
        let isChecked = $(this).is(':checked');
        $('.messageCheckbox').prop('checked', isChecked);
    });
});
