<%@ page import="java.util.*" %>
<%@ page import="jackiesdogs.bean.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%response.setHeader("Pragma","no-cache");
  response.setHeader("Cache-Control","no-cache");
  response.setDateHeader("Expires",-1);
%>
<script type="text/javascript">
	$(document).ready(function () {
		order.onload();
		<% 
			VendorOrder order = (VendorOrder)request.getAttribute("order");
			if (order != null) {//we passed an existing order to the page
				Date deliveryDateTime = order.getDeliveryDate();
				String deliveryDate = new SimpleDateFormat("MM/d/yyyy", Locale.ENGLISH).format(deliveryDateTime); //convert date/time to date string
		%>
		order.setValues("<%=order.getId()%>","<%=deliveryDate%>","<%=order.getDiscount()%>","<%=order.getCredit()%>","<%=order.getDeliveryFee()%>","<%=order.getTollExpense()%>","<%=order.getTotalCost()%>","<%=order.getTotalWeight()%>","<%=order.getStatus()%>","<%=order.getMileage()%>");
		<%
				List<VendorInventory> orderItems = order.getVendorInventoryItems();
				VendorInventory orderItem;
				Product product;
				for (int i=0; i<orderItems.size(); i++) {
					orderItem = orderItems.get(i);				
					product = orderItem.getProduct();
		%>
		order.addItem("<%=orderItem.getId()%>","<%=orderItem.getQuantity()%>","<%=orderItem.getTotalWeight()%>","<%=product.getId()%>","<%=product.getProductName() + "(" + product.getBillBy() + ")"%>","<%=product.getPrice()%>","<%=product.getBillBy()%>","<%=product.getEstimatedWeight()%>","<%=product.getDescription()%>");
		<%
				}
			}
		%>
	});
	
</script>
	<div id="vendorOrderPanel">
		<fieldset id="details">
			<legend id="orderLegend" class="ui-widget-header">Enter Information:</legend>			
			<div id="holderOrder" class="holder">		
			<div id="leftOrder">
			<label for="vendor">Vendor:</label>
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
			<label for="quantity">Item Quantity:</label> 
			<input type="text" id="quantity" class="onlyNumbers item"/>
			<label for="quantityAvailable">Quantity Available:</label> 			
			<input type="text" id="quantityAvailable" class="readOnly item"/>
			<br/>										
			<label for="item">Select Item:</label> 
			<input type="text" id="item" class="item"/>		
			<label for="estimatedWeight">Estimated Weight:</label>	
			<input type="text" id="estimatedWeight" class="item"/>
			<label for="price">Price:</label>				
			<input type="text" id="price" class="item"/>
			<input type="Button" id="addButton"/>	
			<hr/>				
			<form id="orderForm">		
			<label for="totalCost">Food Cost:</label>		
			<input type="text" id="totalCost" class="total readOnly cost"/>	
			<label for="finalCost">Total Cost:</label>		
			<input type="text" id="finalCost" class="total readOnly"/>
			<label for="totalWeight">Total Weight:</label>		
			<input type="text" id="totalWeight" class="readOnly"/>lbs			
			<input type="text" id="custId" class="required hidden"/>	
			<input type="text" id="orderId" class="hidden"/>				
			<input type="text" id="orderInfo" class="hidden"/>	
			<label for="discount">Discount:</label>
			<input type="text" id="discount" class="onlyNumbers cost"/>%
			<label for="credit">Credit: $</label>
			<input type="text" id="credit" class="onlyDecimalNumbers cost"/>	
			<label for="mileage">Mileage:</label>
			<input type="text" id="mileage" class="onlyNumbers"/>			
			<br/>			
			<label for="deliveryDate">Delivery Date:</label>
			<input type="text" id="deliveryDate" class="readOnly"/>		
			<br/>			
			<input type="button" id="enterDeliveryZipButton"/>
			<label for="deliveryFee">Delivery Fee: $</label>
			<input type="text" id="deliveryFee" class="onlyDecimalNumbers cost"/>
			<label for="tollExpense">Toll Expense: $</label>
			<input type="text" id="tollExpense" class="onlyDecimalNumbers cost"/>
			<label for="changeDue">Change Due: $</label>
			<input type="text" id="changeDue" class="onlyDecimalNumbers cost"/>					
			<br/>					
			<label for="status">Status:</label>
			<select id="status">
				<option value="">Select</option>
			<%
				Set<String> keys = Order.STATUS.keySet(); 
				   for (String key: keys) { //iterate through statuses
			%>
				<option value="<%=Order.STATUS.get(key)%>"><%=Order.STATUS.get(key)%></option>			
			<%
			   }
			%>
			</select>
			<label for="delivered">Delivered?</label>
			<input type="checkbox" id="delivered"/>			
			<label for="personal">Personal?</label>
			<input type="checkbox" id="personal"/>	
			<input type="button" id="submitButton"/>			
			<input type="button" id="cancelButton"/>												
			<br/>								
			</form>					
			</div>
			<div id="rightOrder">
			<div id="orderTableDiv" class="tableDiv">
			<table id="orderItems" class="orderTable">
		    		<tr>
		    			<th>ID</th>
	        	   		<th>Item</th>
		    	        <th>Quantity</th>
		    	        <th>Unit Price</th>
		    	        <th>Total Price</th>
			        </tr>
			</table>	
			</div>		
			<br/>
			</div>								
			</div>				
		</fieldset>
		<!-- Dialog boxes -->
		<div id="incompletePoundQuantityDialog" class="dialog">Please fill in quantity and weight.</div>		
		<div id="incompleteDeliveryDialog" class="dialog">Please fill in zip.</div>				
		<div id="orderSubmittedDialog" class="dialog"></div>
		<div id="orderSubmissionFailedDialog" class="dialog">The order submission failed, please contact the admin.</div>
		<div id="orderCancelledDialog" class="dialog"></div>
		<div id="orderCancellationFailedDialog" class="dialog">The order cancellation failed, please contact the admin.</div>		
		<div id="confirmCancelDialog" class="dialog">Are you sure you want to cancel order?</div>			
		<div id="quantityOverrideDialog" class="dialog">Quantity ordered is more than quantity in stock.</div>	
		<div id="editPoundQuantityDialog" class="dialog">
			<form id="exactQuantityForm">
			<label for="exactQuantity">Quantity:</label> 
			<input type="text" id="exactQuantity" class="required onlyNumbers"/>
			<br/>	
			<label for="exactWeight">Exact Weight:</label> 
			<input type="text" id="exactWeight" class="required onlyDecimalNumbers"/>
			</form>
		</div>	
		<div id="enterDeliveryZipDialog" class="dialog">
			<form id="enterDeliveryZipForm">
			<label for="deliveryZip">Delivery Destination Zip Code:</label> 
			<input type="text" id="deliveryZip" class="required onlyNumbers"/>
			</form>
		</div>			
	</div>