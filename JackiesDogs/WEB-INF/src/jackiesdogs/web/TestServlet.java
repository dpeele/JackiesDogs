package jackiesdogs.web;

import javax.servlet.http.*;
import javax.servlet.*;
import javax.servlet.annotation.*;

import java.io.*;

import javax.sql.*;
import java.sql.*;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

@WebServlet("/testServlet")
public class TestServlet extends HttpServlet {

	private ApplicationContext applicationContext;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext());
	}
	
	@Override
	public void doGet (HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		if (applicationContext == null) {
			System.out.println("applicationContext is null");
		}
		DataSource dataSource = (DataSource) applicationContext.getBean("springDataSource");
		try {
			Connection conn = dataSource.getConnection();
			Statement statement = conn.createStatement();
			ResultSet resultSet = statement.executeQuery("select id from customer order by id");
			while (resultSet.next()) {
				System.out.println(resultSet.getString(1));
			}
		} catch (SQLException se) {
			log ("Unable to select customers",se);
		}
	}
}
