$(document).ready(function () {
    // 비밀번호 표시/숨기기 기능
    $('.password-toggle-icon').on('mousedown', function () {
        $('#password').attr('type', 'text');
    }).on('mouseup mouseleave', function () {
        $('#password').attr('type', 'password');
    });

    // 비밀번호 유효성 검사
    $('#joinForm').on('submit', function (event) {
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
    $('#file').change(function () {
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
                reader.onload = function (e) {
                    $('#profilePreview').attr('src', e.target.result).show();
                };
                reader.readAsDataURL(file);
            }
        }
    });
    $(document).ready(function () {
        // 페이지 로드 시 모달 자동 표시
        $('#passwordCheckModal').modal('show');

        // 비밀번호 입력 값이 변경될 때 resultMessage를 초기화
        $('#searchPw').on('input', function () {
            $('#resultMessage').html(''); // resultMessage 영역 초기화
        });

        // 엔터 키로 폼 제출을 방지하고 비밀번호 확인 처리
        $('#passwordForm').on('keypress', function (e) {
            if (e.which === 13) { // 13은 엔터 키의 키 코드입니다
                e.preventDefault(); // 기본 동작 방지
                $('#searchButton').click(); // 비밀번호 확인 버튼 클릭
            }
        });

        // 비밀번호 확인 버튼 클릭 시 동작
        $('#searchButton').on('click', function () {
            var searchPw = $('#searchPw').val();

            // 비밀번호 입력 유효성 검사
            if (searchPw.trim() === '') {
                $('#resultMessage').html('<p style="color:red;">비밀번호를 입력하세요.</p>');
                return;
            }

            // AJAX 요청 보내기
            $.ajax({
                url: 'passwordCheck', // 컨트롤러의 PostMapping URL
                type: 'POST',
                data: {searchPw: searchPw},
                success: function (response) {
                    // 서버에서 반환된 결과에 따라 메시지 표시
                    if (response.result) { // 비밀번호가 일치하는 경우
                        $('#resultMessage').html('<p style="color:green;">비밀번호가 일치합니다.</p>');
                        // 비밀번호가 맞으면 모달을 닫음
                        $('#passwordCheckModal').modal('hide');
                    } else {
                        // 비밀번호가 일치하지 않는 경우
                        $('#resultMessage').html('<p style="color:red;">비밀번호가 일치하지 않습니다.</p>');
                    }
                },
                error: function () {
                    $('#resultMessage').html('<p style="color:red;">오류가 발생했습니다. 다시 시도하세요.</p>');
                }
            });
        });
    });


    $('#joinForm').on('submit', function () {
        var isChecked = $('#withdrawCheckbox').is(':checked');
        $('<input>').attr({
            type: 'hidden',
            name: 'withdraw',
            value: isChecked
        }).appendTo('#joinForm');
    });

});
