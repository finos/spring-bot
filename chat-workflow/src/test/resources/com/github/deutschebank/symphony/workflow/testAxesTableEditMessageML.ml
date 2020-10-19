
<#-- starting template -->
<form 
 id="com.github.deutschebank.symphony.workflow.fixture.TestObjects">
 <span class="tempo-text-color--red">${entity.errors.contents['items']!''}</span>
 
 <table><thead><tr>
   <td ><b>isin</b></td>
   <td style="text-align:center;" ><b>bidAxed</b></td>
   <td style="text-align:center;" ><b>askAxed</b></td>
   <td ><b>creator</b></td>
   <td style="text-align: right;"><b>bidQty</b></td>
   <td style="text-align: right;"><b>askQty</b></td><td style="text-align:center;" ><button name="items.table-delete-rows">Delete</button></td><td style="text-align:center;" ><button name="items.table-add-row">New</button></td>
 </tr></thead><tbody>
 <#list entity.formdata.items as iA>
 <tr>
  <td >${iA.isin!''}</td>
  <td style="text-align:center;" >${iA.bidAxed?string("Y", "N")}</td>
  <td style="text-align:center;" >${iA.askAxed?string("Y", "N")}</td>
  <td >${iA.creator!''}</td>
  <td style="text-align: right;">${iA.bidQty!''}</td>
  <td style="text-align: right;">${iA.askQty!''}</td><td style="text-align:center;" ><checkbox name="items.${iA?index}.selected" /></td><td style="text-align:center;" ><button name="items[${iA?index}].table-edit-row">Edit</button></td></tr>
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
<#-- ending template -->
