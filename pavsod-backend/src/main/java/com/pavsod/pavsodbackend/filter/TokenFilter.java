package com.pavsod.pavsodbackend.filter;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.pavsod.pavsodbackend.utils.CurrentHolder;
import com.pavsod.pavsodbackend.utils.JWTUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@WebFilter(urlPatterns = "/*")
public class TokenFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();

        if(requestURI.contains("/login")){
            log.info("登录请求，放行");
            filterChain.doFilter(request, response);
            return;
        }

        if(requestURI.contains("/register")){
            log.info("注册请求，放行");
            filterChain.doFilter(request, response);
            return;
        }

        if(requestURI.contains("/")){
            log.info("开发时测试用，跳过token验证");
            filterChain.doFilter(request, response);
            return;
        }

        String token = request.getHeader("token");

        if(token == null || token.isEmpty()){
            log.info("令牌为空，相应401");
            response.setStatus(401);
            return;
        }


        try {
            DecodedJWT jwt = JWTUtil.verifyToken(token);

            //userId存入ThreadLocal
            Long userId = jwt.getClaim("id").asLong();
            CurrentHolder.setCurrentLocal(userId);
            log.info("用户 {} 请求 {} ", userId, requestURI);

        } catch (TokenExpiredException e) {
            log.warn("Token 过期: {}", token);
            response.setStatus(401);
            return;
        } catch (SignatureVerificationException e) {
            log.warn("Token 签名无效: {}", token);
            response.setStatus(401);
            return;
        } catch (AlgorithmMismatchException e) {
            log.warn("Token 算法不匹配: {}", token);
            response.setStatus(401);
            return;
        } catch (JWTDecodeException e) {
            log.warn("Token 格式错误: {}", token);
            response.setStatus(401);
            return;
        } catch (Exception e) {
            log.error("Token 验证未知错误", e);
            response.setStatus(401);
            return;
        }

        log.info("token合法，放行");
        filterChain.doFilter(request, response);

        //释放ThreadLocal的资源
        CurrentHolder.remove();
    }

}
