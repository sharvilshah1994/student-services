package com.studentapp.studentapp.services.implementation.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studentapp.studentapp.model.Student;
import com.studentapp.studentapp.repository.StudentRepository;
import com.studentapp.studentapp.services.implementation.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StudentServiceImpl implements StudentService{

    private final StudentRepository studentRepository;
    private ObjectMapper objectMapper;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<Student> getAllStudentData() {
        return studentRepository.findAll();
    }

    @Override
    public ResponseEntity<Student> addStudentData(JsonNode jsonNode) {
        if (!jsonNode.has("first_name") || !jsonNode.has("last_name") || !jsonNode.has("age")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String firstName = jsonNode.get("first_name").asText();
        String lastName = jsonNode.get("last_name").asText();
        int age = jsonNode.get("age").asInt();
        if (studentRepository.findByFirstNameAndLastNameAndAge(firstName, lastName, age) != null) {
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(null);
        }
        Student newStudent = new Student();
        newStudent.setFirstName(firstName);
        newStudent.setLastName(lastName);
        newStudent.setAge(age);
        studentRepository.save(newStudent);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(newStudent);
    }
}
