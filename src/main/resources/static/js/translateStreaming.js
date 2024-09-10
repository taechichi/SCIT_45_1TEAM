// 1. 실시간 스트리밍으로 녹음된 audio/webm 파일을 텍스트화
document.addEventListener('DOMContentLoaded', () => {
    let mediaRecorder;  // 녹음해 담음
    let audioChunks = []; // 쪼갠 조각을 담는 배열


    // 녹음 시작 버튼 클릭 시
    document.getElementById('start-recording').addEventListener('click', () => {
        console.log("1. 언어 선택 후 녹음 시작 버튼 클릭");
        // 녹음 시작 버튼 클릭 시 선택된 언어 업데이트
        streamingLanguage = document.getElementById('streamingLan').value;
        // 사용자의 마이크에 접근하기 위한 권한 요청을 참으로
        navigator.mediaDevices.getUserMedia({audio: true})
            .then(stream => {
                console.log("2. 마이크 접근 허용");
                // MediaRecorder 객체를 생성하여 오디오 스트림 녹음
                mediaRecorder = new MediaRecorder(stream, {mimeType: 'audio/webm'}); // 오디오 포맷 설정

                // 녹음 시작 시 로그 출력
                mediaRecorder.onstart = () => {
                    console.log("4. 녹음이 시작되었습니다.");
                };

                // 녹음된 오디오 데이터 조각을 수집
                mediaRecorder.ondataavailable = event => {
                    audioChunks.push(event.data); // 데이터 조각 배열에 저장
                    console.log("5. 오디오 데이터 조각이 수집되었습니다.");
                };

                // 녹음이 중지되면 호출되는 함수 미리 정의
                mediaRecorder.onstop = () => {
                    console.log("7. 녹음 중지");
                    // 오디오 조각들을 하나의 Blob에 결합.
                    let audioBlob = new Blob(audioChunks, {type: 'audio/webm'}); // Blob 포맷 설정
                    // 옮겨 담고 나면, 다시 새로 녹음 가능하도록 배열 초기화
                    audioChunks = [];

                    // Blob을 URL로 변환하여 오디오 플레이어에 설정
                    document.getElementById('audio-playback').src = URL.createObjectURL(audioBlob);

                    // 녹음된 오디오를 서버로 업로드합니다.
                    uploadAudio(audioBlob);
                    console.log("8. 서버로 오디오 전송 중...");
                };

                // 녹음 시작!
                mediaRecorder.start();
                console.log("3. 녹음 시작");

                // 버튼 상태 변경: 녹음 시작 버튼 비활성화, 중지 버튼 활성화
                document.getElementById('start-recording').disabled = true;
                document.getElementById('stop-recording').disabled = false;
            })
            .catch(error => console.error('마이크 접근에 실패했습니다:', error)); // 마이크 접근 오류 처리
    });

    // 녹음 중지 버튼 클릭 시
    document.getElementById('stop-recording').addEventListener('click', () => {
        // 녹음 중지
        mediaRecorder.stop();
        console.log("6. 녹음 중지");

        // 버튼 상태 변경: 녹음 시작 버튼 활성화, 중지 버튼 비활성화
        document.getElementById('start-recording').disabled = false;
        document.getElementById('stop-recording').disabled = true;
    });

    // 서버에 녹음된 오디오 Blob 형식의 파일을 업로드하는 함수
    function uploadAudio(audioBlob) {
        // FormData 객체를 사용하여 파일을 서버에 전송합니다.
        let formData = new FormData();
        formData.append('file', audioBlob, 'recording.wav');
        formData.append('languageCode', streamingLanguage);

        // 서버에 POST 요청을 보내 실시간 스트리밍 오디오 파일을 업로드
        fetch('voiceConvertText', {
            method: 'POST',
            body: formData
        })
            // 응답 객체
            .then(response => {
                if (!response.ok) {
                    return response.json().then(errorData => {
                        throw new Error(errorData.error || 'Unknown error occurred');
                    });
                }
                return response.json();
            })
            // 서버에서 반환된 값
            .then(data => {
                console.log('9. 서버 응답:', data);
                document.getElementById('voiceTexted').innerHTML = `${data.transcription}`;

                // 번역 요청 함수 호출(texted 글, 어떤 언어인지)
                let whatLan = data.languageCode;
                requestTranslation(data.transcription, whatLan);
            })
            .catch(error => {
                console.error('텍스트로 변환 실패:', error);
                document.getElementById('voiceTexted').innerHTML = `<span th:text="#{translate.textedError}"></span>`;
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
            fetch('translateText', {
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
                    document.getElementById('voiceTranslated').innerHTML += `${data.translatedText}<br>`;
                })
                .catch(error => {
                    console.error('번역 실패:', error);
                    document.getElementById('voiceTranslated').innerHTML = `<span th:text="#{translate.transError}"></span>`;
                });
        });
    }
})