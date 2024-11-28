// package com.example.Mini_Project1.deserializer;

// import com.example.Mini_Project1.exception.ExceptionCode;
// import com.example.Mini_Project1.exception.UserException;
// import com.fasterxml.jackson.core.JsonParser;
// import com.fasterxml.jackson.databind.DeserializationContext;
// import com.fasterxml.jackson.databind.JsonDeserializer;
// import java.io.IOException;
// import java.time.LocalDate;
// import java.time.format.DateTimeFormatter;
// import java.time.format.DateTimeParseException;

// public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

// @Override
// public LocalDate deserialize(JsonParser p, DeserializationContext ctxt)
// throws IOException {
// String dateStr = p.getText().trim();

// // try parsing to localdate
// try {
// return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
// } catch (DateTimeParseException e) {
// throw new UserException(ExceptionCode.LocalDateFormat);
// }
// }
// }
