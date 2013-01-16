<%@ page contentType="text/html;charset=UTF-8" %>
<%response.setHeader("Pragma","no-cache");
  response.setHeader("Cache-Control","no-cache");
  response.setDateHeader("Expires",-1);
%>
<script type="text/javascript">
	$(document).ready(function () {
		search.onload();
	});
	
</script>
	<div id="adminPanel">
		<fieldset id="admin">
			<div id="holder">		
			<div id="leftAdmin">
			<legend id="adminLegend" class="ui-widget-header">Administration Functions:</legend>	
			<form id="adminForm">
			<label for="pricelist">Upload Omas Price List:</label>
			<input type="file" id="pricelistInput"/>
			<br/>
			<input type="submit" id="uploadProducts"/>
			<br/>
			<label for="invoice">Upload Omas Invoice:</label>
			<input type="file" id="invoiceInput"/>
			<br/>
			<input type="submit" id="uploadInvoice"/>
			<br/>
			<label for="url">Scrape Omas Web Site:</label>
			<input type="text" id="scrapeInput"/>
			<br/>
			<input type="submit" id="scrape"/>			
			</form>
			</div>
			<div id="rightAdmin">
			<div id="adminTableDiv" class="tableDiv">
			</div>		
			<br/>
			</div>								
			</div>				
		</fieldset>
		<!-- Dialog boxes -->
		<div id="pricelistMissing" class="dialog">Please select price list to upload.</div>				
		<div id="invoiceMissing" class="dialog">Please select invoice to upload.</div>				
		<div id="scrapeMissing" class="dialog">Please enter web site to scrape.</div>		
	</div>