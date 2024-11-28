// package com.example.Mini_Project1.service;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;

// import com.example.Mini_Project1.entity.Student;
// import com.example.Mini_Project1.exception.ExceptionCode;
// import com.example.Mini_Project1.exception.UserException;
// import com.example.Mini_Project1.mapper.StudentMapper;
// import com.example.Mini_Project1.repository.StudentRepository;
// import com.example.Mini_Project1.request.LoginRequest;
// import com.example.Mini_Project1.request.SignUpRequest;
// import com.example.Mini_Project1.response.Response;
// import com.example.Mini_Project1.response.ResponseCode;
// import com.example.Mini_Project1.support_service.SupportStudentService;
// import com.example.Mini_Project1.utils.JwtTokenUtils;
// import com.example.Mini_Project1.utils.PasswordUtils;

// @Service
// public class StudentService {
// @Autowired
// private StudentRepository studentRepository;

// @Autowired
// private StudentMapper studentMapper;

// @Autowired
// private JwtTokenUtils jwtTokenUtils;

// public ResponseEntity signUp(SignUpRequest signUpRequest) {
// // check email
// if (SupportStudentService.checkStudentEmail(signUpRequest.getEmail(),
// studentRepository)) {
// // convert signUpRequest to student
// Student student = this.studentMapper.signUpRequestToStudent(signUpRequest);
// // set password
// student.setPassword(PasswordUtils.encryptPassword(signUpRequest.getPassword()));
// // save
// this.studentRepository.save(student);
// // create response
// Response response = new
// Response(ResponseCode.SignUpSuccessfully.responseCodeToJson(), null);
// // retrun
// return ResponseEntity.ok().body(response);
// } else {
// // throw exception
// throw new UserException(ExceptionCode.EmailAlreadyExists);
// }
// }

// public ResponseEntity login(LoginRequest loginRequest) {
// // get student
// Student student =
// SupportStudentService.getStudentUsingEmail(loginRequest.getEmail(),
// studentRepository);
// // check student
// if (student == null) {
// // throw exception
// throw new UserException(ExceptionCode.EmailDoesNotExist);
// } else {
// // check password
// if (PasswordUtils.checkPassword(loginRequest.getPassword(),
// student.getPassword()) == 1) {
// // create token
// String token = this.jwtTokenUtils.createToken(student);
// // create response
// Response response = new
// Response(ResponseCode.LoginSuccessfully.responseCodeToJson(), token);
// // return
// return ResponseEntity.ok().body(response);
// } else {
// // password wrong
// throw new UserException(ExceptionCode.WrongPassword);
// }
// }
// }
// }
