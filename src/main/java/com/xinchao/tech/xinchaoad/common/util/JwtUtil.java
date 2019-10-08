package com.xinchao.tech.xinchaoad.common.util;

import com.xinchao.tech.xinchaoad.common.exception.BaseException;
import com.xinchao.tech.xinchaoad.common.exception.ResultCode;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {
    static final String SECRET = "ThisIsASecret";

    public static String generateToken(String username) {
        HashMap<String, Object> map = new HashMap<>();
        //you can put any data in the map
        map.put("username", username);
        String jwt = Jwts.builder()
                .setClaims(map)
                .setExpiration(new Date(System.currentTimeMillis() + 3600_000_000L))// 1000 hour
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
        return jwt; //jwt前面一般都会加Bearer
    }

    public static Map validateToken(String token) {
        try {
            // parse the token.
            Map<String, Object> body = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token.replace("Bearer ", ""))
                    .getBody();
            return body;
        } catch (Exception e) {
            throw new BaseException(ResultCode.FAIL_AUTH_FAIL.getCode(), "token错误：" + token, e);
        }
    }

    public static void main(String[] args) {
        Map map = validateToken("eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE1NDk2NDk5ODYsInVzZXJuYW1lIjoiOWY2N2ExOWItMWVhYi00OGVmLWI5MDktMDFkNDkwNWM2Mjk1In0.ohyvubAeNlpuT9kRu4AAGwkPTytC-VdXjGvW4JbWS6N1M6L5TyJGe2cTkvJXug0Sj76jRk8oUT_htHk8LLAwew");
        System.out.println(map.get("username"));
    }

}