/*
 * Generated by the Jasper component of Apache Tomcat
 * Version: Apache Tomcat/7.0.29
 * Generated at: 2013-02-07 15:24:35 UTC
 * Note: The last modified time of this file was set to
 *       the last modified time of the source file after
 *       generation to assist with modification tracking.
 */
package org.apache.jsp.WEB_002dINF.forwards;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.util.*;
import jackiesdogs.bean.*;

public final class search_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final javax.servlet.jsp.JspFactory _jspxFactory =
          javax.servlet.jsp.JspFactory.getDefaultFactory();

  private static java.util.Map<java.lang.String,java.lang.Long> _jspx_dependants;

  private javax.el.ExpressionFactory _el_expressionfactory;
  private org.apache.tomcat.InstanceManager _jsp_instancemanager;

  public java.util.Map<java.lang.String,java.lang.Long> getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
    _jsp_instancemanager = org.apache.jasper.runtime.InstanceManagerFactory.getInstanceManager(getServletConfig());
  }

  public void _jspDestroy() {
  }

  public void _jspService(final javax.servlet.http.HttpServletRequest request, final javax.servlet.http.HttpServletResponse response)
        throws java.io.IOException, javax.servlet.ServletException {

    final javax.servlet.jsp.PageContext pageContext;
    javax.servlet.http.HttpSession session = null;
    final javax.servlet.ServletContext application;
    final javax.servlet.ServletConfig config;
    javax.servlet.jsp.JspWriter out = null;
    final java.lang.Object page = this;
    javax.servlet.jsp.JspWriter _jspx_out = null;
    javax.servlet.jsp.PageContext _jspx_page_context = null;


    try {
      response.setContentType("text/html");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write('\n');
      out.write('\n');
response.setHeader("Pragma","no-cache");
  response.setHeader("Cache-Control","no-cache");
  response.setDateHeader("Expires",-1);

      out.write("\n");
      out.write("<script type=\"text/javascript\">\n");
      out.write("\t$(document).ready(function () {\n");
      out.write("\t\tsearch.onload();\n");
      out.write("\t\t");
 
			List<Order> orders = (List<Order>)request.getAttribute("orders");
			if (orders != null) {//we passed a list of orders to the page
				for (Order order: orders) {		
      out.write("\n");
      out.write("\t\torder.addItem(\"");
      out.print(order.getId());
      out.write('"');
      out.write(',');
      out.write('"');
      out.print(order.getCustomer().getFullName());
      out.write('"');
      out.write(',');
      out.write('"');
      out.print(order.getOrderDateFormatted());
      out.write('"');
      out.write(',');
      out.write('"');
      out.print(order.getDeliveryDateTimeFormatted());
      out.write('"');
      out.write(',');
      out.write('"');
      out.print(order.getTotalCost());
      out.write('"');
      out.write(',');
      out.write('"');
      out.print(order.getStatus());
      out.write("\");\n");
      out.write("\t\t");

				}		
			}
		
      out.write("\n");
      out.write("\t});\n");
      out.write("\t\n");
      out.write("</script>\n");
      out.write("\t<div id=\"searchPanel\">\n");
      out.write("\t\t<fieldset id=\"search\">\n");
      out.write("\t\t\t<legend id=\"searchLegend\" class=\"ui-widget-header\">Search Orders:</legend>\t\n");
      out.write("\t\t\t<div id=\"holder\">\t\t\n");
      out.write("\t\t\t<div id=\"leftSearch\">\n");
      out.write("\t\t\t<form id=\"customerOrderSearchForm\">\n");
      out.write("\t\t\t<label for=\"startDate\">Start Order Date:</label>\n");
      out.write("\t\t\t<input type=\"text\" id=\"startDate\" class=\"date\"/>\t\t\t\t\t\n");
      out.write("\t\t\t<br/>\n");
      out.write("\t\t\t<label for=\"endDate\">End Order Date:</label>\n");
      out.write("\t\t\t<input type=\"text\" id=\"endDate\" class=\"date\"/>\t\t\t\t\t\n");
      out.write("\t\t\t<br/>\t\t\t\n");
      out.write("\t\t\t<label for=\"status\">Status (Hold down Ctl to make multiple selections):</label>\n");
      out.write("\t\t\t<select id=\"status\">\n");
      out.write("\t\t\t");

				Set<String> keys = Order.STATUS.keySet(); 
				   for (String key: keys) { //iterate through statuses
			
      out.write("\n");
      out.write("\t\t\t\t<option value=\"");
      out.print(Order.STATUS.get(key));
      out.write('"');
      out.write('>');
      out.print(Order.STATUS.get(key));
      out.write("</option>\t\t\t\n");
      out.write("\t\t\t");

			   }
			
      out.write("\n");
      out.write("\t\t\t</select>\n");
      out.write("\t\t\t<br/>\n");
      out.write("\t\t\t<label for=\"customer\">Customer (Hold down Ctl to make multiple selections):</label>\n");
      out.write("\t\t\t<select id=\"customer\">\n");
      out.write("\t\t\t");

				List<Customer> customers = (List<Customer>)request.getAttribute("customers"); 
				if (customers != null) {
				   for (Customer customer: customers) { //iterate through statuses
			
      out.write("\n");
      out.write("\t\t\t\t<option value=\"");
      out.print(customer.getId());
      out.write('"');
      out.write('>');
      out.print(customer.getFullName());
      out.write("</option>\t\t\t\n");
      out.write("\t\t\t");

			   		}
				}
			
      out.write("\n");
      out.write("\t\t\t</select>\t\t\t\n");
      out.write("\t\t\t<br/>\n");
      out.write("\t\t\tOrder type:\n");
      out.write("\t\t\t<label for=\"personal\">Personal</label>\n");
      out.write("\t\t\t<input type=\"checkbox\" id=\"personal\" value=\"");
      out.print(OrderSearchTerms.PERSONAL);
      out.write("\"/>\n");
      out.write("\t\t\t<label for=\"business\">Business</label>\n");
      out.write("\t\t\t<input type=\"checkbox\" id=\"business\" value=\"");
      out.print(OrderSearchTerms.BUSINESS);
      out.write("\" class=\"checked\"/>\t\t\t\n");
      out.write("\t\t\t<br/>\t\t\t\t\n");
      out.write("\t\t\tDelivery type:\t\t\t\n");
      out.write("\t\t\t<label for=\"delivered\">Delivered</label>\n");
      out.write("\t\t\t<input type=\"checkbox\" id=\"delivered\" value=\"");
      out.print(OrderSearchTerms.DELIVERED);
      out.write("\" class=\"checked\"/>\n");
      out.write("\t\t\t<label for=\"undelivered\">Undelivered</label>\n");
      out.write("\t\t\t<input type=\"checkbox\" id=\"undelivered\" value=\"");
      out.print(OrderSearchTerms.UNDELIVERED);
      out.write("\" class=\"checked\"/>\t\t\t\n");
      out.write("\t\t\t<br/>\t\t\n");
      out.write("\t\t\t</form>\n");
      out.write("\t\t\t<label for=\"vendor\">Generate Vendor Order:</label>\n");
      out.write("\t\t\t<select id=\"vendor\">\n");
      out.write("\t\t\t");

				List<String> vendorKeys = new ArrayList<String>(ProductGroup.VENDORS.keySet()); 
				for (int i=0; i<vendorKeys.size(); i++) { //iterate through vendors
			
      out.write("\n");
      out.write("\t\t\t<option value=\"");
      out.print(i);
      out.write('"');
      out.write('>');
      out.print(ProductGroup.VENDORS.get(vendorKeys.get(i)));
      out.write("</option>\n");
      out.write("\t\t\t");

				}
			
      out.write("\n");
      out.write("\t\t\t</select>\t\n");
      out.write("\t\t\t<br/>\n");
      out.write("\t\t\t<input type=\"button\" id=\"vendorOrderButton\"/> \t\t\t\n");
      out.write("\t\t\t</div>\n");
      out.write("\t\t\t<div id=\"rightSearch\">\n");
      out.write("\t\t\t<div id=\"searchTableDiv\" class=\"tableDiv\">\n");
      out.write("\t\t\t<table id=\"orderDetails\" class=\"orderTable\">\n");
      out.write("\t\t    \t\t<tr>\n");
      out.write("\t\t    \t\t\t<th>ID</th>\n");
      out.write("\t        \t   \t\t<th>Customer Name</th>\n");
      out.write("\t\t    \t        <th>Order Date</th>\n");
      out.write("\t\t    \t        <th>Delivery Date</th>\n");
      out.write("\t\t    \t        <th>Total Price</th>\n");
      out.write("\t\t    \t        <th>Order Status</th>\t\t    \t        \n");
      out.write("\t\t\t        </tr>\n");
      out.write("\t\t\t</table>\t\n");
      out.write("\t\t\t</div>\t\t\n");
      out.write("\t\t\t<br/>\n");
      out.write("\t\t\t</div>\t\t\t\t\t\t\t\t\n");
      out.write("\t\t\t</div>\t\t\t\t\n");
      out.write("\t\t</fieldset>\n");
      out.write("\t\t<!-- Dialog boxes -->\n");
      out.write("\t\t<div id=\"confirmOrderLoadDialog\" class=\"dialog\">Are you sure you wish to load this order?</div>\t\t\t\t\n");
      out.write("\t\t<div id=\"confirmOrderRemoveDialog\" class=\"dialog\">Are you sure you wish to remove this order?</div>\t\t\t\t\t\t\n");
      out.write("\t\t<div id=\"confirmVendorOrderCreateDialog\" class=\"dialog\">Are you sure you wish to create a vendor order from this list?</div>\t\t\t\t\n");
      out.write("\t</div>");
    } catch (java.lang.Throwable t) {
      if (!(t instanceof javax.servlet.jsp.SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try { out.clearBuffer(); } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
