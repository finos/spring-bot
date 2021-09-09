
  <table><thead><tr>
    
    <td ><b>hashTag</b></td>
  
    <td ><b>displayName</b></td>
  
    <td ><b>url</b></td>
  
    <td style="text-align:center;" ><b>active</b></td>
  </tr></thead><tbody>
  <#list entity.workflow_001.webhooks as iB>
  <tr>
   
   <td >
   <#if iB.hashTag??><hash 
    tag="${iB.hashTag.name!''}" /></#if></td>
  
   <td >${iB.displayName!''}</td>
  
   <td >${iB.url!''}</td>
  
   <td style="text-align:center;" >${iB.active?string("Y", "N")}</td>
  </tr>
  </#list>
  </tbody></table>
  
   <i>To begin customizing your webhooks, send a test-payload to the URL listed above</i>
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