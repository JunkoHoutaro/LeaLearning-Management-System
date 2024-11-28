// package com.example.Mini_Project1.utils;

// import java.time.Instant;
// import java.time.temporal.ChronoUnit;
// import java.util.Date;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Component;

// import com.example.Mini_Project1.entity.Student;
// import com.example.Mini_Project1.exception.ExceptionCode;
// import com.example.Mini_Project1.exception.UserException;
// import com.nimbusds.jose.JWSAlgorithm;
// import com.nimbusds.jose.JWSHeader;
// import com.nimbusds.jose.JWSObject;
// import com.nimbusds.jose.JWSVerifier;
// import com.nimbusds.jose.Payload;
// import com.nimbusds.jose.crypto.MACSigner;
// import com.nimbusds.jose.crypto.MACVerifier;
// import com.nimbusds.jwt.JWTClaimsSet;
// import com.nimbusds.jwt.SignedJWT;

// @Component
// public class JwtTokenUtils {
// @Value("${jwt.scretKey}")
// private String secretKey;

// public String createToken(Student student) {
// JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
// JWTClaimsSet claim = new JWTClaimsSet.Builder()
// .subject(student.getId())
// .issuer("FPT Mini Project 1")
// .issueTime(new Date())
// .expirationTime(new Date(
// Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
// .claim("scope", buildScope("student"))
// .build();
// Payload payload = new Payload(claim.toJSONObject());
// JWSObject jwsObject = new JWSObject(header, payload);
// try {
// jwsObject.sign(new MACSigner(secretKey.getBytes()));
// return jwsObject.serialize();
// } catch (Exception e) {
// e.printStackTrace();
// throw new UserException(ExceptionCode.CreateTokenFail);
// }
// }

// public String verifyToken(String token) { // this function return studentId
// try {
// JWSVerifier verifier = new MACVerifier(this.secretKey.getBytes());
// SignedJWT signedJWT = SignedJWT.parse(token);
// Date expireTime = (Date) signedJWT.getJWTClaimsSet().getExpirationTime();
// if (expireTime.before(new Date())) {
// throw new UserException(ExceptionCode.VerifyTokenFail);
// } else {
// if (signedJWT.verify(verifier)) {
// return signedJWT.getJWTClaimsSet().getSubject();
// } else {
// throw new UserException(ExceptionCode.VerifyTokenFail);
// }
// }
// } catch (Exception e) {
// throw new UserException(ExceptionCode.VerifyTokenFail);
// }
// }

// public String buildScope(String role) {
// return "ROLE_" + role;
// }
// }
