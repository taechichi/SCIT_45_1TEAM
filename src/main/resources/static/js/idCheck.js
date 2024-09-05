
    $(document).ready(function() {
    // ID 사용 버튼 클릭 시
    $('#inputButton').click(function() {
        let id = $(this).data('id');
        if (id) {
            if (window.opener) {
                $(window.opener.document).find('#memberId').val(id);
                window.close();
            } else {
                alert('부모 창을 찾을 수 없습니다.');
            }
        } else {
            alert('ID가 설정되지 않았습니다.');
        }
    });

    // 실시간 유효성 검사
    $('#searchId').on('input', function() {
    let searchId = $(this).val();
    let errorMessage = '';

    // 글자 수 검사
    if (searchId.length < 3 || searchId.length > 10) {
    errorMessage = '아이디는 3자 이상 10자 이하로 입력해 주세요.';
} else {
    // 특수문자 검사
    let regex = /[^a-zA-Z0-9]/; // 알파벳과 숫자 외의 문자
    if (regex.test(searchId)) {
    errorMessage = '아이디는 알파벳과 숫자만 포함할 수 있습니다.';
}
}

    // 유효성 검사 결과에 따라 메시지 표시
    if (errorMessage) {
    $('#error-message').text(errorMessage).addClass('error').removeClass('valid');
} else {
    $('#error-message').text('').removeClass('error').addClass('valid');
}
});
});
