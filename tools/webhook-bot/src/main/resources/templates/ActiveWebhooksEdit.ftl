<form 
  id="org.finos.symphony.webhookbot.domain.ActiveWebHooks">
  
  <table><thead><tr>
  
    <td ><b>hashTag</b></td>
  
    <td ><b>displayName</b></td>
  
    <td ><b>url</b></td>
  
    <td style="text-align:center;" ><b>active</b></td>
   <td style="text-align:center;" ><button name="webhooks.table-delete-rows">Delete</button></td>
  </tr></thead><tbody>
  <#list entity.formdata.webhooks as iB>
  <tr>
  
   <td >
   <#if iB.hashTag??><hash 
    tag="${iB.hashTag.name!''}" /></#if></td>
  
   <td >${iB.displayName!''}</td>
  
   <td >${iB.url!''}</td>
 
  
   <td style="text-align:center;" >${iB.active?string("Y", "N")}</td>
   <td style="text-align:center;" ><checkbox name="webhooks.${iB?index}.selected" /></td>
  </tr>
  </#list>
  </tbody></table>
  <p><#list entity.buttons.contents as button>
    <button 
         name="${button.name}"
         type="${button.buttonType?lower_case}">
      ${button.text}
    </button>
  </#list></p>
</form>