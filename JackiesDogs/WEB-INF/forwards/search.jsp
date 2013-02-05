<%@ page import="java.util.*" %>
<%@ page import="jackiesdogs.bean.*" %>
<%response.setHeader("Pragma","no-cache");
  response.setHeader("Cache-Control","no-cache");
  response.setDateHeader("Expires",-1);
%>
<script type="text/javascript">
	$(document).ready(function () {
		search.onload();
		<% 
			List<Order> orders = (List<Order>)request.getAttribute("orders");
			if (orders != null) {//we passed a list of orders to the page
				for (Order order: orders) {		%>
		order.addItem("<%=order.getId()%>","<%=order.getCustomer().getFullName()%>","<%=order.getOrderDateFormatted()%>","<%=order.getDeliveryDateTimeFormatted()%>","<%=order.getTotalCost()%>","<%=order.getStatus()%>");
		<%
				}		
			}
		%>
	});
	
</script>
	<div id="searchPanel">
		<fieldset id="search">
			<legend id="searchLegend" class="ui-widget-header">Search Orders:</legend>	
			<div id="holder">		
			<div id="leftSearch">
			<form id="customerOrderSearchForm">
			<label for="startDate">Start Order Date:</label>
			<input type="text" id="startDate" class="date"/>					
			<br/>
			<label for="endDate">End Order Date:</label>
			<input type="text" id="endDate" class="date"/>					
			<br/>			
			<label for="status">Status (Hold down Ctl to make multiple selections):</label>
			<select id="status">
			<%
				Set<String> keys = Order.STATUS.keySet(); 
				   for (String key: keys) { //iterate through statuses
			%>
				<option value="<%=Order.STATUS.get(key)%>"><%=Order.STATUS.get(key)%></option>			
			<%
			   }
			%>
			</select>
			<br/>
			<label for="customer">Customer (Hold down Ctl to make multiple selections):</label>
			<select id="customer">
			<%
				List<Customer> customers = (List<Customer>)request.getAttribute("customers"); 
				if (customers != null) {
				   for (Customer customer: customers) { //iterate through statuses
			%>
				<option value="<%=customer.getId()%>"><%=customer.getFullName()%></option>			
			<%
			   		}
				}
			%>
			</select>			
			<br/>
			Order type:
			<label for="personal">Personal</label>
			<input type="checkbox" id="personal" value="<%=OrderSearchTerms.PERSONAL%>"/>
			<label for="business">Business</label>
			<input type="checkbox" id="business" value="<%=OrderSearchTerms.BUSINESS%>" class="checked"/>			
			<br/>				
			Delivery type:			
			<label for="delivered">Delivered</label>
			<input type="checkbox" id="delivered" value="<%=OrderSearchTerms.DELIVERED%>" class="checked"/>
			<label for="undelivered">Undelivered</label>
			<input type="checkbox" id="undelivered" value="<%=OrderSearchTerms.UNDELIVERED%>" class="checked"/>			
			<br/>		
			</form>
			<label for="vendor">Generate Vendor Order:</label>
			<select id="vendor">
			<%
				List<String> vendorKeys = new ArrayList<String>(ProductGroup.VENDORS.keySet()); 
				for (int i=0; i<vendorKeys.size(); i++) { //iterate through vendors
			%>
			<option value="<%=i%>"><%=ProductGroup.VENDORS.get(vendorKeys.get(i))%></option>
			<%
				}
			%>
			</select>	
			<br/>
			<input type="button" id="vendorOrderButton"/> 			
			</div>
			<div id="rightSearch">
			<div id="searchTableDiv" class="tableDiv">
			<table id="orderDetails" class="orderTable">
		    		<tr>
		    			<th>ID</th>
	        	   		<th>Customer Name</th>
		    	        <th>Order Date</th>
		    	        <th>Delivery Date</th>
		    	        <th>Total Price</th>
		    	        <th>Order Status</th>		    	        
			        </tr>
			</table>	
			</div>		
			<br/>
			</div>								
			</div>				
		</fieldset>
		<!-- Dialog boxes -->
		<div id="confirmOrderLoadDialog" class="dialog">Are you sure you wish to load this order?</div>				
		<div id="confirmOrderRemoveDialog" class="dialog">Are you sure you wish to remove this order?</div>						
		<div id="confirmVendorOrderCreateDialog" class="dialog">Are you sure you wish to create a vendor order from this list?</div>				
	</div>