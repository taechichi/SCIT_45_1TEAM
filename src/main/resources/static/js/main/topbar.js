// 자식요소인 drop-down 메뉴 내 영역들을 클릭해도, 부모요소인 메뉴가 닫히지 않도록 이벤트 전파 중지
document.querySelectorAll('.dropdown-item').forEach(item => {
    item.addEventListener('click', function (event) {
        // 로그아웃 버튼이 아닐 경우에만 이벤트 전파를 중지
        if (!event.target.closest('a[data-toggle="modal"]')) {
            event.stopPropagation(); // 부모 요소로 이벤트 전파를 막음
        }
    });
});

$(document).ready(function () {
    // index에서 아이콘 요소를 클릭하면, 현재 로그인 중인 사용자 정보 불러옴
    let dropdown = document.getElementById('userDropdown');
    dropdown.addEventListener('click', myStatus);

    // 상태 변경 원 요소들을 가져옴
    let circles = document.querySelectorAll('.circle');

    // 상태 수정 원 요소들에 대한 클릭 이벤트 설정
    circles.forEach(function (element) {
        element.addEventListener('click', function () {

            // 이전 타이머가 존재하면 정리
            if (timerInterval) {
                clearInterval(timerInterval);
            }

            // 클릭된 원의 ID 가져오기
            let statusId = parseInt(element.getAttribute('data-id'));
            // 상태 유지 기본시간(사용자가 무효값을 입력했을 때 대비)
            let defaultValue = 1;

            // 사용자가 입력한 상태 유지 시간
            let hoursInput = document.getElementById('hours').value;
            let hours = parseInt(hoursInput, 10);   // 10진수로 변환
            if (isNaN(hours) || hours <= 0) {
                hours = defaultValue;
            }

            console.log('선택된 상태 ID(2:안전/3:도피중/4:긴급):', statusId);
            console.log('상태 지속 시간 (ms):', hours);

            // ajax 요청을 보냄
            $.ajax({
                url: '/member/change/status',
                method: 'PATCH',
                data: {statusId: statusId, duration: hours},
                success: function () {
                    myStatus(); // 상태 변경 후 최신 상태 다시 불러옴
                },
                error: function (error) {
                    console.error('상태 변경 실패:', error);
                }
            });

        });
    });


    // 상태 메시지 관련 요소들
    let editButton = document.getElementById('editStatusMessage');
    let statusMessageDiv = document.getElementById('statusMessage');
    let statusMessageInput = document.getElementById('statusMessageInput');
    let statusMessageInputDiv = document.getElementById('statusMessageInputDiv');
    let saveButton = document.getElementById('saveStatusMessage');
    let charCount = document.getElementById('charCount');

    // 수정 버튼 클릭 시
    editButton.addEventListener('click', function () {
        // 기존 상태 메시지를 텍스트박스에 넣기 - 기존 것에서 수정 가능하도록
        statusMessageInput.value = statusMessageDiv.textContent.trim();

        // DIV 숨기고 텍스트박스와 저장 버튼, 입력 글자수 보이기
        statusMessageDiv.style.display = 'none';
        statusMessageInputDiv.style.display = 'block';
        saveButton.style.visibility = 'visible'; // 저장 버튼 보이기
        charCount.style.visibility = 'visible'; // 문자 수 보이기
    });

    // 입력 이벤트 리스너 등록
    statusMessageInput.addEventListener('input', function () {
        // 현재 입력된 문자의 수를 계산
        let charCountValue = statusMessageInput.value.length;
        // 화면에 문자 수와 최대 문자 수를 업데이트
        charCount.textContent = `${charCountValue}/200자`;
    });

    // 저장 버튼 클릭 시
    saveButton.addEventListener('click', function () {
        // 사용자가 새로 입력한 상태메시지
        let newStatusMessage = statusMessageInput.value;

        // 서버에 업데이트 요청 보내기
        fetch('/member/change/smessage', {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: 'newStatusMessage=' + encodeURIComponent(newStatusMessage)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('서버 오류: ' + response.statusText);
                }
            })
            .then(() => {
                // 업데이트 성공 후 UI 갱신
                statusMessageDiv.textContent = newStatusMessage;
                statusMessageDiv.style.display = 'block';
                statusMessageInput.style.display = 'none';
                saveButton.style.display = 'none';
                charCount.style.visibility = 'hidden';
                myStatus();
            })
            .catch(error => {
                console.error('문제 발생:', error);
            });
    });

    // 알림 이벤트 감지해서 알림 보내기
    const eventSource = new EventSource('/notification');

    // 친구 요청 수신, 친구 요청 수락, 메시지 수신 이벤트
    $(eventSource).on('messageReceive friendRequestReceive friendRequestAccept', function (e) {
        const eventType = e.type;
        selectAlarmList(eventType);
    });

}); // end of $(document).ready(function() {})


// 사용자 최신 상태를 읽어와 반영할 element
let lastStUpdateDt = document.getElementById("lastStUpdateDt");
let stMessage = document.getElementById("statusMessage");
let profileImg = document.getElementById("statusProfileImg");
let remainingTime = document.getElementById("remainingTime");

let timerInterval = null; // 전역 변수로 선언

// 사용자 최신 상태 반환
function myStatus() {
    // 서버에 업데이트 요청 보내기
    fetch('/member/view/statuses', {
        method: 'GET',
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('서버 오류: ' + response.statusText);
            }
            return response.json();  // 서버 응답을 JSON 형식으로 변환
        })
        .then((data) => {
            // 사용자 상태 정보 뿌리기
            stMessage.textContent = data.stMessage;
            // 아이콘을 누른 시점 기준 상대적 시간 일회 표시 - 클릭 시 마다 업데이트됨
            lastStUpdateDt.textContent = data.lastStUpdateDt;
            // 상태에 따른 사용자 프로필 이미지 둘레 색 변경
            updateStatusColor(data.statusId);

            // 남은 시간 업데이트
            if (data.endTime) {
                // 이전 타이머가 존재하면 정리
                if (timerInterval) {
                    clearInterval(timerInterval);
                }
                let endTime = new Date(data.endTime).getTime();  // UTC 시간을 사용해 endTime 계산
                startTimer(endTime); // endTime을 기준으로 타이머 시작
            }
        })
        .catch(error => {
            console.error('상태 읽어오기 문제 발생:', error);
        });
}

// 프로필 이미지 업데이트 함수
function updateStatusColor(statusId) {
    switch (statusId) {
        case 1:
            profileImg.style.border = '6px solid black';
            break;
        case 2:
            profileImg.style.border = '6px solid green';
            break;
        case 3:
            profileImg.style.border = '6px solid yellow';
            break;
        case 4:
            profileImg.style.border = '6px solid red';
            break;
        default:
            console.log("statusId 오류");
    }
}

// 타이머 시작
function startTimer(endTime) {
    function updateRemainingTime() {
        let now = new Date();
        let timeLeft = endTime - now;

        if (timeLeft <= 0) {
            remainingTime.innerHTML = `${expiredMessage}`;
            profileImg.style.border = '6px solid black'; // 상태 만료 시 프로필 사진 테두리 색상 변경
            clearInterval(timerInterval); // 타이머 중지
        } else {
            let hoursLeft = Math.floor(timeLeft / (1000 * 60 * 60));
            let minutesLeft = Math.floor((timeLeft % (1000 * 60 * 60)) / (1000 * 60));
            let secondsLeft = Math.floor((timeLeft % (1000 * 60)) / 1000);
            remainingTime.innerHTML = `
            ${remainingMessage} ${hoursLeft}${hoursLabel} ${minutesLeft}${minutesLabel} ${secondsLeft}${secondsLabel}`;
        }
    }

    // 타이머를 매 1초마다 업데이트
    timerInterval = setInterval(updateRemainingTime, 1000);
    updateRemainingTime(); // 즉시 업데이트
}


// 알림 조회
function selectAlarmList(eventType) {
    // 서버에서 알림 데이터를 조회
    $.ajax({
        url: '/notification/list',  // 알림 데이터 조회 URL
        data: {
            eventType: eventType
        },
        method: 'GET',
        success: function (response) {
            updateAlarmUI(response, eventType);
        },
        error: function (xhr, status, error) {
            console.error('알림 데이터를 가져오는 중 오류 발생:', error);
        }
    });
}

// 알림 UI 갱신
function updateAlarmUI(response, eventType) {
    const $alertsDropdown = $("#alertsDropdown");
    const $alarmBadgeCounter = $alertsDropdown.find(".alarm-badge");

    // 알림 개수 갱신
    updateCnt($alarmBadgeCounter);

    if (eventType === "messageReceive") {
        const $messageDropdown = $("#messagesDropdown");
        const $messageBadgeCounter = $messageDropdown.find(".message-badge");
        updateCnt($messageBadgeCounter);
        updateMessageDropdown(response.messageList[0]);
    }

    updateAlarmDropdown(response.alarmList[0]);
}

// 알림 드롭다운 갱신
function updateAlarmDropdown(alarm) {
    const $alarmTarget = $(".alarm-list-header");

    const newAlarm = $(`
        <a class="dropdown-item d-flex align-items-center">
            <div class="mr-3">
                <div class="icon-circle ${getIconBgClass(alarm.categoryId)}">
                    <i class="fas ${getIconClass(alarm.categoryId)} text-white"></i>
                </div>
            </div>
            <div>
                <span class="font-weight-bold">${alarm.contents}</span>
                <div class="small text-gray-500">${formatDate(alarm.alarmDt)}</div>
            </div>
        </a>
    `);

    $alarmTarget.after(newAlarm);
}

function updateMessageDropdown(message) {
    const $messageTarget = $(".message-list-header");

    const newMessage = $(`
        <a class="dropdown-item d-flex align-items-center" href="/my/message/${message.messageId}">
            <div class="dropdown-list-image mr-3">
                <img class="rounded-circle" src="/member/download/${message.senderId}" alt="Profile Picture" />
                <div class="status-indicator ${getStatusIndicatorClass(message.statusId)}"></div>
            </div>
            <div class="font-weight-bold">
                <div class="text-truncate">${message.content}</div>
                <div class="span-container">
                    <span class="small text-gray-500">${message.senderId}</span>
                    <span class="small text-gray-500">${formatDate(message.createDt)}</span>
                </div>
            </div>
        </a>
    `);

    $messageTarget.after(newMessage);

    // 메시지 목록 링크 업데이트
    $(".dropdown-item.text-center.small.text-gray-500").attr("href", "/my/message");
}

// 알림 아이콘 배경 동적으로 적용
function getIconBgClass(categoryId) {
    if (categoryId === 1) {
        return "bg-primary";
    } else if (categoryId === 2 || categoryId === 3) {
        return "bg-success";
    } else if (categoryId === 4) {
        return "bg-warning";
    } else {
        return "bg-primary";
    }
}

// 알림 아이콘 동적으로 적용
function getIconClass(categoryId) {
    if (categoryId === 1) {
        return "fa-file-alt";
    } else if (categoryId === 2 || categoryId === 3) {
        return "fa-fw fa-user";
    } else if (categoryId === 4) {
        return "fa-exclamation-triangle";
    } else {
        return "fa-file-alt";
    }
}

function getStatusIndicatorClass(statusId) {
    if (statusId === 1) {
        return "";
    } else if (statusId === 2) {
        return "bg-success";
    } else if (statusId === 3) {
        return "bg-warning";
    } else if (statusId === 4) {
        return "bg-gradient-danger";
    } else {
        return "";
    }
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('ko-KR', {year: 'numeric', month: '2-digit', day: '2-digit'}).replace(/\./g, '.');
}

function updateCnt($badgeCounter) {
    let cnt = parseInt($badgeCounter.text()) || 0;
    $badgeCounter.text(cnt + 1);
}
document.addEventListener('DOMContentLoaded', function () {
    $('#profileModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget); // 모달을 트리거한 버튼
        var memberId = button.data('memberid'); // data-memberid
        var nickname = button.data('nickname'); // data-nickname
        var statusId = button.data('statusid'); // data-statusid
        var statusName = button.data('statusname'); // data-statusname
        var lastUpdate = button.data('lastupdate'); // data-lastupdate
        var stMessage = button.data('stmessage'); // data-stmessage
        var imgUrl = button.data('imgurl'); // data-imgurl

        // 모달 내부에 데이터 설정
        var modal = $(this);

        // 헤더와 본문에 정보 설정
        modal.find('.modal-header #headerNickname').text(nickname ? nickname : 'No nickname');
        modal.find('.modal-header #headerStatusName').text(statusName ? statusName : 'Unknown status');
        modal.find('.modal-body #modalMemberId').text(memberId ? memberId : 'No ID');
        modal.find('.modal-body #modalNickname').text(nickname ? nickname : 'No nickname');
        modal.find('.modal-body #modalStatusName').text(statusName ? statusName : 'Unknown status');
        modal.find('.modal-body #modalLastUpdate').text(lastUpdate ? lastUpdate : 'No update available');
        modal.find('.modal-body #modalProfileImage').attr('src', imgUrl ? imgUrl : '/default-profile.png');
        modal.find('.modal-body #modalStMessage').text(stMessage ? stMessage : 'No status message');

        // 상태 표시기 색상 변경 (헤더와 본문에 적용)
        var statusIndicator = modal.find('.modal-body .status-indicator');
        var statusText = modal.find('.modal-body #modalStatusName');
        var headerStatusText = modal.find('.modal-header #headerStatusName');
        statusIndicator.removeClass('bg-success bg-warning bg-gradient-danger');
        statusText.removeClass('text-success text-warning text-danger');
        headerStatusText.removeClass('text-success text-warning text-danger');

        // Close 버튼 색상 변경
        var closeButton = modal.find('.modal-footer .btn-secondary');
        closeButton.removeClass('btn-success btn-warning btn-danger'); // 기존 색상 제거

        if (statusId == 2) {
            statusIndicator.addClass('bg-success');
            statusText.addClass('text-success');
            headerStatusText.addClass('text-success');
            closeButton.addClass('btn-success'); // 상태에 맞게 Close 버튼 색상 변경
        } else if (statusId == 3) {
            statusIndicator.addClass('bg-warning');
            statusText.addClass('text-warning');
            headerStatusText.addClass('text-warning');
            closeButton.addClass('btn-warning'); // 상태에 맞게 Close 버튼 색상 변경
        } else if (statusId == 4) {
            statusIndicator.addClass('bg-gradient-danger');
            statusText.addClass('text-danger');
            headerStatusText.addClass('text-danger');
            closeButton.addClass('btn-danger'); // 상태에 맞게 Close 버튼 색상 변경
        }
    });
});