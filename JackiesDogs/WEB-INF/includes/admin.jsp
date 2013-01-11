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
			<form id="AdminForm">
			<label for="pricelist">Upload Omas Price List:</label>
			<input type="file" id="pricelist"/>
			<br/>
			<input type="button" id="uploadPricelistButton"/>
			<br/>
			<label for="invoice">Upload Omas Invoice:</label>
			<input type="file" id="invoice"/>
			<br/>
			<input type="button" id="uploadInvoiceButton"/>
			<br/>
			<label for="url">Scrape Omas Web Site:</label>
			<input type="text" id="url"/>
			<br/>
			<input type="button" id="scrapeSiteButton"/>			
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
		<div id="missingFilename" class="dialog">Please select filename to upload.</div>				
		<div id="missingWebSite" class="dialog">Please enter web site to scrape.</div>		
	</div>