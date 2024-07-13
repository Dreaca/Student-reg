package org.example.studentreg.persistence;

import org.example.studentreg.dto.StudentDTO;

import java.sql.Connection;
import java.sql.SQLException;

public interface Data {
    StudentDTO getStudentDTO(String studentId, Connection connection) throws SQLException;
    String saveStudentDTO(StudentDTO studentDTO, Connection connection) throws SQLException;
    boolean deleteStudentDTO(String studentId , Connection connection) throws SQLException;
    boolean updateStudentDTO(StudentDTO studentDTO, String studentId, Connection connection) throws SQLException;
}
