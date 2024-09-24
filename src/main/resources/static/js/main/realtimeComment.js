// ==== 댓글 표시 기능 ======================================================
document.addEventListener("DOMContentLoaded", function () {
    // 사용자의 위치 정보를 저장할 변수
    let userLocation = "Unknown";
    let currentDate = new Date();
    currentDate.setHours(currentDate.getHours() + 9); // 동경시간 보정
    let connectionTime = currentDate.toISOString();

    // 로그인된 사용자 닉네임 가져오기
    const metaUser = document.querySelector("meta[name='authenticatedUser']");
    let userNickname = null;

    if(metaUser) {
        userNickname = metaUser.getAttribute('content');   // 로그인한 사용자의 닉네임
    } else {
        console.log("사용자가 로그인되지 않았습니다.");
    }

    console.log("연결 시간:", connectionTime);

    // ============================

    // 외부 API를 통해 사용자의 위치 정보 얻기 (ipinfo.io 예시)
    // API 쓸라면 주석 해제
    /*fetch('https://ipinfo.io/json?token=8024395341b3f3')
        .then(response => response.json())
        .then(data => {
            userLocation = `${data.region}_${data.city}`;   // 도시 정보 추출
            console.log("User location: ", userLocation);
        })
        .catch(error => {
            console.error("Error fetching location: ", error);
            userLocation = "Unknown";   // 위치 정보가 없을 때 기본값
        });*/

    // 1. SSE 연결 설정 (실시간 채팅 수신)
    // SSE로 서버와 연결하여 실시간 채팅 수신 설정
    const eventSource = new EventSource(`/comments/stream?since=${connectionTime}`);
    const commentList = document.getElementById("commentList");

    // 서버로부터 새로운 메시지가 올 때마다 실행
    eventSource.onmessage = function (event) {
        const comments = JSON.parse(event.data); // 서버에서 받은 데이터를 JSON 형식으로 파싱
        //commentList.innerHTML = ''; // 기존 메시지 목록 초기화

        comments.forEach(comment => {   // 새로운 채팅 메시지들을 반복 처리
            const li = document.createElement("li");    // 새로운 li 요소 생성
            li.innerHTML = `(${comment.location}) [${comment.nickname}]<br>${comment.contents}`;       // 닉네임과 내용을 설정
            commentList.appendChild(li);    // 댓글 목록에 li 요소를 추가하여 화면에 표시
        });
    };

    // SSE 연결 오류 처리
    eventSource.onerror = function(event) {
        console.error("SSE 연결에서 오류가 발생했습니다.", event);
        eventSource.close();  // 연결이 끊어졌을 때 연결 종료
    };


    const sendButton = document.getElementById("sendCommentButton");
    if(sendButton && userNickname) {
        // 댓글 전송하는 함수
        sendButton.addEventListener("click", function () {
            const contents = document.getElementById("commentInput").value;

            // 서버에 새로운 댓글을 전송하는 POST 요청
            fetch("/comments", {
                method: "POST", // HTTP 메소드는 POST로 설정
                headers: {
                    "Content-Type": "application/json"  // 요청 데이터 타입은 JSON으로 설정
                },
                body: JSON.stringify({
                    nickname: userNickname,
                    contents: contents,
                    location: userLocation
                })
            }).then(response => {
                if(response.ok) {
                    console.log(response);
                    console.log("댓글 전송 완료");
                } else {
                    console.log(`댓글 전송 실패: 상태코드 ${response.status}`);
                }
            }).catch(error => {
                console.log("댓글 전송 실패: ", error);
            });
        })
    }
});

// ==== 댓글창 열고 닫는 기능 ================================================
document.addEventListener("DOMContentLoaded", function() {
    const commentSection = document.getElementById("realtime_comment");
    const toggleButton = document.getElementById("toggleCommentButton");
    const sidebar = document.getElementById("accordionSidebar"); // 메뉴바

    // 애니메이션 추가 함수
    const addAnimation = () => {
        commentSection.style.transition = "all 0.3s ease";
        toggleButton.style.transition = "all 0.3s ease";
    };

    // 애니메이션 삭제 함수
    const removeAnimation = () => {
        commentSection.style.transition = "none";
        toggleButton.style.transition = "none";
    };

    // 처음 페이지 로드 시 게시판이 열려 있도록 설정
    const updateLayout = () => {
        const sidebarWidth = sidebar.offsetWidth;
        const windowHeight = window.innerHeight;
        const footerHeight = 25; // 이건 예시값 나중에 새로 적용해야함.

        // 게시판 높이 설정 (너비는 고정)
        commentSection.style.height = `${windowHeight - 45 - footerHeight}px`;  // 창 높이에 따라 조정

        // 게시판 상태에 따라 위치 설정
        if (commentSection.classList.contains("open")) {
            commentSection.style.left = `${sidebarWidth}px`;  // 열려 있을 때 위치
            toggleButton.style.left = `${sidebarWidth + 300}px`;  // 버튼 위치
        } else {
            commentSection.style.left = `-${300}px`;  // 닫혀 있을 때 위치
            toggleButton.style.left = `${sidebarWidth}px`;  // 버튼이 메뉴 옆에 붙음
        }
    };

    // 페이지가 처음 로드될 때 게시판을 열려 있는 상태로 설정
    const initializeState = () => {
        const sidebarWidth = sidebar.offsetWidth;
        commentSection.classList.add("open");  // 처음부터 게시판을 열기
        commentSection.style.left = `${sidebarWidth}px`;  // 메뉴 너비만큼 이동
        toggleButton.style.left = `${sidebarWidth + 300}px`;  // 버튼 위치 설정
        toggleButton.textContent = "◀";  // 화살표 표시도 열려 있는 상태로
    };

    initializeState();
    updateLayout();

    // 토글 버튼 클릭 시 게시판 열고 닫기
    toggleButton.addEventListener("click", function() {
        const updatedSidebarWidth = sidebar.offsetWidth;

        if (commentSection.classList.contains("close")) {
            // 게시판을 열기
            commentSection.classList.remove("close");
            commentSection.classList.add("open");
            commentSection.style.left = `${updatedSidebarWidth}px`; // 다시 메뉴 너비만큼 이동
            toggleButton.style.left = `${updatedSidebarWidth + 300}px`; // 버튼 위치 설정
            toggleButton.textContent = "◀"; // 버튼 화살표 변경
        } else {
            // 게시판을 닫기
            commentSection.classList.remove("open");
            commentSection.classList.add("close");
            commentSection.style.left = `-${300}px`; // 게시판 숨기기
            toggleButton.style.left = `${updatedSidebarWidth}px`;
            toggleButton.textContent = "▶";         // 게시판이 닫히면 화살표 변경
        }
    });

    // 창 크기 변경 시 레이아웃 다시 설정
    window.addEventListener("resize", function() {
        const updatedSidebarWidth = sidebar.offsetWidth;

        // 창 크기 조정 중에는 애니메이션 삭제
        removeAnimation();

        // 게시판 상태에 따라 위치를 다시 설정
        if(commentSection.classList.contains("open")) {
            commentSection.style.left = `${updatedSidebarWidth}px`;
            toggleButton.style.left = `${updatedSidebarWidth + 300}px`;
        } else {
            // 닫힌 상태일때 게시판이 튀어나오지 않도록 위치 유지
            commentSection.style.left = `-${300}px`;
            toggleButton.style.left = `${updatedSidebarWidth}px`;
        }

        updateLayout();  // 창 크기 조정에 따라 게시판 높이 조정 및 위치 재설정

        // 애니메이션 복구 (창 크기 조정 후 다시 적용)
        setTimeout(() => {
            addAnimation();
        }, 100);
    });
});
