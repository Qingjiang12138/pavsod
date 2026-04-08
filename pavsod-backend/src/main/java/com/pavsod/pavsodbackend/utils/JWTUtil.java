package com.pavsod.pavsodbackend.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.pavsod.pavsodbackend.dto.UserLoginDTO;

import java.util.Date;

public class JWTUtil {

    public static String getToken(UserLoginDTO dto){
        Algorithm algorithm = Algorithm.HMAC256("liqingjiang");
        String token = JWT.create()
                .withIssuer("admin")//签发者
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 1000))//token过期时间2h
                .withAudience("app")//校验jwt的一方
                .withClaim("phone",dto.getPhone())
                .sign(algorithm);

        return token;
    }

    public static void checkToken(String token){
        JWT.require(Algorithm.HMAC256("liqingjiang")).build().verify(token);
    }
}
