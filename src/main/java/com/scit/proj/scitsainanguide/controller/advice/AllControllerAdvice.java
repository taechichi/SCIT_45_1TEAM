package com.scit.proj.scitsainanguide.controller.advice;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class AllControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EntityNotFoundException.class)
    public void entityNotFoundExHandle(EntityNotFoundException e) {
        // TODO 추후 에러처리 방안이 생긴다면 아래의 코드를 수정해야함. 일단은 메세지만 출력하는 것으로 작성
        log.error("[EntityNotFoundException Handler] ", e);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public void exHandle(Exception e) {
        // TODO 추후 에러처리 방안이 생긴다면 아래의 코드를 수정해야함. 일단은 메세지만 출력하는 것으로 작성
        log.error("[Exception Handler] ", e);
    }


}
