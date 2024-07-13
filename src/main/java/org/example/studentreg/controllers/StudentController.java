package org.example.studentreg.controllers;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.studentreg.dto.StudentDTO;
import org.example.studentreg.util.UtilProcess;

import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns = "/student")
public class StudentController extends HttpServlet {
    Connection connection;
    static String SAVE_STUDENT = "INSERT INTO student(id,name,city,level,email) VALUES (?,?,?,?,?)";
    static String GET_STUDENT = "SELECT * FROM student WHERE id = ?";
    static String DELETE_STUDENT = "DELETE FROM student WHERE id = ?";
    static String UPDATE_STUDENT = "UPDATE student SET name = ?,city = ?,level = ?,email = ? WHERE id = ?";

    @Override
    public void init() {
        try {
            var driVerClass = getServletContext().getInitParameter("driver");
            var dbURL = getServletContext().getInitParameter("dbURL");
            var user = getServletContext().getInitParameter("dbUsername");
            var password = getServletContext().getInitParameter("dbPassword");
            Class.forName(driVerClass);
            this.connection = DriverManager.getConnection(dbURL, user, password);

        }catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if(!req.getContentType().toLowerCase().startsWith("application/json")||req.getContentType() == null){
            resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        }
        //Todo : get student
        var studentDTO = new StudentDTO();
        try(var writer = resp.getWriter()){
            PreparedStatement pstm = connection.prepareStatement(GET_STUDENT);
            String id = req.getParameter("id");
            pstm.setString(1,id);
            ResultSet resultSet = pstm.executeQuery();
            while (resultSet.next()){

                studentDTO.setId(resultSet.getString("id"));
                studentDTO.setName(resultSet.getString("name"));
                studentDTO.setCity(resultSet.getString("city"));
                studentDTO.setLevel(resultSet.getString("level"));
                studentDTO.setEmail(resultSet.getString("email"));
            }
            resp.setContentType("application/json");
            var json = JsonbBuilder.create();
            json.toJson(studentDTO, resp.getWriter());
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //Todo : save student
        if(!req.getContentType().toLowerCase().startsWith("application/json")||req.getContentType() == null){
            resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        }
        //Single Bind Object JSON
        /*String id = UtilProcess.generateId();
        Jsonb jsonb = JsonbBuilder.create();
        StudentDTO studentDTO = jsonb.fromJson(req.getReader(), StudentDTO.class);
        studentDTO.setId(id);
        System.out.println(studentDTO);
*/
        /* BufferedReader reader = req.getReader();
        PrintWriter writer = resp.getWriter();
        StringBuilder sb = new StringBuilder();
        reader.lines().forEach(line -> sb.append(line+"\n"));
        System.out.println(sb.toString());
        writer.print(sb.toString());
        writer.close();*/
        //jason manipulate with Parson

       /* JsonReader reader = Json.createReader(req.getReader());
        JsonObject jsonObject = reader.readObject();
        System.out.println(jsonObject.getString("name"));*/

        /*JsonReader reader2 = Json.createReader(req.getReader());
        JsonArray array = reader2.readArray();
        for (int i = 0; i < array.size(); i++) {
            System.out.println(array.getJsonObject(i).getString("name"));
        }*/
        // JSON array with bind function

        /*Jsonb jsonb = JsonbBuilder.create();
        List<StudentDTO> students = jsonb.fromJson(req.getReader(),new ArrayList<StudentDTO>(){}.getClass().getGenericSuperclass());
        students.forEach(System.out::println);*/

        Jsonb jsonb = JsonbBuilder.create();
        StudentDTO sdto = jsonb.fromJson(req.getReader(), StudentDTO.class);
        try{
            var preparedStatement = connection.prepareStatement(SAVE_STUDENT);

            preparedStatement.setString(1, sdto.getId());
            preparedStatement.setString(2, sdto.getName());
            preparedStatement.setString(3,sdto.getCity());
            preparedStatement.setString(4,sdto.getLevel());
            preparedStatement.setString(5, sdto.getEmail());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //Todo : delete student
        if(!req.getContentType().toLowerCase().startsWith("application/json")||req.getContentType() == null){
            resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        }
        try(var writer = resp.getWriter()) {
            var pstm = this.connection.prepareStatement(DELETE_STUDENT);
            var id = req.getParameter("id");
            pstm.setString(1,id);
            int i = pstm.executeUpdate();
            if (i !=0){
                writer.write("Delete Successful");
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }else{
                writer.write("Delete failed");
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //Todo : update student
        if(!req.getContentType().toLowerCase().startsWith("application/json")||req.getContentType() == null){
            resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        }
        try(var writer = resp.getWriter()){
            var pstm = this.connection.prepareStatement(UPDATE_STUDENT);
            var id = req.getParameter("id");
            Jsonb jsonb = JsonbBuilder.create();
            var updatedStudent = jsonb.fromJson(req.getReader(),StudentDTO.class);
            pstm.setString(1,updatedStudent.getName());
            pstm.setString(2,updatedStudent.getCity());
            pstm.setString(3,updatedStudent.getLevel());
            pstm.setString(4, updatedStudent.getEmail());
            pstm.setString(5,id);
            if (pstm.executeUpdate() != 0){
                writer.write("Student Updated");
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }else{
                writer.write("Update Failed");
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            pstm.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public StudentController() {
        super();
    }

}
