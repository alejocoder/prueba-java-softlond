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

public class TransactionController extends HttpServlet {
    private IService<Transaction> transactionService
            = new TransactionService(new TransactionRepository(IRepository.FILE_DB), new AccountRepository(IRepository.FILE_DB));

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id;
        List<Transaction> transactions;
        resp.setContentType("application/json");
        switch (req.getPathInfo()) {
            case "/all":
                transactions = transactionService.getAll();
                resp.getWriter().write(this.toJSON(transactions));
                break;
            case "/find":
                id = Integer.parseInt(req.getParameter("id"));
                Transaction account = transactionService.getOne(id);
                if(account == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                } else {
                    resp.getWriter().write(this.toJSON(account));
                }
                break;
            case "/account":
                id = Integer.parseInt(req.getParameter("id"));
                transactions = ((TransactionService) transactionService).getByAccount(id);
                resp.getWriter().write(this.toJSON(transactions));
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
                Transaction transaction = this.mapTransaction(req.getInputStream());
                almacenado = transactionService.save(transaction);
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
                Transaction transaction = this.mapTransaction(req.getInputStream());
                modificado = transactionService.update(transaction, id);
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
        int id;
        boolean eliminado = false;
        switch (req.getPathInfo()) {
            case "/delete":
                id = Integer.parseInt(req.getParameter("id"));
                eliminado = transactionService.remove(id);
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

    public Transaction mapTransaction(ServletInputStream rawData) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(rawData, new TypeReference<Transaction>(){});
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
