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
        commentSection.style.height = `${windowHeight - 160 - footerHeight}px`;  // 창 높이에 따라 조정

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
