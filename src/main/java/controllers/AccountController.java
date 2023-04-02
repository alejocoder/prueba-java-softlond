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

public class AccountController extends HttpServlet {
    private IService<Account> accountService
            = new AccountService(new AccountRepository(IRepository.FILE_DB));

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id;
        List<Account> accounts;
        resp.setContentType("application/json");
        switch (req.getPathInfo()) {
            case "/all":
                accounts = accountService.getAll();
                resp.getWriter().write(this.toJSON(accounts));
                break;
            case "/find":
                id = Integer.parseInt(req.getParameter("id"));
                Account account = accountService.getOne(id);
                if(account == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                } else {
                    resp.getWriter().write(this.toJSON(account));
                }
                break;
            case "/user":
                id = Integer.parseInt(req.getParameter("id"));
                accounts = ((AccountService) accountService).getByUser(id);
                resp.getWriter().write(this.toJSON(accounts));
                break;
            default:
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean almacenado = false;
        String content = req.getContentType();
        if(content != "application/json" || content == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        switch (req.getPathInfo()) {
            case "/save":
                Account account = this.mapAccount(req.getInputStream());
                almacenado = accountService.save(account);
                if(almacenado){
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                }
                break;
            default:
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                break;
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id;
        boolean eliminado = false;
        switch (req.getPathInfo()) {
            case "/delete":
                id = Integer.parseInt(req.getParameter("id"));
                eliminado = accountService.remove(id);
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

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id;
        boolean modificado = false;
        String content = req.getContentType();
        if(content != "application/json" || content == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        switch (req.getPathInfo()) {
            case "/update":
                id = Integer.parseInt(req.getParameter("id"));
                Account account = this.mapAccount(req.getInputStream());
                modificado = accountService.update(account, id);
                break;

            default:
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                break;
        }
        if(modificado == false) {
            resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }
    }

    public Account mapAccount(ServletInputStream rawData) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(rawData, new TypeReference<Account>(){});
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

