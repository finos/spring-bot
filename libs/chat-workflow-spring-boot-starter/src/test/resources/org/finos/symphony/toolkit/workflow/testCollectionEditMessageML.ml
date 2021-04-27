
<#-- starting template -->
<form 
  id="org.finos.symphony.toolkit.workflow.Person">
 <table>
 <tr><td><b>Names:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['names']!''}</span>
  
  <table><thead><tr><td><b>Value</b></td>
   <td style="text-align:center;" ><button name="names.table-delete-rows">Delete</button></td>
   <td style="text-align:center;" ><button name="names.table-add-row">New</button></td>
  </tr></thead><tbody>
  <#list entity.formdata.names as iB>
  <tr><td>${iB!''}</td>
   <td style="text-align:center; width:10%" ><checkbox name="names.${iB?index}.selected" /></td>
   <td style="text-align:center;" ><button name="names[${iB?index}].table-edit-row">Edit</button></td>
  </tr>
  </#list>
  </tbody></table></td></tr>
 <tr><td><b>Addresses:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['addresses']!''}</span>
  
  <table><thead><tr>
  
    <td style="text-align:center; width:10%" ><b>City</b></td>
   <td style="text-align:center;" ><button name="addresses.table-delete-rows">Delete</button></td>
   <td style="text-align:center;" ><button name="addresses.table-add-row">New</button></td>
  </tr></thead><tbody>
  <#list entity.formdata.addresses as iB>
  <tr>
  
   <td >${iB.city!''}</td>
   <td style="text-align:center; width:10%" ><checkbox name="addresses.${iB?index}.selected" /></td>
   <td style="text-align:center;" ><button name="addresses[${iB?index}].table-edit-row">Edit</button></td>
  </tr>
  </#list>
  </tbody></table></td></tr>
 </table>
  <p><#list entity.buttons.contents as button>
    <button 
         name="${button.name}"
         type="${button.buttonType?lower_case}">
      ${button.text}
    </button>
  </#list></p>
</form>
<#-- ending template -->
