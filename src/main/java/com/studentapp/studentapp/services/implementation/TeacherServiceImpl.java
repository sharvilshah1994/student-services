package com.studentapp.studentapp.services.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.studentapp.studentapp.model.School;
import com.studentapp.studentapp.model.Teacher;
import com.studentapp.studentapp.repository.SchoolRepository;
import com.studentapp.studentapp.repository.StudentRepository;
import com.studentapp.studentapp.repository.TeacherRepository;
import com.studentapp.studentapp.services.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static com.studentapp.studentapp.config.Constants.*;

@Component
public class TeacherServiceImpl implements TeacherService {


    private final TeacherRepository teacherRepository;
    private final SchoolRepository schoolRepository;
    private ObjectMapper objectMapper;

    @Autowired
    public TeacherServiceImpl(StudentRepository studentRepository, TeacherRepository teacherRepository,
                              SchoolRepository schoolRepository) {
        this.teacherRepository = teacherRepository;
        this.schoolRepository = schoolRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<Teacher> getAllTeacherData() {
        return teacherRepository.findAll();
    }

    @Override
    public ResponseEntity<ObjectNode> addTeacherData(JsonNode jsonNode) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        if (!jsonNode.has(FIRST_NAME) || !jsonNode.has(LAST_NAME)
                || !jsonNode.has(SCHOOL_ID)) {
            objectNode.put(STATUS, "ERROR! `first_name`, `last_name` & `school_id` are required parameters.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
        String firstName = jsonNode.get(FIRST_NAME).asText();
        String lastName = jsonNode.get(LAST_NAME).asText();
        long schoolId = jsonNode.get(SCHOOL_ID).asLong();
        School school = schoolRepository.findOne(schoolId);
        if (school == null) {
            objectNode.put(STATUS, "ERROR! School id: " + schoolId + " not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(objectNode);
        }
        Teacher newTeacher = new Teacher();
        newTeacher.setFirstName(firstName);
        newTeacher.setLastName(lastName);
        newTeacher.setSchool(school);
        teacherRepository.save(newTeacher);
        objectNode.put(ID, newTeacher.getId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(objectNode);
    }

    @Override
    public ResponseEntity<ObjectNode> updateTeacherData(JsonNode jsonNode) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        if (!jsonNode.has(ID)) {
            objectNode.put(STATUS, "ERROR! `id` is a required field.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
        long id = jsonNode.get(ID).asLong();
        Teacher teacher = teacherRepository.findOne(id);
        if (teacher == null) {
            objectNode.put(STATUS, "ERROR! `Teacher id` " + id + " does not exist.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(objectNode);
        }
        if (jsonNode.has(FIRST_NAME)) {
            teacher.setFirstName(jsonNode.get(FIRST_NAME).asText());
        }
        if (jsonNode.has(LAST_NAME)) {
            teacher.setLastName(jsonNode.get(LAST_NAME).asText());
        }
        if (jsonNode.has(SCHOOL_ID)) {
            School school = schoolRepository.findOne(jsonNode.get(SCHOOL_ID).asLong());
            if (school == null) {
                objectNode.put(STATUS, "ERROR! `school_id` is invalid. Cannot update the record!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
            teacher.setSchool(school);
            Set<Teacher> teacherSet = school.getTeachers();
            teacherSet.remove(teacher);
            school.setTeachers(teacherSet);
            schoolRepository.save(school);
        }
        teacherRepository.save(teacher);
        objectNode.put(FIRST_NAME, teacher.getFirstName());
        objectNode.put(LAST_NAME, teacher.getLastName());
        objectNode.put(SCHOOL_ID, teacher.getSchool().getId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(objectNode);
    }

    @Override
    public ResponseEntity<ObjectNode> deleteTeacherData(long id) {
        Teacher teacher = teacherRepository.findOne(id);
        ObjectNode objectNode = objectMapper.createObjectNode();
        if (teacher == null) {
            objectNode.put(STATUS, "ERROR! Teacher id: " + id + " not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(objectNode);
        }
        School school = teacher.getSchool();
        Set<Teacher> teacherSet = school.getTeachers();
        teacherSet.remove(teacher);
        school.setTeachers(teacherSet);
        schoolRepository.save(school);
        teacherRepository.delete(id);
        objectNode.put(STATUS, "SUCCESS! Teacher Id: " + id + " deleted.");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(objectNode);
    }
}
