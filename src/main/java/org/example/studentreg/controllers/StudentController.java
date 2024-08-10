package org.example.studentreg.controllers;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.studentreg.dto.StudentDTO;
import org.example.studentreg.persistence.StudentDataProcess;
import org.example.studentreg.util.UtilProcess;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
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


    @Override
    public void init() {
        try {

            var ctx = new InitialContext();
            DataSource pool = (DataSource) ctx.lookup("java:comp/env/jdbc/studentregdb");
            this.connection = pool.getConnection();

        }catch (SQLException | NamingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Todo : get student

            String id = req.getParameter("id");
            StudentDataProcess studentDataProcess = new StudentDataProcess();

            try(var writer = resp.getWriter())
            {
                var studentDTO = studentDataProcess.getStudentDTO(id, connection);
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
            StudentDTO sdto = jsonb.fromJson(req.getReader(), StudentDTO.class); //Object Binding - Binding a json to a java object
            StudentDataProcess studentDataProcess = new StudentDataProcess();
            sdto.setId(UtilProcess.generateId());

            writer.write(studentDataProcess.saveStudentDTO(sdto, connection));
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
        StudentDataProcess studentDataProcess = new StudentDataProcess();
        try(var writer = resp.getWriter()) {

            var id = req.getParameter("id");
            boolean b = studentDataProcess.deleteStudentDTO(id, connection);

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
        StudentDataProcess studentDataProcess = new StudentDataProcess();
        try(var writer = resp.getWriter()){

            var id = req.getParameter("id");
            Jsonb jsonb = JsonbBuilder.create();
            var updatedStudent = jsonb.fromJson(req.getReader(),StudentDTO.class);
            boolean b = studentDataProcess.updateStudentDTO(updatedStudent, id, connection);
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
