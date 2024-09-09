package com.scit.proj.scitsainanguide.controller.advice;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

@Slf4j
@ControllerAdvice
public class CommonControllerAdvice {

    // ** RUNTIME ERROR 는 throws 할 필요없습니다.
    // ** COMPILE ERROR 에서만 throws 해주세요.

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EntityNotFoundException.class)
    public void entityNotFoundExHandle(EntityNotFoundException e) {
        // TODO 추후 에러처리 방안이 생긴다면 아래의 코드를 수정해야함. 일단은 메세지만 출력하는 것으로 작성
        log.error("[EntityNotFoundException Handler] ", e);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalStateException.class)
    public void illegalStateExHandle(IllegalStateException e) {
        // TODO 추후 에러처리 방안이 생긴다면 아래의 코드를 수정해야함. 일단은 메세지만 출력하는 것으로 작성
        log.error("[IllegalStateException Handler] ", e);
    }


    // ===== Shelter_Entity db 삽입 관련 예외처리입니다. ==============================================
    // JsonParseException 처리
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(JsonParseException.class)
    public void jsonParseExceptionHandle(JsonParseException e) {
        log.error("[JsonParseException Handler] JSON 파싱 오류 발생 ", e);
    }

    // JsonMappingException 처리
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(JsonMappingException.class)
    public void jsonMappingExceptionHandle(JsonMappingException e) {
        log.error("[JsonMappingException Handler] JSON 매핑 오류 발생", e);
    }

    // IOException 처리 (파일 읽기 오류 포함)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IOException.class)
    public void ioExceptionHandle(IOException e) {
        log.error("[IOException Handler] 파일 읽기 오류 발생", e);
    }
    // ============================================================================================

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public void exHandle(Exception e) {
        // TODO 추후 에러처리 방안이 생긴다면 아래의 코드를 수정해야함. 일단은 메세지만 출력하는 것으로 작성
        log.error("[Exception Handler] ", e);
    }


}
