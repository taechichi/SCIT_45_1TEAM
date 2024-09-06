
    $(document).ready(function() {
    // 비밀번호 표시/숨기기 기능
    $('.password-toggle-icon').on('mousedown', function() {
        $('#password').attr('type', 'text');
    }).on('mouseup mouseleave', function() {
        $('#password').attr('type', 'password');
    });

    // 비밀번호 유효성 검사
    $('#joinForm').on('submit', function(event) {
    let password = $('#password').val();
    let errorMessage = '';

    // 비밀번호 길이와 특수문자 검사
    if (password.length < 8 || password.length >= 20) {
    errorMessage = '비밀번호는 8자 이상 20자 미만이어야 합니다.';
} else {
    let regex = /[!@#$%^&*(),.?":{}|<>]/; // 허용하는 특수문자
    if (!regex.test(password)) {
    errorMessage = '비밀번호는 특수문자를 포함해야 합니다.';
}
}

    // 유효성 검사 결과에 따라 메시지 표시
    if (errorMessage) {
    $('#error-message').text(errorMessage).addClass('error').removeClass('valid');
    event.preventDefault(); // 폼 제출 방지
} else {
    $('#error-message').text('').removeClass('error').addClass('valid');
}
});

    // 프로필 사진 용량 제한 체크 및 미리보기 기능
    $('#file').change(function() {
    let file = this.files[0];
    if (file) {
    let maxSize = 5 * 1024 * 1024; // 5MB
    let filePath = file.name;
    let allowedExtensions = /(\.jpg|\.jpeg|\.png)$/i;

    if (file.size > maxSize) {
    $('#fileError').text('프로필 사진은 5MB를 초과할 수 없습니다.').addClass('error').removeClass('valid');
    $(this).val(''); // 파일 입력 초기화
} else if (!allowedExtensions.exec(filePath)) {
    $('#fileError').text('허용된 파일 형식이 아닙니다. (.jpg, .jpeg, .png)').addClass('error').removeClass('valid');
    $(this).val(''); // 파일 입력 초기화
} else {
    $('#fileError').text('').removeClass('error').addClass('valid');
    // 파일 미리보기
    let reader = new FileReader();
    reader.onload = function(e) {
    $('#profilePreview').attr('src', e.target.result).show();
};
    reader.readAsDataURL(file);
}
}
});

    // ID 중복 확인 버튼 클릭 시 새 창 열기
    $('#checkDuplicateId').click(function() {
    window.open('idCheck', 'idwin', 'left=500,top=300,width=400,height=300,location=no,titlebar=no');
});
});
