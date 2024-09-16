$(document).ready(function() {
    // 페이지가 로드될 때마다 최신 유저 상태를 반영하는 함수 실행
    myStatus();

    // index에서 아이콘 요소를 클릭하면, 현재 로그인 중인 사용자 정보 불러옴
    let dropdown = document.getElementById('userDropdown');
    dropdown.addEventListener('click', myStatus);

    // 상태 변경 원 요소들을 가져옴
    let circles = document.querySelectorAll('.circle');

    // 상태 수정 원 요소들에 대한 클릭 이벤트 설정
    circles.forEach(function(element) {
        element.addEventListener('click', function() {

            // 이전 타이머 정리
            if (timerInterval) {
                clearInterval(timerInterval);
            }

            // 클릭된 원의 ID 가져오기
            let statusId = parseInt(element.id);
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
                url: '/member/changeMyStatus',
                method: 'POST',
                data: { statusId: statusId, duration: hours },
                success: function() {
                    myStatus(); // 상태 변경 후 최신 상태 다시 불러옴
                },
                error: function(error) {
                    console.error('상태 변경 실패:', error);
                }
            });

        });
    });


    // 상태 메시지 관련 요소들
    let editButton = document.getElementById('editStatusMessage');
    let statusMessageDiv = document.getElementById('statusMessage');
    let statusMessageInput = document.getElementById('statusMessageInput');
    let saveButton = document.getElementById('saveStatusMessage');
    let charCount = document.getElementById('charCount');

    // 수정 버튼 클릭 시
    editButton.addEventListener('click', function() {
        // 기존 상태 메시지를 텍스트박스에 넣기 - 기존 것에서 수정하도록
        statusMessageInput.value = statusMessageDiv.textContent.trim();

        // DIV 숨기고 텍스트박스와 저장 버튼, 입력 글자수 보이기
        statusMessageDiv.style.display = 'none';
        statusMessageInput.style.display = 'block';
        saveButton.style.display = 'inline';
        charCount.style.display = 'block';
    });

    // 입력 이벤트 리스너 등록
    statusMessageInput.addEventListener('input', function() {
        // 현재 입력된 문자의 수를 계산
        let charCountValue = statusMessageInput.value.length;

        // 화면에 문자 수와 최대 문자 수를 업데이트
        charCount.textContent = `${charCountValue}/200자`;
    });

    // 저장 버튼 클릭 시
    saveButton.addEventListener('click', function() {
        // 사용자가 새로 입력한 상태메시지
        let newStatusMessage = statusMessageInput.value;

        // 서버에 업데이트 요청 보내기
        fetch('/member/changeMyStatusMessage', {
            method: 'POST',
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
                myStatus();
            })
            .catch(error => {
                console.error('문제 발생:', error);
            });
    });

    // 알림 이벤트 감지해서 알림 보내기
    const eventSource = new EventSource('/notification');
    let data;  // 서버에서 보내온 친구 요청 데이터

    // 친구 요청 수신, 친구 요청 수락, 메시지 수신 이벤트
    const events = ['friendRequestReceive', 'friendRequestAccept', 'messageReceive'];

    // 모든 이벤트에 대해 동일한 처리
    events.forEach(event => {
        $(eventSource).on(event, function(e) {
            data = JSON.parse(e.data);
        });
    });

}); // end of $(document).ready(function() {})

// 사용자 최신 상태를 읽어와 반영할 element
let lastStUpdateDt = document.getElementById("lastStUpdateDt");
let stMessage = document.getElementById("statusMessage");
let profileImg = document.getElementById("profileImg");
let remainingTime = document.getElementById("remainingTime");

// 사용자 최신 상태 반환
function myStatus(){
    // 서버에 업데이트 요청 보내기
    fetch('/member/viewStatuses', {
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
            // lastStUpdateDt를 포맷팅해서 화면에 출력
            let formattedTime = formatTime(data.lastStUpdateDt);

            // UI 갱신
            lastStUpdateDt.textContent = formattedTime;
            stMessage.textContent = data.stMessage;

            // 상태에 따른 사용자 프로필 이미지 둘레 색 변경
            switch(data.statusId){
                case 1: profileImg.style.border = '6px solid black'; break;
                case 2: profileImg.style.border = '6px solid green'; break;
                case 3: profileImg.style.border = '6px solid yellow'; break;
                case 4: profileImg.style.border = '6px solid red'; break;
                default: console.log("statusId 오류");
            }


            // 남은 시간 업데이트
            if (data.endTime) {
                startTimer(new Date(data.endTime)); // endTime을 기준으로 타이머 시작
            }
        })
        .catch(error => {
            console.error('상태 읽어오기 문제 발생:', error);
        });
}

// 날짜 정보 포맷해서 띄우기
function formatTime(dateString) {
    // Date 객체 생성
    let date = new Date(dateString);

    // 연도, 월, 일, 시간, 분, 초 추출
    let year = date.getFullYear();
    let month = String(date.getMonth() + 1).padStart(2, '0');
    let day = String(date.getDate()).padStart(2, '0');
    let hours = String(date.getHours()).padStart(2, '0');
    let minutes = String(date.getMinutes()).padStart(2, '0');
    let seconds = String(date.getSeconds()).padStart(2, '0');

    // 포맷팅된 날짜와 시간 반환
    return `${year}/${month}/${day} / ${hours}:${minutes}:${seconds}`;
}

let timerInterval = null; // 전역 변수로 선언

// 타이머 시작
function startTimer(endTime) {
    function updateRemainingTime() {
        let now = new Date();
        let timeLeft = endTime - now;

        if (timeLeft <= 0) {
            remainingTime.textContent = '상태가 만료되었습니다.';
            profileImg.style.border = '6px solid black'; // 상태 만료 시 프로필 사진 테두리 색상 변경
            clearInterval(timerInterval); // 타이머 중지
        } else {
            let hoursLeft = Math.floor(timeLeft / (1000 * 60 * 60));
            let minutesLeft = Math.floor((timeLeft % (1000 * 60 * 60)) / (1000 * 60));
            let secondsLeft = Math.floor((timeLeft % (1000 * 60)) / 1000);
            remainingTime.textContent = `남은 시간: ${hoursLeft}시간 ${minutesLeft}분 ${secondsLeft}초`;
        }
    }

    // 타이머를 매 1초마다 업데이트
    let timerInterval = setInterval(updateRemainingTime, 1000);
    updateRemainingTime(); // 즉시 업데이트
}

// 알림 UI 갱신
function updateAlarmUI(data) {
    const alertsDropdown = document.querySelector("#alertsDropdown");
    const badgeCounter = alertsDropdown.querySelector(".badge-counter");
    const dropdownList = document.querySelector(".dropdown-list");

    const iconClass = getIconClass(data.categoryId);  // categoryId에 따라 아이콘 결정

    // 알림 개수 갱신
    const currentCount = parseInt(badgeCounter.textContent) || 0;
    badgeCounter.textContent = currentCount + 1;

    // 새로운 알림 추가
    const newAlarm = document.createElement("a");
    newAlarm.classList.add("dropdown-item", "d-flex", "align-items-center");
    newAlarm.innerHTML = `
        <div class="mr-3">
            <div class="icon-circle ${iconClass}">
                <i class="fas fa-file-alt text-white"></i>
            </div>
        </div>
        <div>
            <div class="small text-gray-500">${new Date().toLocaleDateString()}</div>
            <span class="font-weight-bold">${data.contents}</span>
        </div>
    `;

    // 알림 목록에 추가
    dropdownList.insertBefore(newAlarm, dropdownList.firstChild);
}

// 알림 아이콘 동적으로 적용
function getIconClass(categoryId) {
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