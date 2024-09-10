package com.scit.proj.scitsainanguide.controller;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import com.scit.proj.scitsainanguide.service.TranslationService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Controller
@Data
public class TranslationController {

    // try-catch 없애기
    
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

    /**
     * 홈화면에서 좌측 탭 메뉴 > TranaslateImage 버튼을 클릭하면 이동
     * @return
     */
    @GetMapping("imageTranslate")
    public String imageTranslate(){
        return "translate/translateImage";
    }

    // 사용할 서비스
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

        // contentType이 null인 경우 기본 형식으로 처리하거나 예외를 던질 수 있습니다.
        if (contentType == null) {
            throw new IOException("Audio content type is missing or unsupported.");
        }

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
                throw new IOException("Unsupported audio format. Please upload a supported audio file.");
        }

            RecognitionConfig config = configBuilder.build();
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(ByteString.copyFrom(audioBytes))
                    .build();

        // SpeechClient 생성 및 인식 요청
        SpeechClient speechClient = SpeechClient.create();
        RecognizeResponse response = speechClient.recognize(config, audio);
        List<SpeechRecognitionResult> results = response.getResultsList();

        StringBuilder transcript = new StringBuilder();
        for (SpeechRecognitionResult result : results) {
            transcript.append(result.getAlternativesList().get(0).getTranscript());
        }
        return "{\"text\":\"" + transcript + "\"}";
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
                                                             @RequestParam("languageCode") String languageCode) throws IOException {
        Map<String, String> response = new HashMap<>();
        String transcription = tservice.streamingMicRecognize(file, languageCode);
        response.put("transcription", transcription);
        response.put("languageCode", languageCode);
        return ResponseEntity.ok(response);
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

    /**
     * 텍스트에 대해, 어떤 언어로 번역할 것인지 함께 파라미터로 전송함으로써 번역 후 반환하는 메서드
     * @param text
     * @param sourceLanguage
     * @param targetLanguage
     * @param translate
     * @return 번역완료된 문자열
     */
    private String translate(String text, String sourceLanguage, String targetLanguage, Translate translate) {
        // 번역 요청
        Translation translation = translate.translate(
                text,
                Translate.TranslateOption.sourceLanguage(sourceLanguage),
                Translate.TranslateOption.targetLanguage(targetLanguage)
        );

        return translation.getTranslatedText();
    }

    /**
     * 사용자가 업로드한 이미지 파일에 대해, 텍스트를 추출해 반환하는 메서드
     * @param file
     * @return 추출한 텍스트
     */
    @ResponseBody
    @PostMapping("/uploadImage")
    public ResponseEntity<Map<String, String>> uploadAndTranslate(@RequestParam("file") MultipartFile file) throws IOException {

        // 이미지에서 텍스트 추출
        String extractedText = extractTextFromImage(file);

        // 결과를 모델에 추가하여 클라이언트에 반환
        Map<String, String> response = new HashMap<>();
        response.put("extractedText", extractedText);

        return ResponseEntity.ok(response);
    }

    /**
     * 사용자가 업로드한 이미지 파일에서 문자를 추출해 text화하여 반환함
     * @param file
     * @return text화된 문자열
     * @throws IOException
     */
    private String extractTextFromImage(MultipartFile file) throws IOException {
        // 업로드된 파일을 ByteString 형식으로 변환
        ByteString imgBytes = ByteString.readFrom(file.getInputStream());

        // Vision API 클라이언트 생성
        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {
            // 이미지 객체 생성
            Image img = Image.newBuilder().setContent(imgBytes).build();

            // 텍스트 감지를 위한 요청 생성
            Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build();

            // Vision API에 요청을 보내고 응답을 받음
            AnnotateImageResponse response = vision.batchAnnotateImages(List.of(request)).getResponses(0);

            // 응답에서 텍스트 정보를 추출하여 단일 문자열로 반환
            String text = response.getTextAnnotationsList().stream()
                    .map(EntityAnnotation::getDescription)
                    .collect(Collectors.joining(" "));

            // 불필요한 개행 문자 제거 및 공백으로 대체
            return text.replaceAll("\\s+", " ").trim();
        }
    }

}
