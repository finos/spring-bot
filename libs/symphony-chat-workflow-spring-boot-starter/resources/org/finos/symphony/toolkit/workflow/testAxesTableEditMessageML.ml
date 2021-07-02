
<#-- starting template -->
<form 
  id="org.finos.symphony.toolkit.workflow.fixture.TestObjects">
 <table>
 <tr><td><b>Items:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['items']!''}</span>
  
  <table><thead><tr>
  
    <td><b>Isin</b></td>
  
    <td style="text-align:center;" ><b>Bid Axed</b></td>
  
    <td style="text-align:center;" ><b>Ask Axed</b></td>
  
    <td><b>Creator</b></td>
  
    <td style="text-align: right;"><b>Bid Qty</b></td>
  
    <td style="text-align: right;"><b>Ask Qty</b></td>
   <td style="text-align:center;" ><button name="items.table-delete-rows">Delete</button></td>
   <td style="text-align:center;" ><button name="items.table-add-row">New</button></td>
  </tr></thead><tbody>
  <#list entity.formdata.items as iB>
  <tr>
  
   <td >${iB.isin!''}</td>
  
   <td style="text-align:center;" >${iB.bidAxed?string("Y", "N")}</td>
  
   <td style="text-align:center;" >${iB.askAxed?string("Y", "N")}</td>
  
   <td >${iB.creator!''}</td>
  
   <td style="text-align: right;">${iB.bidQty!''}</td>
  
   <td style="text-align: right;">${iB.askQty!''}</td>
   <td style="text-align:center; width:10%" ><checkbox name="items.${iB?index}.selected" /></td>
   <td style="text-align:center;" ><button name="items[${iB?index}].table-edit-row">Edit</button></td>
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
