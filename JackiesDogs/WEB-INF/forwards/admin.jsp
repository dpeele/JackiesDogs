<%@ page contentType="text/html;charset=UTF-8" %>
<%response.setHeader("Pragma","no-cache");
  response.setHeader("Cache-Control","no-cache");
  response.setDateHeader("Expires",-1);
%>
<script type="text/javascript">
	$(document).ready(function () {
		admin.onload();
	});
	
</script>
	<div id="adminPanel">
		<fieldset id="admin">
			<legend id="adminLegend" class="ui-widget-header">Administrative Functions:</legend>	
			<div id="holder">		
			<div id="leftAdmin">
			<form id="adminForm">
			<label for="pricelist">Upload Omas Price List:</label>
			<br/>
			<input type="file" id="pricelistInput"/>
			<br/>
			<input type="submit" id="pricelist"/>
			<br/>
			<label for="invoice">Upload Omas Invoice:</label>
			<br/>
			<input type="file" id="invoiceInput"/>
			<br/>
			<input type="submit" id="invoice"/>
			<br/>
			<label for="url">Scrape Omas Web Site:</label>
			<br/>
			<input type="text" id="scrapeInput" value = "<%=request.getAttribute("defaultOmaUrl")%>"/>
			<br/>
			<input type="submit" id="scrape"/>		
			<input type="text" id="command"/>
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