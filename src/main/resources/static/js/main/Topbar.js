document.addEventListener('DOMContentLoaded', function() {

    // 상태 변경 원 요소들을 가져옴
    let circles = document.querySelectorAll('.circle');

    // 클릭 이벤트를 각 원에 대해 설정합니다.
    circles.forEach(function(element) {
        element.addEventListener('click', function() {
            alert('클릭됨'); // 클릭 확인을 위한 alert

            // 클릭된 원의 ID 가져오기
            let statusId = parseInt(element.id);

            console.log('Clicked status ID:', statusId);

            // ajax 요청을 보냄
            $.ajax({
                url: '/member/changeMyStatus',
                method: 'POST',
                data: { statusId: statusId },
                success: function(response) {
                    // 서버에서 반환된 데이터를 처리합니다.
                    console.log('상태 변경 성공:', response);
                },
                error: function(error) {
                    console.error('상태 변경 실패:', error);
                }
            });
        });
    });






}); // end of DOMContentLoaded;

