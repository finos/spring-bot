
<div style='padding-top:1px;padding-left:5px;'>
  <p><span class="tempo-text-color--secondary"> by <mention uid="${entity.workflow_001.poller.id}" />, poll id <hash tag="${entity.workflow_001.id.name!''}" /></span></p>
  <br />
</div>  

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