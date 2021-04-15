
<#-- starting template -->
 <table>
 <tr><td><b>Isin:</b></td><td>${entity.workflow_001.isin!''}</td></tr>
 <tr><td><b>Bid Axed:</b></td><td>${entity.workflow_001.bidAxed?string("Y", "N")}</td></tr>
 <tr><td><b>Ask Axed:</b></td><td>${entity.workflow_001.askAxed?string("Y", "N")}</td></tr>
 <tr><td><b>Creator:</b></td><td>${entity.workflow_001.creator!''}</td></tr>
 <tr><td><b>Bid Qty:</b></td><td>${entity.workflow_001.bidQty!''}</td></tr>
 <tr><td><b>Ask Qty:</b></td><td>${entity.workflow_001.askQty!''}</td></tr>
 </table>
<form 
  id="just-buttons-form">
  <p><#list entity.buttons.contents as button>
    <button 
         name="${button.name}"
         type="${button.buttonType?lower_case}">
      ${button.text}
    </button>
  </#list></p>
</form>
<#-- ending template -->
