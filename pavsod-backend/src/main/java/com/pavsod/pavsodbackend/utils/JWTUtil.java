package com.pavsod.pavsodbackend.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.pavsod.pavsodbackend.config.AppConfig;
import com.pavsod.pavsodbackend.dto.UserLoginDTO;

import java.util.Date;

public class JWTUtil {


    public static String getToken(long id){
        Algorithm algorithm = Algorithm.HMAC256(AppConfig.DEFAULT_JWT_SECRET);
        String token = JWT.create()
                .withIssuer("admin")//签发者
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + 7200 * 1000))//token过期时间2h
                .withAudience("app")//校验jwt的一方
                .withClaim("id",id)
                .sign(algorithm);

        return token;
    }

    /**
     * 验证 Token 并返回 DecodedJWT（从中获取数据）
     */
    public static DecodedJWT verifyToken(String token) {
        return JWT.require(Algorithm.HMAC256(AppConfig.DEFAULT_JWT_SECRET)).build().verify(token);
    }

    /**
     * 从 Token 中获取 userId
     */
    public static Long getUserId(String token) {
        DecodedJWT jwt = verifyToken(token);
        return jwt.getClaim("userId").asLong();
    }
}
