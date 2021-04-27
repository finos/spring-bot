
<#-- starting template -->
 <table>
 <tr><td><b>Names:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['names']!''}</span>
  
  <table><thead><tr><td><b>Value</b></td>
  </tr></thead><tbody>
  <#list entity.workflow_001.names as iB>
  <tr><td>${iB!''}</td>
  </tr>
  </#list>
  </tbody></table></td></tr>
 <tr><td><b>Addresses:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['addresses']!''}</span>
  
  <table><thead><tr>
  
    <td style="text-align:center; width:10%" ><b>City</b></td>
  </tr></thead><tbody>
  <#list entity.workflow_001.addresses as iB>
  <tr>
  
   <td >${iB.city!''}</td>
  </tr>
  </#list>
  </tbody></table></td></tr>
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
