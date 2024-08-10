package org.example.studentreg.persistence;
import org.example.studentreg.dto.StudentDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class StudentDataProcess implements StudentData {
    static String SAVE_STUDENT = "INSERT INTO student(id,name,city,level,email) VALUES (?,?,?,?,?)";
    static String GET_STUDENT = "SELECT * FROM student WHERE id = ?";
    static String DELETE_STUDENT = "DELETE FROM student WHERE id = ?";
    static String UPDATE_STUDENT = "UPDATE student SET name = ?,city = ?,level = ?,email = ? WHERE id = ?";

    @Override
    public StudentDTO getStudentDTO(String studentId, Connection connection) throws SQLException {
        StudentDTO studentDTO = new StudentDTO();
        try{
            PreparedStatement pstm = connection.prepareStatement(GET_STUDENT);
            String id = studentId;
            pstm.setString(1,id);
            ResultSet resultSet = pstm.executeQuery();
            while (resultSet.next()){

                studentDTO.setId(resultSet.getString("id"));
                studentDTO.setName(resultSet.getString("name"));
                studentDTO.setCity(resultSet.getString("city"));
                studentDTO.setLevel(resultSet.getString("level"));
                studentDTO.setEmail(resultSet.getString("email"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return studentDTO;
    }

    @Override
    public String saveStudentDTO(StudentDTO studentDTO, Connection connection) throws SQLException {

        String message = "";
        try{
            var preparedStatement = connection.prepareStatement(SAVE_STUDENT);

            preparedStatement.setString(1, studentDTO.getId());
            preparedStatement.setString(2, studentDTO.getName());
            preparedStatement.setString(3,studentDTO.getCity());
            preparedStatement.setString(4,studentDTO.getLevel());
            preparedStatement.setString(5, studentDTO.getEmail());
            if (preparedStatement.executeUpdate()!=0) {
                message = "Student saved successfully";
            }
            else {
                message = "Student not saved";
            }
            preparedStatement.close();

        }catch (SQLException e){
            e.printStackTrace();
        }
        return message;
    }

    @Override
    public boolean deleteStudentDTO(String studentId, Connection connection) throws SQLException {
        try{
            var pstm = connection.prepareStatement(DELETE_STUDENT);
            pstm.setString(1,studentId);
            return pstm.executeUpdate()!=0;
        }catch (SQLException e){
            throw e;
        }
    }

    @Override
    public boolean updateStudentDTO(StudentDTO studentDTO, String studentId, Connection connection) throws SQLException {

        try (var pstm = connection.prepareStatement(UPDATE_STUDENT)) {

            pstm.setString(1,studentDTO.getName());
            pstm.setString(2,studentDTO.getCity());
            pstm.setString(3,studentDTO.getLevel());
            pstm.setString(4, studentDTO.getEmail());
            pstm.setString(5,studentId);

            return (pstm.executeUpdate()!=0);
        }
        catch (SQLException e){
            throw e;
        }
    }
}
