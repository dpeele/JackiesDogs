package jackiesdogs.web.context;

import jackiesdogs.web.AdminServlet;

import java.sql.*;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.log4j.Logger;

/**
 * Application Lifecycle Listener implementation class ServletContextDatabaseCloseListener
 *
 */
@WebListener
public class ServletContextDatabaseCloseListener implements ServletContextListener {

	private final Logger log = Logger.getLogger(AdminServlet.class);
	
    /**
     * Default constructor. 
     */
    public ServletContextDatabaseCloseListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0) {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                log.info(String.format("Deregistering jdbc driver: %s", driver));
            } catch (SQLException e) {
                log.error(String.format("Error deregistering driver %s", driver), e);
            }
        }
    }
	
}
