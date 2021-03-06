package com.studentapp.studentapp.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.studentapp.studentapp.services.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/school", produces = MediaType.APPLICATION_JSON_VALUE)
public class SchoolController {

    private final SchoolService schoolService;

    @Autowired
    public SchoolController(SchoolService schoolService) {
        this.schoolService = schoolService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ArrayNode> getAllSchoolData() {
        return schoolService.getSchoolData();
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ObjectNode> addSchoolData(@RequestBody JsonNode jsonNode) {
        return schoolService.addSchoolData(jsonNode);
    }

    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ObjectNode> updateSchoolData(@RequestBody JsonNode jsonNode) {
        return schoolService.updateSchoolData(jsonNode);
    }

    @RequestMapping(method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE, value = "/{id}")
    @ResponseBody
    public ResponseEntity<ObjectNode> deleteSchoolData(@PathVariable("id") long id) {
        return schoolService.deleteSchoolData(id);
    }
}
