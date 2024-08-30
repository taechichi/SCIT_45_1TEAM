let progressInterval;

function updateProgressBar(percentage) {
    let progressBar = document.getElementById('progress-bar');
    progressBar.style.width = percentage + '%';
    progressBar.textContent = percentage + '%';
}

function startProgress() {
    let progress = 0;
    progressInterval = setInterval(() => {
        if (progress < 100) {
            progress += 10;  // 10%씩 증가
            updateProgressBar(progress);
        } else {
            progress = 0;
            updateProgressBar(progress);
        }
    }, 1000); // 1초마다 업데이트
}

function uploadFile() {
    let formData = new FormData();
    let fileInput = document.querySelector('input[type="file"]');
    let file = fileInput.files[0];
    formData.append('file', file);

    startProgress();

    fetch('http://localhost:8080/api/shelters/import-file', {
        method: 'POST',
        body: formData
    })
        .then(response => response.text())
        .then(data => {
            clearInterval(progressInterval);
            updateProgressBar(100);
            alert(data);  // 성공 메시지 알림
            console.log('Success:', data);
        })
        .catch((error) => {
            clearInterval(progressInterval)
            updateProgressBar(0);
            alert('Error occurred: ' + error);  // 에러 메시지 알림
            console.error('Error:', error);
        });
}