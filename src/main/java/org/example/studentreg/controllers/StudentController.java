package org.example.studentreg.controllers;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.studentreg.dto.StudentDTO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebServlet(urlPatterns = "/student")
public class StudentController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //Todo : get student details
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //Todo : save student
        if(!req.getContentType().toLowerCase().startsWith("application/json")||req.getContentType() == null){
            resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        }
        //Single Bind Object JSON
        /*String id = UUID.randomUUID().toString();
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

        Jsonb jsonb = JsonbBuilder.create();
        List<StudentDTO> students = jsonb.fromJson(req.getReader(),new ArrayList<StudentDTO>(){}.getClass().getGenericSuperclass());
        students.forEach(System.out::println);


    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //Todo : delete student
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //Todo : update student
    }

    public StudentController() {
        super();
    }

}
