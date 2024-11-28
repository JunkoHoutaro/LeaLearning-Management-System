// package com.example.Mini_Project1.controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.validation.annotation.Validated;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.example.Mini_Project1.request.LoginRequest;
// import com.example.Mini_Project1.request.SignUpRequest;
// import com.example.Mini_Project1.service.StudentService;

// import jakarta.validation.Valid;

// @RestController
// @RequestMapping("/student")
// public class StudentController {
//     @Autowired
//     private StudentService studentService;

//     @PostMapping("/signUp")
//     public ResponseEntity signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
//         return studentService.signUp(signUpRequest);
//     }

//     @PostMapping("/login")
//     public ResponseEntity login(@Valid @RequestBody LoginRequest loginRequest) {
//         return studentService.login(loginRequest);
//     }
// }
