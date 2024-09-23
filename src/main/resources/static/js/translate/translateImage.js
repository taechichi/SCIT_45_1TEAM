// css를 위해 disabled된 input type="file" 요소에 대해, 사용자가 파일을 선택했을 시 선택된 파일명을 표시
function displayFileName() {
    let fileInput = document.getElementById('file');
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

// 1. 이미지를 업로드할 때 실행되는 함수, 사용자가 선택한 원문 언어값 추출된 텍스트를 가지고 번역함수 호출
function handleFormSubmit(event) {
    event.preventDefault(); // 폼 제출 시 페이지 새로 고침 방지

    let fileInput = document.getElementById('file');
    let file = fileInput.files[0];

    // 원문 언어가 뭔지
    let sourceLanguage = document.getElementById('sourceLanguage').value;
    console.log('sourceLanguage: ', sourceLanguage);

    // 파일이 선택되지 않은 경우
    if (!file) {
        console.error('파일이 선택되지 않았습니다.');
        return;
    }

    // FormData 객체 생성
    let formData = new FormData(document.getElementById('uploadForm'));

    // 파일을 FormData에 추가
    formData.append('file', file);

    // fetch 요청
    fetch('/uploadImage', { // 서버 엔드포인트
        method: 'POST',
        body: formData
    })
        .then(response => response.json())
        .then(data => {
            // 서버 응답 처리
            document.getElementById('extractedText').innerText = data.extractedText || 'No text extracted';
            // 추출된 텍스트와, 사용자가 선택한 원문 언어 정보로 번역 함수 호출
            requestTranslation(data.extractedText, sourceLanguage);
        })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById('extractedText').innerText = `${textedError}`;
        });
}


// 2. 실시간 번역 요청 함수(text화된 문자열, 언어)
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
        }) // 응답 객체
            .then(response => {
                if (!response.ok) {
                    return response.json().then(errorData => {
                        throw new Error(errorData.error || 'Unknown error occurred');
                    });
                }
                return response.json();
            })
            .then(data => {
                // 현재의 텍스트에 새 번역 결과를 추가하고 개행 문자를 추가
                document.getElementById('translatedText').innerHTML += `${data.translatedText}<br>`;
            })
            .catch(error => {
                console.error('번역 실패:', error);
                document.getElementById('translatedText').innerHTML = `${transError}`;
            });
    });
}

