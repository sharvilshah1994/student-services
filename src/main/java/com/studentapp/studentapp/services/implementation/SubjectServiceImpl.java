package com.studentapp.studentapp.services.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.studentapp.studentapp.model.Subject;
import com.studentapp.studentapp.model.Teacher;
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
    private ObjectMapper objectMapper;

    @Autowired
    public SubjectServiceImpl(TeacherRepository teacherRepository, SubjectRepository subjectRepository) {
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<Subject> getAllSubjectData() {
        return subjectRepository.findAll();
    }

    @Override
    public ResponseEntity<ObjectNode> addSubjectData(JsonNode jsonNode) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        if (!jsonNode.has(SUBJECT_NAME) || !jsonNode.has(TEACHER_ID)) {
            objectNode.put(STATUS, "ERROR! `subject_name` & `teacher_id` are required parameters.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectNode);
        }
        String subjectName = jsonNode.get("subject_name").asText();
        long teacherId = jsonNode.get("teacher_id").asLong();
        Teacher teacher = teacherRepository.findOne(teacherId);
        if (teacher == null) {
            objectNode.put(STATUS, "ERROR! Teacher Id: " + teacherId + " not found. Cannot add subject.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(objectNode);
        }
        Subject newSubject = new Subject();
        newSubject.setSubjectName(subjectName);
        newSubject.setTeacher(teacher);
        subjectRepository.save(newSubject);
        objectNode.put(ID, newSubject.getId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(objectNode);
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
            subject.setTeacher(teacher);
            Set<Subject> subjectSet = teacher.getSubjects();
            subjectSet.remove(subject);
            teacher.setSubjects(subjectSet);
            teacherRepository.save(teacher);
        }
        subjectRepository.save(subject);
        objectNode.put(SUBJECT_NAME, subject.getSubjectName());
        objectNode.put(TEACHER_ID, subject.getTeacher().getId());
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
        Teacher teacher = subject.getTeacher();
        Set<Subject> subjectSet = teacher.getSubjects();
        subjectSet.remove(subject);
        teacher.setSubjects(subjectSet);
        teacherRepository.save(teacher);
        subjectRepository.delete(id);
        objectNode.put(STATUS, "SUCCESS! Subject Id: " + id + " deleted.");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(objectNode);
    }
}
