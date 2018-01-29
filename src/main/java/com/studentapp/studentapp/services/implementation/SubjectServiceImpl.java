package com.studentapp.studentapp.services.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.studentapp.studentapp.model.School;
import com.studentapp.studentapp.model.Subject;
import com.studentapp.studentapp.model.Teacher;
import com.studentapp.studentapp.repository.SchoolRepository;
import com.studentapp.studentapp.repository.SubjectRepository;
import com.studentapp.studentapp.repository.TeacherRepository;
import com.studentapp.studentapp.services.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static com.studentapp.studentapp.config.Constants.*;

@Component
public class SubjectServiceImpl implements SubjectService {

    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final SchoolRepository schoolRepository;
    private ObjectMapper objectMapper;

    @Autowired
    public SubjectServiceImpl(TeacherRepository teacherRepository, SubjectRepository subjectRepository,
                              SchoolRepository schoolRepository) {
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
        this.schoolRepository = schoolRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<Subject> getAllSubjectData() {
        return subjectRepository.findAll();
    }

    @Override
    public ResponseEntity<ObjectNode> addSubjectData(JsonNode jsonNode) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        if (!jsonNode.has(SUBJECT_NAME) || !jsonNode.has(TEACHER_ID) || !jsonNode.has(SCHOOL_ID)) {
            objectNode.put(STATUS, "ERROR! `subject_name`, `teacher_id` & `school_id` are required parameters.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
        String subjectName = jsonNode.get("subject_name").asText();
        long teacherId = jsonNode.get("teacher_id").asLong();
        long schoolId = jsonNode.get("school_id").asLong();
        Teacher teacher = teacherRepository.findOne(teacherId);
        if (teacher == null) {
            objectNode.put(STATUS, "ERROR! Teacher Id: " + teacherId + " not found. Cannot add subject.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(objectNode);
        }
        if (schoolId != teacher.getSchool().getId()) {
            objectNode.put(STATUS, "ERROR! Teacher Id and School Id mismatch.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
        School school = schoolRepository.findOne(schoolId);
        if (school == null) {
            objectNode.put(STATUS, "ERROR! School Id: " + schoolId+ " not found. Cannot add subject.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(objectNode);
        }
        Subject newSubject = new Subject();
        newSubject.setSubjectName(subjectName);
        newSubject.setTeacher(teacher);
        newSubject.setSchool(school);
        subjectRepository.save(newSubject);
        objectNode.put(ID, newSubject.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(objectNode);
    }

    @Override
    public ResponseEntity<ObjectNode> updateSubjectData(JsonNode jsonNode) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        if (!jsonNode.has(ID)) {
            objectNode.put(STATUS, "ERROR! `id` is a required field.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
        long id = jsonNode.get(ID).asLong();
        Subject subject = subjectRepository.findOne(id);
        if (subject == null) {
            objectNode.put(STATUS, "ERROR! `Subject Id: " + id + " does not exist.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(objectNode);
        }
        if (jsonNode.has(SUBJECT_NAME)) {
            subject.setSubjectName(jsonNode.get(SUBJECT_NAME).asText());
        }
        if (jsonNode.has(TEACHER_ID)) {
            Teacher teacher = teacherRepository.findOne(jsonNode.get(TEACHER_ID).asLong());
            if (teacher == null) {
                objectNode.put(STATUS, "ERROR! `teacher_id` is invalid. Cannot update the record!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
            if (teacher.getSchool().getId() != subject.getSchool().getId()) {
                objectNode.put(STATUS, "ERROR! Teacher Id and School Id mismatch.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
            subject.setTeacher(teacher);
            Set<Subject> subjectSet = teacher.getSubjects();
            subjectSet.remove(subject);
            teacher.setSubjects(subjectSet);
            teacherRepository.save(teacher);
        }
        if (jsonNode.has(SCHOOL_ID)) {
            School school = schoolRepository.findOne(jsonNode.get(SCHOOL_ID).asLong());
            if (school == null) {
                objectNode.put(STATUS, "ERROR! `school_id` is invalid. Cannot update the record!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
            if (school.getId() != subject.getTeacher().getSchool().getId()) {
                objectNode.put(STATUS, "ERROR! Teacher Id and School Id mismatch.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
            }
            subject.setSchool(school);
            Set<Subject> subjectSet = school.getSubjects();
            subjectSet.remove(subject);
            school.setSubjects(subjectSet);
            schoolRepository.save(school);
        }
        subjectRepository.save(subject);
        objectNode.put(SUBJECT_NAME, subject.getSubjectName());
        objectNode.put(TEACHER_ID, subject.getTeacher().getId());
        objectNode.put(SCHOOL_ID, subject.getSchool().getId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(objectNode);
    }

    @Override
    public ResponseEntity<ObjectNode> deleteSubjectData(long id) {
        Subject subject = subjectRepository.findOne(id);
        ObjectNode objectNode = objectMapper.createObjectNode();
        if (subject == null) {
            objectNode.put(STATUS, "ERROR! Subject id: " + id + " not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(objectNode);
        }
        subjectRepository.delete(id);
        objectNode.put(STATUS, "SUCCESS! Subject Id: " + id + " deleted.");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(objectNode);
    }
}
