$(document).ready(function() {
    $('#inputButton').on('click', use);
    $('#searchWord').on('keyup', list);
    list();

    // 동적으로 생성된 친구 리스트에 대한 이벤트 위임 - '.receiver'를 클릭하면 use() 함수 실행
    $(document).on("click", ".receiver", use);

    $("#completeSelect").on('click', function(){
        window.close();
    })
});

// 부모창인 myMessageWriteForm의 '받는사람'에 id 추가
function use() {
    let id = $(this).attr('data-id');

    // 부모창의 '#receiverId' 입력 필드에 현재 값 가져오기
    let parentInput = $(opener.document).find('#receiverId');
    let currentValue = parentInput.val();

    // 현재 값이 비어있지 않으면, 기존 값과 새 값을 구분하여 추가
    if (currentValue) {
        parentInput.val(currentValue + ', ' + id);
    } else {
        parentInput.val(id);
    }

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


