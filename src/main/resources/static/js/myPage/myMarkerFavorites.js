let myLat;
let myLong;

$(document).ready(function () {

    // 도쿄역 JR선 위도(myLat), 경도(myLong)
    myLat = 35.68198566590147;
    myLong = 139.76721063068848;
    //console.log('초기 위치 설정:', '위도:', myLat, '경도:', myLong);

    // 모달이 닫힐 때 백드롭을 제거하는 이벤트 추가
    $('#promptNicknameModal').on('hidden.bs.modal', function () {
        forceCloseModal();  // 모달이 닫힐 때 백드롭을 강제로 제거
    });

    // 필터 버튼 클릭 시 필터 조건을 설정하고 마커 데이터를 가져옵니다.
    $('#filterAll').click(function (event) {
        event.preventDefault();
        //console.log('모두 보기 필터 클릭됨');
        $('#filter').val(''); // 필터를 제거
        fetchMarkers();
    });

    $('#filterHospital').click(function (event) {
        event.preventDefault();
        //console.log('병원 필터 클릭됨');
        $('#filter').val('hospital'); // 병원 필터
        fetchMarkers();
    });

    $('#filterShelter').click(function (event) {
        event.preventDefault();
        //console.log('대피소 필터 클릭됨');
        $('#filter').val('shelter'); // 대피소 필터
        fetchMarkers();
    });

    // 검색어 입력 시 실시간 검색
    $('#searchWord').on('keyup', function () {
        //console.log('검색어 입력됨:', $(this).val());
        fetchMarkers();
    });

    // 정렬 버튼 클릭 시 정렬 조건을 설정하고 마커 데이터를 가져옵니다.
    $('#sortByDistance').click(function (event) {
        event.preventDefault();
        //console.log('거리순 정렬 클릭됨');
        $('#sortBy').val('sortByDistance'); // 거리 정렬 설정
        fetchMarkers();
    });

    $('#sortByName').click(function (event) {
        event.preventDefault();
        //console.log('이름순 정렬 클릭됨');
        $('#sortBy').val('sortByName'); // 이름 정렬 설정
        fetchMarkers();
    });

    // 닉네임 수정 / 등록 기능
    $('#markerTableBody').on('click', 'a.nickname-exists, a.nickname-missing', function (event) {
        event.preventDefault();
        let favoriteId = $(this).closest('tr').find('input[name="favoriteId"]').val();
        let currentNickname = $(this).text();
        //console.log('닉네임 클릭됨:', 'favoriteId:', favoriteId, '현재 닉네임:', currentNickname);

        // 닉네임 입력 모달을 바로 띄우기
        showPromptNicknameModal(currentNickname, function(newNickname) {
            // 공란일 경우 닉네임 삭제
            if (newNickname === '') {
                newNickname = null;  // 서버에서는 null로 처리해 닉네임을 삭제
            }
            updateNickname(favoriteId, newNickname);
        });
        /*if (confirm(confirmNicknameUpdateMessage + currentNickname)) {
            let newNickname = prompt(nicknamePromptMessage, currentNickname);
            if (newNickname !== null) {
                updateNickname(favoriteId, newNickname);
            }
        }*/
    });

    // 닉네임이 없는 경우 클릭 시 등록할 수 있는 알림창을 띄움
    $('#markerTableBody').on('click', 'a.nickname-missing', function (event) {
        event.preventDefault(); // 기본 링크 클릭 동작 방지
        let favoriteId = $(this).closest('tr').find('input[name="favoriteId"]').val();

        // 닉네임 등록 모달 안내 후 닉네임 입력 모달로 이어짐
        showPromptNicknameModal('', function(newNickname) {
            if (newNickname === '') {
                newNickname = null;  // 공란 입력 시 등록하지 않음
            }
            updateNickname(favoriteId, newNickname);
        });

        /*if (confirm(confirmNicknameRegistrationMessage)) {
            let newNickname = prompt(nicknamePromptMessage);
            if (newNickname) {
                updateNickname(favoriteId, newNickname);
            }
        }*/
    });

    // 체크창 체크하면 전부 체크 되도록 하는 부분
    document.getElementById('selectAll').addEventListener('change', function () {
        // selectAll 체크박스가 변경될 때마다
        let checkboxes = document.querySelectorAll('input[name="favoriteId"]'); // 모든 체크박스 선택
        checkboxes.forEach(function(checkbox) {
            checkbox.checked = document.getElementById('selectAll').checked; // 모든 체크박스 상태를 selectAll의 상태로 설정
        });
    });

    // 닉네임 수정/등록을 서버로 전달하는 함수
    function updateNickname(favoriteId, newNickname) {
        //console.log('닉네임 업데이트 요청:', 'favoriteId:', favoriteId, '새 닉네임:', newNickname);
        $.ajax({
            url: '/my/myMarkerFavorites/updateNickname',
            type: 'POST',
            data: {
                favoriteId: favoriteId,
                nickname: newNickname,
            },
            success: function (response) {
                //console.log('닉네임 업데이트 성공:', response);
                fetchMarkers();
                showCustomAlert(updateNicknameSuccessMessage);  // 모달 사용
            },
            error: function (error) {
                //console.log('닉네임 업데이트 오류:', error);
                showCustomAlert(updateNicknameErrorMessage);  // 모달 사용
            }
        });
    }

    // 모달을 띄우는 함수 추가(alert)
    function showCustomAlert(message, callback) {
        document.getElementById('customAlertModalBody').textContent = message;
        var myModal = new bootstrap.Modal(document.getElementById('customAlertModal'), {});

        // '확인' 버튼 클릭 시 처리
        document.getElementById('confirmModalBtn').onclick = function () {
            if (typeof callback === 'function') {
                callback();  // 콜백 실행
            }
            myModal.hide();  // 모달 닫기
        };

        // 모달을 보여줌
        myModal.show();
    }

    // 닉네임 입력 모달을 띄우는 함수(prompt)
    function showPromptNicknameModal(currentNickname, callback) {
        document.getElementById('newNicknameInput').value = currentNickname || '';  // 기존 닉네임 또는 빈 값
        var promptModal = new bootstrap.Modal(document.getElementById('promptNicknameModal'), {});

        // 확인 버튼 클릭 시 처리 (submitNicknameBtn 사용)
        document.getElementById('submitNicknameBtn').onclick = function () {
            let newNickname = document.getElementById('newNicknameInput').value;

            // 공란일 경우 닉네임을 null로 설정하여 서버에서 삭제 처리
            if (newNickname.trim() === '') {
                newNickname = null;
            }

            if (typeof callback === 'function') {
                callback(newNickname);  // 입력값을 콜백 함수로 전달
            }
            promptModal.hide();  // 모달 닫기
        };

        // 취소 버튼 클릭 시 모달을 닫음
        document.getElementById('cancelNicknameBtn').onclick = function () {
            promptModal.hide();  // 모달 닫기
        };

        promptModal.show();  // 모달 띄우기
    }

    // confirm 모달을 띄우는 함수
    function showConfirmModal(message, callback) {
        document.getElementById('customAlertModalBody').textContent = message;
        var myModal = new bootstrap.Modal(document.getElementById('customAlertModal'), {});

        // '확인' 버튼 클릭 시 콜백 실행
        document.getElementById('confirmModalBtn').onclick = function () {
            if (typeof callback === 'function') {
                callback();  // 콜백 실행 (닉네임 입력 모달 띄우기)
            }
            myModal.hide();
        };

        myModal.show();  // 모달 열기
    }

    // 모달을 강제로 닫는 함수
    function forceCloseModal() {
        $('.modal-backdrop').remove();  // 모달 백드롭 제거
        $('body').removeClass('modal-open');  // 모달 열림 상태 클래스 제거
        $('body').css('padding-right', '');  // 모달 열림 시 적용된 패딩 제거
    }

    // =================================================================================

    // Ajax로 리스트 데이터를 불러오는 함수
    function fetchMarkers() {
        let formData = $('#pagingForm').serialize();
        formData += `&latitude=${myLat}&longitude=${myLong}`;
        //console.log('마커 데이터 요청:', formData);

        $.ajax({
            url: '/my/myMarkerFavorites', // 데이터를 요청할 URL
            type: 'GET',
            data: formData, // 폼 데이터를 직렬화하여 전송
            success: function (response) {
                //console.log('마커 데이터 수신 성공');
                // 서버로부터 받은 HTML 중 리스트 부분만 교체
                $('#markerTableBody').html($(response).find('#markerTableBody').html());
                $('#navigator').html($(response).find('#navigator').html());
            },
            error: function (error) {
                //console.log('마커 데이터 수신 오류:', error);
            }
        });
    }
});
