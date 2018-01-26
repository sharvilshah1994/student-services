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
    
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String AGE = "age";
    private static final String STATUS = "status";

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
    public ResponseEntity<ObjectNode> addStudentData(JsonNode jsonNode) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        if (!jsonNode.has(FIRST_NAME) || !jsonNode.has(LAST_NAME)
                || !jsonNode.has(AGE)) {
            objectNode.put(STATUS, "ERROR! `first_name`, `last_name` & `age` are required parameters.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
        String firstName = jsonNode.get(FIRST_NAME).asText();
        String lastName = jsonNode.get(LAST_NAME).asText();
        int age = jsonNode.get(AGE).asInt();
        if (studentRepository.findByFirstNameAndLastNameAndAge(firstName, lastName, age) != null) {
            objectNode.put(STATUS, "ERROR! Similar record exists.");
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(null);
        }
        Student newStudent = new Student();
        newStudent.setFirstName(firstName);
        newStudent.setLastName(lastName);
        newStudent.setAge(age);
        studentRepository.save(newStudent);
        objectNode.put("id", newStudent.getId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(objectNode);
    }

    @Override
    public ResponseEntity<ObjectNode> updateStudentData(JsonNode jsonNode) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        if (!jsonNode.has("id")) {
            objectNode.put(STATUS, "ERROR! `id` is a required field.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
        long id = jsonNode.get("id").asLong();
        Student student = studentRepository.findById(id);
        if (student == null) {
            objectNode.put(STATUS, "ERROR! `id` " + id + " does not exist.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(objectNode);
        }
        if (jsonNode.has(FIRST_NAME)) {
            student.setFirstName(jsonNode.get(FIRST_NAME).asText());
        }
        if (jsonNode.has(LAST_NAME)) {
            student.setLastName(jsonNode.get(LAST_NAME).asText());
        }
        if (jsonNode.has(AGE)) {
            student.setAge(jsonNode.get(AGE).asInt());
        }
        studentRepository.save(student);
        objectNode.put(FIRST_NAME, student.getFirstName());
        objectNode.put(LAST_NAME, student.getLastName());
        objectNode.put(AGE, student.getAge());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(objectNode);
    }

    @Override
    public ResponseEntity<ObjectNode> deleteStudentData(long id) {
        Student student = studentRepository.findById(id);
        ObjectNode objectNode = objectMapper.createObjectNode();
        if (student == null) {
            objectNode.put(STATUS, "ERROR! Id: " + id + " does not exist.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(objectNode);
        }
        studentRepository.delete(student);
        objectNode.put(STATUS, "SUCCESS! Id: " + id + " deleted.");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(objectNode);
    }
}
