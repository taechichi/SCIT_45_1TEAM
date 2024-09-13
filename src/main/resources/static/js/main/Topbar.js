document.addEventListener('DOMContentLoaded', function() {
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

    let editButton = document.getElementById('editStatusMessage');
    let statusMessageDiv = document.getElementById('statusMessage');
    let statusMessageInput = document.getElementById('statusMessageInput');
    let saveButton = document.getElementById('saveStatusMessage');

    // 수정 버튼 클릭 시
    editButton.addEventListener('click', function() {
        // 기존 상태 메시지를 텍스트박스에 넣기 - 기존 것에서 수정하도록
        statusMessageInput.value = statusMessageDiv.textContent.trim();

        // DIV 숨기고 텍스트박스와 저장 버튼 보이기
        statusMessageDiv.style.display = 'none';
        statusMessageInput.style.display = 'block';
        saveButton.style.display = 'inline';
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
                // 여기에 응답을 처리하는 코드가 필요 없다면 비워도 됩니다.
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


}); // end of DOMContentLoaded;

// 사용자 최신 상태를 읽어와 반영할 element
let lastStUpdateDt = document.getElementById("lastStUpdateDt");
let stMessage = document.getElementById("stMessage");
let profileImg = document.getElementById("profileImg");

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
            switch(data.statusId){
                case 1: profileImg.style.border = '6px solid red;'; break;
                case 2: profileImg.style.border = '6px solid green;'; break;
                case 3: profileImg.style.border = '6px solid yellow;'; break;
                case 4: profileImg.style.border = '6px solid red;'; break;
                default: console.log("statusId error");
            }
        })
        .catch(error => {
            console.error('상태 읽어오기 문제 발생:', error);
        });
}

// 날짜 정보 포맷해서 보내기
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