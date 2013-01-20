<%@ page import="java.util.*" %>
<%@ page import="jackiesdogs.bean.*" %>
<%response.setHeader("Pragma","no-cache");
  response.setHeader("Cache-Control","no-cache");
  response.setDateHeader("Expires",-1);
%>
<script type="text/javascript">
	$(document).ready(function () {
		vendorVendorSearch.onload();
		<% 
			List<VendorOrder> vendorOrders = (List<VendorOrder>)request.getAttribute("orders");
			if (vendorOrders != null) {//we passed a list of vendorOrders to the page
				for (VendorOrder vendorOrder: vendorOrders) {		%>
		vendorOrder.addItem("<%=vendorOrder.getId()%>","<%=vendorOrder.getCustomer().getFullName()%>","<%=vendorOrder.getVendorOrderDateFormatted()%>","<%=vendorOrder.getDeliveryDateTimeFormatted()%>","<%=vendorOrder.getTotalCost()%>","<%=vendorOrder.getStatus()%>");
		<%
				}		
			}
		%>
	});
	
</script>
	<div id="vendorSearchPanel">
		<fieldset id="vendorSearch">
			<div id="holder">		
			<div id="leftVendorSearch">
			<legend id="vendorSearchLegend" class="ui-widget-header">Search Vendor Orders:</legend>	
			<form id="VendorOrderSearchForm">
			<label for="vendorStartDate">Start Date:</label>
			<input type="text" id="vendorStartDate" class="date"/>					
			<br/>
			<label for="vendorEndDate">End Date:</label>
			<input type="text" id="vendorEndDate" class="date"/>					
			<br/>			
			<label for="vendorStatus">Status (Hold down Ctl to make multiple selections):</label>
			<select id="vendorStatus">
			<%
				Set<String> statusKeys = VendorOrder.STATUS.keySet(); 
				   for (String key: statusKeys) { //iterate through statuses
			%>
				<option value="<%=VendorOrder.STATUS.get(key)%>"><%=VendorOrder.STATUS.get(key)%></option>			
			<%
			   }
			%>
			<br/>
			<label for="vendor">Vendor (Hold down Ctl to make multiple selections):</label>
			<select id="vendor">
			<%
				Set<String> vendorKeys = ProductGroup.VENDORS.keySet(); 
				   for (String key: vendorKeys) { //iterate through vendors
			%>
				<option value="<%=ProductGroup.VENDORS.get(key)%>"><%=ProductGroup.VENDORS.get(key)%></option>			
			<%
			   }
			%>
			<br/>			
			</form>
			</div>
			<div id="rightVendorSearch">
			<div id="vendorVendorSearchTableDiv" class="tableDiv">
			<table id="vendorOrderDetails" class="orderTable">
		    		<tr>
		    			<th>ID</th>
	        	   		<th>Customer Name</th>
		    	        <th>VendorOrder Date</th>
		    	        <th>Delivery Date</th>
		    	        <th>Total Price</th>
		    	        <th>VendorOrder Status</th>		    	        
			        </tr>
			</table>	
			</div>		
			<br/>
			</div>								
			</div>				
		</fieldset>
		<!-- Dialog boxes -->
		<div id="confirmVendorOrderLoadDialog" class="dialog">Are you sure you wish to load this vendor order?</div>				
	</div>