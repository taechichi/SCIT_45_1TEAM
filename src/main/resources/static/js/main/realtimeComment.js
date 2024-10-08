document.addEventListener("DOMContentLoaded", function () {
    // === 전역 변수 정의 ===
    let userLocation = "Unknown";
    let currentDate = new Date();
    currentDate.setHours(currentDate.getHours() + 9); // 동경시간 보정
    let connectionTime = currentDate.toISOString();

    // ==== 로그인된 사용자 닉네임과 ID 가져오기 ====
    const loginMemberIdElement = document.getElementById('loginMemberId');
    const loginNicknameElement = document.getElementById('loginNickname');
    const loginMemberStMessage = document.getElementById('loginStMessage');

    let memberId = "";
    let nickname = "";
    let stMessage = "";

    if(loginMemberIdElement && loginNicknameElement) {
        memberId = loginMemberIdElement.value;
        nickname = loginNicknameElement.value;
        stMessage = loginMemberStMessage.value;
    }

    // ==== 도시 정보 추출 api 코드 ====
    // 외부 API를 통해 사용자의 위치 정보 얻기 (ipinfo.io 예시)
    // API 쓸라면 주석 해제
    fetch('https://ipinfo.io/json?token=8024395341b3f3')
        .then(response => response.json())
        .then(data => {
            if(memberId === "aaaaa")
            {
                userLocation = 'Tokyo_harajuku';
            } else if (memberId === "test1") {
                userLocation = '서울 강남구';
            } else if (memberId === "test2") {
                userLocation = '東京 渋谷';
            } else if (memberId === "test3") {
                userLocation = '東京 新宿';
            } else if (memberId === "test4") {
                userLocation = '東京 浅草';
            } else if (memberId === "test5") {
                userLocation = '東京 原宿';
            } else if (memberId === "test6") {
                userLocation = '東京 銀座';
            } else if(memberId === "test7") {
                userLocation = '東京 池袋 ';
            } else {
                userLocation = `${data.region}_${data.city}`;   // 도시 정보 추출
                // console.log("User location: ", userLocation);
            }
        })
        .catch(error => {
            //console.error("Error fetching location: ", error);
            userLocation = "Unknown";   // 위치 정보가 없을 때 기본값
        });

    // ==== 친구 정보 가져오기 ====
    // HTML에서 숨겨진 친구 데이터를 가져와서 friendList 배열을 생성
    const friendList = [];
    document.querySelectorAll('input[id^="friend"]').forEach(friendInput => {
        const friendId = friendInput.value;
        const nickname = document.getElementById(`nickname${friendId}`).value;
        const statusMessage = document.getElementById(`statusMessage${friendId}`).value;
        const statusName = document.getElementById(`statusName${friendId}`).value;

        // friendList 배열에 친구 정보 추가
        friendList.push({
            memberId: friendId,
            nickname: nickname,
            stMessage: statusMessage,
            statusName: statusName,
        });
    });

    // console.log(friendList);
    //console.log("Id, nickname", memberId, nickname);
    //console.log("연결 시간:", connectionTime);

    let eventSource = null;
    const commentList = document.getElementById("commentList");
    const sendButton = document.getElementById("sendCommentButton");
    const inputField = document.getElementById("commentInput");
    const commentListContainer = document.getElementById("commentListContainer");

    // Thymleaf에서 렌더링된 placeholder 값과 안정화 문구 가져오기 (data-* 속성 사용)
    const originalPlaceholder = inputField ? inputField.placeholder : "";  // 원래 placeholder 값
    const originalPlaceholderStabilization = inputField ? inputField.getAttribute('data-stabilization') : "채팅창 안정화 중..."; // 안정화 중 placeholder 값

    // SSE 연결 시작
    startEventSource();

    if(sendButton && inputField) {
        // 초기 상태에서는 전송 버튼 및 입력창 비활성화
        sendButton.disabled = true;
        inputField.disabled = true;
        inputField.placeholder = originalPlaceholderStabilization;  // 안정화 중 placeholder 값


        // 댓글 전송 버튼 이벤트 (버튼 클릭 또는 엔터키 입력 시)
        inputField.addEventListener("keydown", function (event) {
            if (event.key === "Enter") {
                event.preventDefault(); // 기본 엔터 동작 방지
                sendMessage();  // 엔터키로도 전송
            }
        });

        sendButton.addEventListener("click", function () {
            sendMessage();  // 전송 버튼으로도 전송
        });
    } else {
        // console.log("로그인되지 않은 사용자. 댓글 보지만, 전송은 못하지롱.")
    }

    // ======== 함수 정의 영역 =========

    // SSE 연결 시작하는 함수
    function startEventSource() {
        // console.log("startEventSource 호출됨."); // 이 로그 추가
        if (!eventSource) {
            eventSource = new EventSource(`/comments/stream?since=${connectionTime}`);
            // console.log("SSE 연결 설정됨");

            eventSource.onopen = function () {
                // console.log("SSE 연결이 성공적으로 열렸습니다.");
                sendButton.disabled = false;
                inputField.disabled = false;
                inputField.placeholder = originalPlaceholder;  // 안내 문구 삭제
                commentList.innerHTML = ''; // 기존 댓글 목록 초기화
            };

            eventSource.onmessage = function (event) {
                const comments = JSON.parse(event.data); // 서버에서 받은 데이터를 JSON 형식으로 파싱
                // console.log("받은 댓글 데이터:", comments);
                const isAtBottom = commentListContainer.scrollTop + commentListContainer.clientHeight >= commentListContainer.scrollHeight;

                // 전체 댓글 다시 그리기
                renderComments(comments);

                if (isAtBottom) { // 스크롤이 맨 아래에 있을 때만 자동으로 하단으로 이동
                    commentListContainer.scrollTop = commentListContainer.scrollHeight;
                }
            };

            eventSource.onerror = function (event) {
                // console.error("SSE 연결에서 오류가 발생했습니다.", event);
                eventSource.close();
                eventSource = null; // SSE 연결을 닫음
                sendButton.disabled = true;
                inputField.disabled = true;
                inputField.placeholder = originalPlaceholderStabilization;
                setTimeout(startEventSource, 2000);
            };
        }
    }

    // 댓글 전송 함수
    function sendMessage() {
        const contents = inputField.value;
        const currentTime = new Date().toISOString(); // 현재 시간을 전송할 때 포함

        if (contents.trim() !== "") {
            fetch("/comments", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    memberId: memberId,
                    nickname: nickname,
                    contents: contents,
                    location: userLocation,
                    createDt: currentTime,
                    stMessage: stMessage,
                })
            }).then(response => {
                if (response.ok) {
                    // console.log("댓글 전송 완료");
                    inputField.value = ""; // 입력 필드 초기화
                    commentListContainer.scrollTop = commentListContainer.scrollHeight;
                    updateCommentTimes();   // 채팅을 입력할 때 시간 업데이트
                } else {
                    // console.log(`댓글 전송 실패: 상태코드 ${response.status}`);
                }
            }).catch(error => {
                // console.log("댓글 전송 실패: ", error);
            });
        }
    }

    // 댓글 목록을 그리는 함수
    function renderComments(comments) {
        comments.forEach(comment => {
            const li = document.createElement("li");
            li.classList.add("chat-comment");
            li.setAttribute("data-created-at", comment.createDt); // 댓글 생성 시간 설정

            // 자신이 쓴 댓글인지 확인
            const itSelf = (comment.memberId === memberId);
            if(itSelf) {
                li.classList.add("self");
            }

            // 이미지 태그 추가 (자신의 메시지가 아닐 때만)
            if (!itSelf) {
                const img = document.createElement("img");
                img.classList.add("img-profile", "rounded-circle");
                img.src = `/member/download/${comment.memberId}`; // 서버에서 닉네임으로 이미지를 받아옴
                img.alt = "Profile Picture";

                // 프로필 이미지에 모달을 연결
                const imgWrapper = document.createElement("a");
                imgWrapper.href = "#";
                imgWrapper.setAttribute("data-toggle", "modal");
                imgWrapper.setAttribute("data-target", `#profileModal${comment.memberId}`);
                imgWrapper.appendChild(img);

                li.appendChild(imgWrapper);
            }

            // 댓글 내용 추가
            const commentContent = document.createElement("div");
            commentContent.classList.add("comment-content");

            // createDt를 변환하고 로그로 확인
            const createdTime = new Date(comment.createDt);
            // 시간 변환 (UTC에서 KST로 변환)
            const kstTime = convertToKST(new Date(createdTime));
            // 시간을 표시할 포맷으로 변환
            const hours = kstTime.getHours().toString().padStart(2, '0');
            const minutes = kstTime.getMinutes().toString().padStart(2, '0');
            const formattedTime = `${hours}:${minutes}`;

            // 댓글 생성
            commentContent.innerHTML = `
                    <small>(${comment.location})</small>
                    <small>[${comment.nickname}]</small>
                    ${comment.contents}<br>
                    <small class="comment-time">${formattedTime}</small>`;

            li.appendChild(commentContent);
            commentList.appendChild(li);
            
            // 모달 창 생성
            createProfileModal(comment);
        });
    }

    // 프로필 모달을 생성하는 함수
    function createProfileModal(comment) {
        const isCurrentUser = (comment.memberId === memberId);
        const friendData = friendList.find(friend => friend.memberId === comment.memberId);
        const isFriend = !!friendData; // friendData가 존재하면 true, 아니면 false

        // 로그인 여부를 확인
        const isLoggedIn = memberId !== "";

        // friendList에서 상태 이름을 가져옴 (친구인 경우에만)
        const statusName = friendData ? friendData.statusName : null;

        // 모달 HTML 생성
        const modalHtml = `
            <div class="modal fade" id="profileModal${comment.memberId}" tabindex="-1" role="dialog" aria-labelledby="profileModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered" role="document"> <!-- modal-dialog-centered로 중앙 정렬 -->
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="profileModalLabel">${comment.nickname}</h5>
                            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div class="modal-body text-center"> <!-- text-center로 가운데 정렬 -->
                            <div class="profile-picture-container">
                                <img class="img-profile rounded-circle img-64" src="/member/download/${comment.memberId}" alt="Profile Picture"/>
                            </div>
                            ${isCurrentUser ? `
                                <p><strong>상태 메시지:</strong> ${comment.stMessage ? comment.stMessage : 'No status message'}</p>
                            ` : `
                                ${isLoggedIn ? `
                                    <!-- 친구 추가 버튼을 중앙에 위치 -->
                                    ${!isFriend ? `
                                        <button class="btn btn-primary friend-add-btn mt-3" data-friend-id="${comment.memberId}">친구 추가</button>
                                        <p class="mt-3">
                                            <div class="text-left"> <!-- 새 div로 감싸서 좌측 정렬 -->
                                                <strong>상태 메시지:</strong>
                                                <pre>${comment.stMessage ? comment.stMessage : 'No status message'}</pre>
                                            </div>
                                        </p>
                                    ` : `
                                        <div class="modal-links">
                                            <a href="/my/message/write">쪽지 보내기</a>
                                        </div>
                                        <p><strong>마지막 상태 업데이트:</strong> ${statusName ? statusName : 'No status'}</p>
                                        <p><strong>상태 메시지:</strong> ${comment.stMessage ? comment.stMessage : 'No status message'}</p>
                                    `}
                                ` : `
                                    <p><strong>상태 메시지:</strong> ${comment.stMessage ? comment.stMessage : 'No status message'}</p>
                                `}
                            `}
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-dismiss="modal">닫기</button>
                        </div>
                    </div>
                </div>
            </div>
        `;

        // 모달 HTML을 body에 추가
        document.body.insertAdjacentHTML('beforeend', modalHtml);

        // 친구 추가 버튼에 이벤트 리스너 추가 (로그인된 상태에서만 추가)
        if (memberId !== "") {
            document.querySelectorAll('.friend-add-btn').forEach(button => {
                button.addEventListener('click', function() {
                    const friendId = this.getAttribute('data-friend-id');
                    addFriend(friendId);
                });
            });
        }
    }

    // 친구 판별 함수
    function checkIfFriend(friendId) {
        // friendId로 해당 친구의 hidden input을 찾아서 존재 여부 확인
        const friendElement = document.getElementById(`friend${friendId}`);
        return !!friendElement;  // element가 존재하면 true, 없으면 false 반환
    }

    // 친구 추가 버튼 동작
    function addFriend(friendId) {
        // console.log("friendId:", friendId);
        fetch(`/my/friend/${friendId}`, {
            method: 'POST',
            headers: {
                'Content-Type' : 'application/json'
            }
        }).then(response => {
            if(response.ok) {
                alert('친구 신청이 완료되었습니다.');
            } else {
                alert('친구 신청이 실패했습니다.');
            }
        }).catch(error => {
            console.error('Error:', error);
        })
    }

    // 시간 차이를 계산하는 함수
    function formatToTime(createDt) {
        const createdTime = new Date(createDt);
        // console.log("KST로 변환된 시간:", createdTime);
        const hours = createdTime.getHours().toString().padStart(2, "0");       // 2자리로 맞춤
        const minutes = createdTime.getMinutes().toString().padStart(2, "0");   // 2자리로 맞춤

        return `${hours}:${minutes}`;
    }


    // UTC 시간을 KST로 변환하는 함수
    function convertToKST(utcTime) {
        const date = new Date(utcTime);
        date.setHours(date.getHours() + 9); // 9시간 더해서 한국 시간(KST)으로 변환
        return date;
    }

    // 댓글 시간 업데이트 함수
    function updateCommentTimes() {
        const commentItems = document.querySelectorAll(".chat-comment");
        commentItems.forEach(commentItem => {
            const timeElement = commentItem.querySelector(".comment-time");
            const createdAt = commentItem.getAttribute("data-created-at");
            if (timeElement && createdAt) {
                timeElement.innerText = formatToTime(convertToKST(createdAt));
            }
        });
    }
});


// ==== 댓글창 열고 닫는 기능 ================================================================================
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
