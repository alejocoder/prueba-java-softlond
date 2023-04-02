import controllers.AccountController;
import controllers.TransactionController;
import controllers.UserController;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class Main {
    public static void main( String[] args )
    {
        Server server = new Server(9090);
        server.setHandler(new DefaultHandler());

        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        context.addServlet(UserController.class, "/api/user/*");
        context.addServlet(AccountController.class, "/api/account/*");
        context.addServlet(TransactionController.class, "/api/transaction/*");
        server.setHandler(context);

        try{
            server.start();
            server.join();
        }catch(Exception e){
            e.printStackTrace(System.out);
        }
    }
}