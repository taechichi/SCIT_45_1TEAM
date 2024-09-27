document.addEventListener("DOMContentLoaded", function () {
    let userLocation = "Unknown";
    let currentDate = new Date();
    currentDate.setHours(currentDate.getHours() + 9); // 동경시간 보정
    let connectionTime = currentDate.toISOString();

    // ==== 로그인된 사용자 닉네임 가져오기 ====
    const metaUser = document.querySelector("meta[name='authenticatedUser']");
    let userNickname = null;

    // 외부 API를 통해 사용자의 위치 정보 얻기 (ipinfo.io 예시)
    // 서울의 경우 Seoul_Seoul이라고 나오는데, 일본의 경우에는 Tokyo_akihabara 처럼 나옴 (<-국제화 필요)
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


    if (metaUser) {
        userNickname = metaUser.getAttribute('content'); // 로그인한 사용자의 닉네임
    } else {
        console.log("사용자가 로그인되지 않았습니다.");
    }
    console.log("연결 시간:", connectionTime);

    let eventSource = null;
    const commentList = document.getElementById("commentList");
    const sendButton = document.getElementById("sendCommentButton");
    const inputField = document.getElementById("commentInput");
    const commentListContainer = document.getElementById("commentListContainer");

    // 초기 상태에서는 전송 버튼 및 입력창 비활성화
    sendButton.disabled = true;
    inputField.disabled = true;
    inputField.placeholder = "채팅창 안정화 중...";  // 안내 문구 추가

    function startEventSource() {
        if (!eventSource) {
            eventSource = new EventSource(`/comments/stream?since=${connectionTime}`);
            console.log("SSE 연결 설정됨");

            eventSource.onopen = function () {
                console.log("SSE 연결이 성공적으로 열렸습니다.");
                // 연결이 성공하면 전송 버튼과 입력창 활성화
                sendButton.disabled = false;
                inputField.disabled = false;
                inputField.placeholder = "댓글 입력";  // 안내 문구 삭제
                commentList.innerHTML = ''; // 기존 댓글 목록 초기화
            };

            eventSource.onmessage = function (event) {
                const comments = JSON.parse(event.data); // 서버에서 받은 데이터를 JSON 형식으로 파싱
                const isAtBottom = commentListContainer.scrollTop + commentListContainer.clientHeight >= commentListContainer.scrollHeight;

                comments.forEach(comment => { // 새로운 채팅 메시지들을 반복 처리

                    // HTML 태그 생성
                    const li = document.createElement("li");
                    // 댓글에 클래스 추가
                    li.classList.add("chat-comment");

                    // 이미지 태그 추가
                    const img = document.createElement("img");
                    img.classList.add("img-profile", "rounded-circle");
                    img.src = `/member/download`; // 서버에서 닉네임으로 이미지를 받아옴
                    img.alt = "Profile Picture";

                    // 댓글 내용 추가
                    const commentContent = document.createElement("div");
                    commentContent.classList.add("comment-content");
                    commentContent.innerHTML = `(${comment.location}) [${comment.nickname}]<br>${comment.contents}`;

                    // li 요소에 이미지와 댓글 내용을 추가
                    li.appendChild(img);
                    li.appendChild(commentContent);

                    // 댓글 목록에 추가
                    commentList.appendChild(li);

                    if(isAtBottom) { // 스크롤이 맨 아래에 있을 때만 자동으로 하단으로 이동
                        // 스크롤을 맨 아래로 이동
                        commentListContainer.scrollTop = commentListContainer.scrollHeight;
                    }
                });
            };

            eventSource.onerror = function (event) {
                console.error("SSE 연결에서 오류가 발생했습니다.", event);
                eventSource.close();
                eventSource = null; // SSE 연결을 닫음
                // 연결이 끊어지면 전송 버튼과 입력창 비활성화
                sendButton.disabled = true;
                inputField.disabled = true;
                inputField.placeholder = "채팅창 안정화 중...";  // 다시 안내 문구 표시
                // 2초 후에 재연결 시도
                setTimeout(startEventSource, 2000);
            };
        }
    }

    startEventSource(); // SSE 연결 시작

    // 댓글 전송 버튼 이벤트 (버튼 클릭 또는 엔터키 입력 시)
    function sendMessage() {
        const contents = inputField.value;
        if (contents.trim() !== "") { // 빈 내용이 아닐 경우에만 전송
            // 서버에 새로운 댓글을 전송하는 POST 요청
            fetch("/comments", {
                method: "POST", // HTTP 메소드는 POST로 설정
                headers: {
                    "Content-Type": "application/json" // 요청 데이터 타입은 JSON으로 설정
                },
                body: JSON.stringify({
                    nickname: userNickname,
                    contents: contents,
                    location: userLocation,
                })
            }).then(response => {
                if (response.ok) {
                    console.log("댓글 전송 완료");
                    inputField.value = ""; // 입력 필드 초기화

                    // 채팅을 입력할 때는 항상 스크롤을 맨 아래로 이동
                    commentListContainer.scrollTop = commentListContainer.scrollHeight;
                } else {
                    console.log(`댓글 전송 실패: 상태코드 ${response.status}`);
                }
            }).catch(error => {
                console.log("댓글 전송 실패: ", error);
            });
        }
    }

    // 전송 버튼 클릭 및 엔터키 입력 이벤트 통합 처리
    inputField.addEventListener("keydown", function (event) {
        if(event.key === "Enter") {
            event.preventDefault(); // 기본 엔터 동작 방지
            sendMessage();  // 엔터키로도 전송
        }
    });

    if(sendButton && userNickname) {
        sendButton.addEventListener("click", function () {
            sendMessage();  // 전송 버튼으로도 전송
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
