package com.studentapp.studentapp.services.implementation.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
    public ResponseEntity<?> addStudentData(JsonNode jsonNode) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        if (!jsonNode.has("first_name") || !jsonNode.has("last_name")
                || !jsonNode.has("age")) {
            objectNode.put("Status", "ERROR! `first_name`, `last_name` & `age` are required parameters.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
        String firstName = jsonNode.get("first_name").asText();
        String lastName = jsonNode.get("last_name").asText();
        int age = jsonNode.get("age").asInt();
        if (studentRepository.findByFirstNameAndLastNameAndAge(firstName, lastName, age) != null) {
            objectNode.put("Status", "ERROR! Similar record exists.");
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(null);
        }
        Student newStudent = new Student();
        newStudent.setFirstName(firstName);
        newStudent.setLastName(lastName);
        newStudent.setAge(age);
        studentRepository.save(newStudent);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(newStudent);
    }

    @Override
    public ResponseEntity<?> updateStudentData(JsonNode jsonNode) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        if (!jsonNode.has("id")) {
            objectNode.put("Status", "ERROR! `id` is a required field.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
        long id = jsonNode.get("id").asLong();
        Student student = studentRepository.findById(id);
        if (student == null) {
            objectNode.put("Status", "ERROR! `id` " + id + " does not exist.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        } else {
            if (jsonNode.has("first_name")) {
                student.setFirstName(jsonNode.get("first_name").asText());
            }
            if (jsonNode.has("last_name")) {
                student.setLastName(jsonNode.get("last_name").asText());
            }
            if (jsonNode.has("age")) {
                student.setAge(jsonNode.get("age").asInt());
            }
            studentRepository.save(student);
        }
        student = studentRepository.findById(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(student);
    }
}
