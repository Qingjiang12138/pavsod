package com.pavsod.pavsodbackend.exception;


import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class LoginExceptionHandler {

    //Token过期
    @ExceptionHandler(TokenExpiredException.class)
    public Result handleTokenExpiredException(TokenExpiredException e) {
        return Result.error(400, "token已过期");
    }

    @ExceptionHandler(SignatureVerificationException.class)
    public Result handleSignatureVerificationException(SignatureVerificationException e){
        return Result.error(400, "登录状态异常，请重新登录");
    }


    @ExceptionHandler(AlgorithmMismatchException.class)
    public Result handleAlgorithmMismatchException(AlgorithmMismatchException e){
        return Result.error(400, "登录状态异常，请重新登录");
    }

    @ExceptionHandler(JWTDecodeException.class)
    public Result handleResultJWTDecodeException(JWTDecodeException e){
        return Result.error(400, "登录状态异常，请重新登录");
    }
}
