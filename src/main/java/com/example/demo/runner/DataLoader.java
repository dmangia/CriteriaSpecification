package com.example.demo.runner;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import com.example.demo.entity.Address;
import com.example.demo.entity.Student;
import com.example.demo.entity.Subject;
import com.example.demo.repo.AddressRepository;
import com.example.demo.repo.StudentRepository;

//@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Override
    public void run(String... args) throws Exception {
        // Create and save addresses
        Address address1 = createAndSaveAddress("New York City");
        Address address2 = createAndSaveAddress("Chicago");
        Address address3 = createAndSaveAddress("Houston");

        // Create and save students with subjects
        createAndSaveStudent("Rocco", address1, "JAVA", "Spring Boot", "JUnit");
        createAndSaveStudent("Jerry", address2, "Angular", "CSS", "Javascript");
        createAndSaveStudent("William", address3, "Git", "Jenkins", "Jira");
    }

    private Address createAndSaveAddress(String city) {
        Address address = new Address();
        address.setCity(city);
        return addressRepository.save(address);
    }

    private void createAndSaveStudent(String studentName, Address address, String... subjects) {
        Student student = new Student();
        student.setName(studentName);
        student.setAddress(address);
        Set<Subject> subjectSet = new HashSet<>();
        for (String subjectName : subjects) {
            Subject subject = new Subject();
            subject.setName(subjectName);
            subject.setStudentId(student);
            subjectSet.add(subject);
        }
        student.setSubjects(subjectSet);
        studentRepository.save(student); // This will cascade and save subjects as well
    }

}
