$(document).ready(function() {
    $('#inputButton').on('click', use);
    $('#searchWord').on('keyup', list);
    list();

    // 동적으로 생성된 친구 리스트에 대한 이벤트 위임 - '.receiver'를 클릭하면 use() 함수 실행
    $(document).on("click", ".receiver", use);

    // 동적으로 생성된 친구 리스트에 대한 이벤트 위임 - '.delete'를 클릭하면 deleteUse() 함수 실행
    $(document).on("click", ".delete", deleteUse);

    // 부모창의 초기 상태를 저장할 변수
    let originalReceiverId = $(opener.document).find('#receiverId').val();
    let isCompleted = false; // 상태를 추적할 변수

    // 선택 완료 버튼 클릭 시, 친구 검색 내역이 부모창에 반영됨
    $("#completeSelect").on('click', function(){
        isCompleted = true; // 상태를 업데이트
        window.close(); // 창 닫기
    });

    // X버튼으로 내용을 반영하지 않고 창을 닫을 때
    $(window).on('unload', function() {
        if (!isCompleted) {
            // 창을 닫기 전 이벤트 - 부모창의 값을 search 창을 열기 전 값으로 되돌림
            $(opener.document).find('#receiverId').val(originalReceiverId);
        }
    });
});

// 부모창인 myMessageWriteForm의 '받는사람'에 id 추가
function use() {
    // 사용자가 전송하고자 하는 친구ID
    let id = $(this).attr('data-id');

    // 부모창의 '#receiverId' 입력 필드의 기존 값 가져오기
    let parentInput = $(opener.document).find('#receiverId');
    let currentValue = parentInput.val();

    // 현재 값이 비어있지 않으면, 기존 값을 배열로 변환
    let idsArray = currentValue ? currentValue.split(',').map(id => id.trim()) : [];

    // 이미 존재하는 ID인지 확인
    if (!idsArray.includes(id)) {
        idsArray.push(id); // 배열에 추가
        parentInput.val(idsArray.join(', ')); // 새로운 값으로 설정
    } else {
        alert(idAlreadyAddedMessage); // 사용자에게 알림
    }

}

// 부모창인 myMessageWriteForm의 '받는사람'에서 해당 id 제거
function deleteUse(){
    // 사용자가 전송을 취소하고자 하는 친구ID
    let idToDel = $(this).attr('data-id');

    // 부모창의 '#receiverId' 입력 필드의 기존 값 가져오기
    let parentInput = $(opener.document).find('#receiverId');
    let currentValue = parentInput.val();

    // 입력 필드에서 삭제할 ID를 찾기
    let idsArray = currentValue.split(',').map(id => id.trim()); // 현재 값을 배열로 변환하고 공백 제거

    // ID를 제거
    idsArray = idsArray.filter(id => id !== idToDel);

    // 새로운 값으로 설정
    parentInput.val(idsArray.join(', ')); // 다시 문자열로 합쳐서 입력 필드에 설정

}

function list(){
    $.ajax({
        url: 'friendList',
        type: 'post',
        data: { searchWord: $("#searchWord").val() },
        success: function(list){
            $('#output').empty();

            $(list).each(function(i, obj){
                let favoriteYn = obj.favoriteYn ? '즐겨찾기' : '일반';
                let gender = obj.gender == 'F' ? '여성' : '남성';
                let nationality = obj.nationality == 'JP' ? '일본' : '한국';

                // 프로필 사진 경로 생성
                let profileImgUrl = `/member/download/${obj.memberId}`;

                // HTML 생성
                let html = `
                    <tr>
                        <th>${i + 1}</th>
                        <th>${favoriteYn}</th>
                        <td><img class="profile-picture" alt="Profile Picture" src="${profileImgUrl}" /></td>
                        <td>${obj.memberId}</td>
                        <td>${obj.nickname}</td>
                        <td>${gender}</td>
                        <td>${nationality}</td>
                        <td class="receiver" data-id="${obj.memberId}">➕</td>
                        <td class="delete" data-id="${obj.memberId}">❌</td>
                    </tr>
                `;

                // 생성된 HTML을 테이블에 추가
                $('#output').append(html);

                // 방금 추가된 이미지에 CSS 속성 적용
                $('#output img').last().css({
                    'border-radius': '50%',
                    'width': '40px',
                    'height': '40px'
                });
            });
        },
        error: function(e){
            alert('조회 실패');
        }
    });
}


