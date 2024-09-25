$(document).ready(function () {

    // 검색 버튼 클릭 시 폼 제출
    $('#searchBtn').on('click', function() {
        pagingFormSubmit(1); // 여기서 pagingFormSubmit 함수 호출
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
$(document).ready(function(){

    // 친구 검색 버튼을 눌렀을 때 새 창을 열어 친구 목록 검색 창 띄움
    $('#writeMessageBtn').click(function(){
        window.open(	// 현재 창(부모창) 기준 자식창 새로 열기 - method="get"만 지원, "post"로 하고 싶으면, js 사용해야!
            // open() 함수: 괄호 안 4Param - 경로, 새로 열릴 창 이름(자유), value(default: _blank 새 창), 모양
            // http://localhost:8888/my/message/searchFriend -> 그치만 이렇게 열면, 부모창-자식창 관계가 성립되지 않음!
            '/my/message/write',
            'write',
            'top=150, left=150, width=600, height=450, location=no, titlebar=no');	// 시작 위치 & 크기
    })
})
