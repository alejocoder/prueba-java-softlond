package controllers;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import repositories.*;
import domain.*;
import services.*;


public class UserController extends HttpServlet {
    private IService<User> userService
            = new UserService(new UserRepository(IRepository.FILE_DB));

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int cedula;
        resp.setContentType("application/json");
        switch (req.getPathInfo()) {
            case "/all":
                List<User> cuentasAhorro = userService.getAll();
                resp.getWriter().write(this.toJSON(cuentasAhorro));
                break;
            case "/find":
                cedula = Integer.parseInt(req.getParameter("cedula"));
                User user = userService.getOne(cedula);
                if(user == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                } else {
                    resp.getWriter().write(this.toJSON(user));
                }
                break;
            default:
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String content = req.getContentType();
        boolean almacenado = false;
        if(content != "application/json" || content == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        switch (req.getPathInfo()) {
            case "/save":
                User user = this.mapUser(req.getInputStream());
                almacenado = userService.save(user);
                if(almacenado){
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                }else{
                    resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                }
                break;
            default:
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                break;
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int cedula;
        boolean modificado = false;
        String content = req.getContentType();
        if(content != "application/json" || content == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        switch (req.getPathInfo()) {
            case "/update":
                cedula = Integer.parseInt(req.getParameter("cedula"));
                User user = this.mapUser(req.getInputStream());
                modificado = userService.update(user, cedula);
                break;

            default:
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                break;
        }
        if(modificado == false) {
            resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int cedula;
        boolean eliminado = false;
        switch (req.getPathInfo()) {
            case "/delete":
                cedula = Integer.parseInt(req.getParameter("cedula"));
                eliminado = userService.remove(cedula);
                resp.setStatus(HttpServletResponse.SC_OK);
                break;

            default:
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                break;
        }
        if(eliminado == false) {
            resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }
    }

    public User mapUser(ServletInputStream rawData) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(rawData, new TypeReference<User>(){});
        } catch (IOException e) {
            e.printStackTrace(System.out);
            return null;
        }
    }

    public String toJSON(Object object) {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            return ow.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace(System.out);
            return null;
        }
    }
}
