package com.scit.proj.scitsainanguide.controller;

import com.google.protobuf.ByteString;
import com.scit.proj.scitsainanguide.service.TranslationService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.google.cloud.speech.v1.*;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Controller
@Data
public class TranslationController {

    /**
     * 홈화면에서 좌측 탭 메뉴 > FileToTranslate 버튼을 클릭하면 이동
     * @return
     */
    @GetMapping("fileToTranslate")
    public String fileTrans(){
        return "translate/translateUploadedFile";
    }

    /**
     * 홈화면에서 좌측 탭 메뉴 > StreamingToTranslate 버튼을 클릭하면 이동
     * @return
     */
    @GetMapping("streamingToTranslate")
    public String streamingTrans(){
        return "translate/translateStreaming";
    }

    // 사용할 서비스 메서드
    private final TranslationService tservice;

    /**
     * 음성 파일 업로드 시, 자동으로 텍스트로 변환해주는 메서드
     * @param audioFile
     * @return
     * @throws IOException
     */
    @ResponseBody
    @PostMapping("fileConvertText")
    public String handleSpeechToText(@RequestParam("audio") MultipartFile audioFile) throws IOException {
        // Load audio file into memory
        byte[] audioBytes = audioFile.getBytes();
        String contentType = audioFile.getContentType();

        // 기본 공통 정보 세팅
        RecognitionConfig.Builder configBuilder = RecognitionConfig.newBuilder()
                .setLanguageCode("ja-JP") // 일본어로 설정하더라도, 영어 파일의 경우 자동으로 인식해 반환해줌
                .setAudioChannelCount(2); // Default to mono if not specified

        // 파일 형식에 맞게 세팅
        switch (contentType) {
            case "audio/flac":
                configBuilder.setEncoding(RecognitionConfig.AudioEncoding.FLAC);
                break;
            case "audio/l16":
                configBuilder.setEncoding(RecognitionConfig.AudioEncoding.LINEAR16);
                configBuilder.setSampleRateHertz(16000); // Default sample rate for LINEAR16
                break;
            case "audio/ogg":
                configBuilder.setEncoding(RecognitionConfig.AudioEncoding.OGG_OPUS);
                break;
            case "audio/amr":
                configBuilder.setEncoding(RecognitionConfig.AudioEncoding.AMR);
                break;
            // case "audio/mpeg": configBuilder.setEncoding(RecognitionConfig.AudioEncoding.MP3); break; <- 왜인지 MP3 사용불가
            default:
                return "{\"text\":\"Unsupported audio format. Please upload a supported audio file.\"}";
        }

            RecognitionConfig config = configBuilder.build();
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(ByteString.copyFrom(audioBytes))
                    .build();

            try (SpeechClient speechClient = SpeechClient.create()) {
                RecognizeResponse response = speechClient.recognize(config, audio);
                List<SpeechRecognitionResult> results = response.getResultsList();

                StringBuilder transcript = new StringBuilder();
                for (SpeechRecognitionResult result : results) {
                    transcript.append(result.getAlternativesList().get(0).getTranscript());
            }


            return "{\"text\":\"" + transcript + "\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"text\":\"Error occurred during speech recognition\"}";
        }
    }


    /**
     * 사용자가 실시간 스트리밍으로 음성을 입력하면, 선택된 언어와 음성본을 서비스로 가져가 텍스트로 반환
     * @param file
     * @param languageCode
     * @return 변환된 문자열
     */
    @ResponseBody
    @PostMapping("voiceConvertText")
    public ResponseEntity<Map<String, String>> convertSpeech(@RequestParam("file") MultipartFile file,
                                                             @RequestParam("languageCode") String languageCode) {
        Map<String, String> response = new HashMap<>();
        try {
            String transcription = tservice.streamingMicRecognize(file, languageCode);
            response.put("transcription", transcription);
            response.put("languageCode", languageCode);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("error", "Failed to convert speech to text: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 위에서 text화된 글에 대해, 번역해 결과를 반환
     * @param request
     * @return
     * @throws IOException
     */
    @ResponseBody
    @PostMapping("/translateText")
    public Map<String, String> translateText(@RequestBody Map<String, String> request) throws IOException {
        String sourceLanguage = request.get("sourceLanguage");
        String targetLanguage = request.get("targetLanguage");
        String text = request.get("text");

        // 번역 서비스 객체를 생성
        Translate translate = TranslateOptions.getDefaultInstance().getService();

        // 번역을 요청
        String translatedText = translate(text, sourceLanguage, targetLanguage, translate);

        // 응답을 준비
        Map<String, String> response = new HashMap<>();
        response.put("translatedText", translatedText);
        return response;
    }

    private String translate(String text, String sourceLanguage, String targetLanguage, Translate translate) {
        // 번역 요청
        Translation translation = translate.translate(
                text,
                Translate.TranslateOption.sourceLanguage(sourceLanguage),
                Translate.TranslateOption.targetLanguage(targetLanguage)
        );

        return translation.getTranslatedText();
    }

}
