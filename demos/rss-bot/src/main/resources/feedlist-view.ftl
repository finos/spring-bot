
  <table><thead><tr>
  
    <td ><b>name</b></td>
  
    <td ><b>description</b></td>
  </tr></thead><tbody>
  <#list entity.workflow_001.feeds as iB>
  <tr>
   <td ><a href="${iB.url!''}">${iB.name!''}</a></td>
   <td >${iB.description!''}</td>
  </tr>
  </#list>
  </tbody></table>
  
  <p><i>${entity.workflow_001.paused?string("Currently Paused", "Feeding Every Hour")}</i></p>
  <p>Created by Rob @ DB Symphony Practice</p>
  <hr />
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