package com.scit.proj.scitsainanguide.service;

import com.google.cloud.translate.v3.DetectLanguageRequest;
import com.google.cloud.translate.v3.DetectLanguageResponse;
import com.google.cloud.translate.v3.DetectedLanguage;
import com.google.cloud.translate.v3.TranslationServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import java.io.IOException;
import java.util.List;


@Slf4j
@Service
public class TranslationService {

    /**
     * Streaming 음성을 듣고, text로 반환하는 메서드
     * @param file
     * @param languageCode
     * @return
     * @throws IOException
     */
    public String streamingMicRecognize(MultipartFile file, String languageCode) throws IOException {

        try (SpeechClient speechClient = SpeechClient.create()) {
            ByteString audioBytes = ByteString.copyFrom(file.getBytes());

            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.WEBM_OPUS)  // 오디오 파일의 인코딩 설정
                    .setSampleRateHertz(48000)  // 오디오 샘플 레이트 설정
                    .setLanguageCode(languageCode)  // 언어 코드 설정
                    .build();

            RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();

            RecognizeRequest request = RecognizeRequest.newBuilder()
                    .setConfig(config)
                    .setAudio(audio)
                    .build();

            List<SpeechRecognitionResult> response = speechClient.recognize(request).getResultsList();

            StringBuilder transcription = new StringBuilder();
            for (SpeechRecognitionResult result : response) {
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                transcription.append(alternative.getTranscript());
            }

            return transcription.toString();
        }
    }

}
