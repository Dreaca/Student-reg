package org.example.studentreg.controllers;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.studentreg.dto.StudentDTO;
import org.example.studentreg.persistence.implementation.DataProcess;
import org.example.studentreg.util.UtilProcess;

import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns = "/student"
/*,
    initParams = {
    @WebInitParam(name = "driver", value = "com.mysql.cj.jdbc.Driver"),
    @WebInitParam(name = "dbURL", value = "jdbc:mysql://localhost:3306/studentregdb"),
    @WebInitParam(name = "dbUsername", value = "root"),
    @WebInitParam(name = "dbPassword", value = "Ijse@1234")
}*/
)
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
            /*var driVerClass = getServletConfig().getInitParameter("driver");
            var dbURL = getServletConfig().getInitParameter("dbURL");
            var user = getServletConfig().getInitParameter("dbUsername");
            var password = getServletConfig().getInitParameter("dbPassword");*/

            Class.forName(driVerClass);
            this.connection = DriverManager.getConnection(dbURL, user, password);

        }catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Todo : get student

            String id = req.getParameter("id");
            DataProcess dataProcess = new DataProcess();

            try(var writer = resp.getWriter())
            {
                var studentDTO = dataProcess.getStudentDTO(id, connection);
                resp.setContentType("application/json");
                var json = JsonbBuilder.create();
                json.toJson(studentDTO, writer);
            }
            catch (SQLException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //Todo : save student
        if(!req.getContentType().toLowerCase().startsWith("application/json")||req.getContentType() == null) {
            resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        }
        try(var writer = resp.getWriter())
        {
            Jsonb jsonb = JsonbBuilder.create();
            StudentDTO sdto = jsonb.fromJson(req.getReader(), StudentDTO.class);
            DataProcess dataProcess = new DataProcess();
            sdto.setId(UtilProcess.generateId());

            writer.write(dataProcess.saveStudentDTO(sdto, connection));
            resp.setStatus(HttpServletResponse.SC_CREATED);

        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //Todo : delete student
        if(!req.getContentType().toLowerCase().startsWith("application/json")||req.getContentType() == null){
            resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        }
        DataProcess dataProcess = new DataProcess();
        try(var writer = resp.getWriter()) {

            var id = req.getParameter("id");
            boolean b = dataProcess.deleteStudentDTO(id, connection);

            if (b) {
                writer.write("Student Deleted Successfully");
            }
            else {
                writer.write("Student Not Deleted ");
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
        DataProcess dataProcess = new DataProcess();
        try(var writer = resp.getWriter()){

            var id = req.getParameter("id");
            Jsonb jsonb = JsonbBuilder.create();
            var updatedStudent = jsonb.fromJson(req.getReader(),StudentDTO.class);
            boolean b = dataProcess.updateStudentDTO(updatedStudent, id, connection);
            if (b){
                writer.write("Student Updated");
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }else{
                writer.write("Update Failed");
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public StudentController() {
        super();
    }

}
