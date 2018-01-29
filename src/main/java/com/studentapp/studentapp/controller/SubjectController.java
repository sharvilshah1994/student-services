package com.studentapp.studentapp.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.studentapp.studentapp.model.Subject;
import com.studentapp.studentapp.services.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/subject", produces = MediaType.APPLICATION_JSON_VALUE)
public class SubjectController {

    private final SubjectService subjectService;

    @Autowired
    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Subject> getAllSubjectData() {
        return subjectService.getAllSubjectData();
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ObjectNode> addSubjectData(@RequestBody JsonNode jsonNode) {
        return subjectService.addSubjectData(jsonNode);
    }

    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ObjectNode> updateSubjectData(@RequestBody JsonNode jsonNode) {
        return subjectService.updateSubjectData(jsonNode);
    }

    @RequestMapping(method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE, value = "/{id}")
    @ResponseBody
    public ResponseEntity<ObjectNode> deleteSubjectData(@PathVariable("id") long id) {
        return subjectService.deleteSubjectData(id);
    }
}
