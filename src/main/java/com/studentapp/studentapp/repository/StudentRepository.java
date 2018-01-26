package com.studentapp.studentapp.repository;

import com.studentapp.studentapp.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByFirstNameAndLastNameAndAge(String firstName, String lastName, int age);
}
