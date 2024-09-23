window.addEventListener('load', function () {
    setTimeout(function () {
        document.getElementById('loading').style.display = 'none';
        document.getElementById('content').style.display = 'block';
    }, 1500); // 1.5초 동안 로딩 화면 유지
});