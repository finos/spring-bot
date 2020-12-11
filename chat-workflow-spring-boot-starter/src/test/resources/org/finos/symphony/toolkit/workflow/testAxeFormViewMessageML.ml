
<#-- starting template -->
<table><tr><td><b>isin:</b></td><td>${entity.workflow_001.isin!''}</td></tr><tr><td><b>bidAxed:</b></td><td>${entity.workflow_001.bidAxed?string("Y", "N")}</td></tr><tr><td><b>askAxed:</b></td><td>${entity.workflow_001.askAxed?string("Y", "N")}</td></tr><tr><td><b>creator:</b></td><td>${entity.workflow_001.creator!''}</td></tr><tr><td><b>bidQty:</b></td><td>${entity.workflow_001.bidQty!''}</td></tr><tr><td><b>askQty:</b></td><td>${entity.workflow_001.askQty!''}</td></tr>
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
