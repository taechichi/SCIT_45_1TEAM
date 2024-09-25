// css를 위해 disabled된 input type="file" 요소에 대해, 사용자가 파일을 선택했을 시 선택된 파일명을 표시
function displayFileName() {
    let fileInput = document.getElementById('audioFile');
    let fileNameDisplay = document.getElementById('fileName');

    if (fileInput.files.length > 0) {
        // 선택된 파일의 이름을 가져옴
        let fileName = fileInput.files[0].name;
        fileNameDisplay.textContent = fileName; // 파일명을 표시
        fileNameDisplay.style.visibility = "visible";
    } else {
        fileNameDisplay.textContent = ''; // 파일이 선택되지 않으면 빈 문자열
    }
}

// 텍스트화와 번역이 진행되는 동안 로딩
function showLoading() {
    $('#loadingOverlay').show(); // 로딩 오버레이 보이기
}

function hideLoading() {
    $('#loadingOverlay').hide(); // 로딩 오버레이 숨기기
}

// 사용자가 선택한 언어 담을 변수
let whatLan;

// 1. 사용자가 업로드한 flac 오디오 파일을 텍스트화
function upload() {
    // 사용자가 업로드한 파일 요소
    let fileInput = document.getElementById('audioFile');
    // 파일을 담을 배열 선언
    let file = fileInput.files[0];
    // 사용자가 선택한 언어 담을 변수
    whatLan = document.getElementById('selectLan').value;

    // 파일이 비어있는데 버튼을 눌렀을 시,
    if (!file) {
        alert(noFileSelected);
        return;
    }

    showLoading(); // 로딩 시작

    // 오디오 파일 데이터를 담을 FormData 객체 생성
    let formData = new FormData();
    formData.append('audio', file);

    // 파일을 서버로 보내 변환된 텍스트 받아오기
    fetch('/fileConvertText', {
        method: 'POST',
        body: formData
    })
        // 성공 시,
        .then(response => response.json())
        .then(data => { // 리턴되는 값]
            // 반환된 텍스트를 번역하기 위해 함수 호출
            let whatLan = document.getElementById('selectLan').value;
            requestTranslation(data.text, whatLan);
            // 서버에서 반환된 객체 'data'의 text 값을 id가 'texted'인 요소에 입력
            document.getElementById('texted').innerHTML += `${data.text}<br>`;

        })
        // 실패시
        .catch(error => {
            hideLoading(); // 오류 발생 시 로딩 종료
            console.error('Error converting audio to text:', error);
            document.getElementById('texted').innerHTML = `${textedError}`;
        });
}


// 3. 실시간 번역 요청 함수(text화된 문자열, 언어)
function requestTranslation(text, whatLan) {
    // 어떤 언어들로 변환할지 배열에 담음
    let targetLanguages = [];
    switch (whatLan) {
        case 'en-US':
            targetLanguages = ['ko-KR', 'ja-JP'];
            break;
        case 'ko-KR':
            targetLanguages = ['en-US', 'ja-JP'];
            break;
        case 'ja-JP':
            targetLanguages = ['en-US', 'ko-KR'];
            break;
        default:
            console.error('알 수 없는 타겟 소스 언어:', whatLan);
            return;
    }

    // 번역되어야 할 타겟 언어 배열의 요소들을 반복
    targetLanguages.forEach(targetLanguage => {
        fetch('/translateText', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                text: text,
                sourceLanguage: whatLan,
                targetLanguage: targetLanguage
            })
        })
            .then(response => response.json())
            .then(data => {
                hideLoading(); // 번역 완료 후 로딩 종료
                // 현재의 텍스트에 새 번역 결과를 추가하고 개행 문자를 추가
                document.getElementById('translated').innerHTML += `${data.translatedText}<br>`;
            })
            .catch(error => {
                hideLoading(); // 오류 발생 시 로딩 종료
                console.error('번역 실패:', error);
                document.getElementById('translated').append(`${transError}`);
            });
    });
}

