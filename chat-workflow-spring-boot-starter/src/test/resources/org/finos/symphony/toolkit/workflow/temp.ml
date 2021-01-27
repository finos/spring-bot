<messageML>
<#-- starting template -->
<form 
 id="org.finos.symphony.toolkit.workflow.fixture.TestObjects">
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
  <td >
  <span class="tempo-text-color--red">${entity.errors.contents['isin']!''}</span>
  <text-field 
   name="isin"
   placeholder="isin">${iA.isin!''}</text-field></td>
  <td style="text-align:center;" >
  <span class="tempo-text-color--red">${entity.errors.contents['bidAxed']!''}</span>
  <checkbox 
   name="bidAxed"
   checked="${iA.bidAxed?string('true', 'false')}"
   value="true">bid axed</checkbox></td>
  <td style="text-align:center;" >
  <span class="tempo-text-color--red">${entity.errors.contents['askAxed']!''}</span>
  <checkbox 
   name="askAxed"
   checked="${iA.askAxed?string('true', 'false')}"
   value="true">ask axed</checkbox></td>
  <td >
  <span class="tempo-text-color--red">${entity.errors.contents['creator']!''}</span>
  <text-field 
   name="creator"
   placeholder="creator">${iA.creator!''}</text-field></td>
  <td style="text-align: right;">
  <span class="tempo-text-color--red">${entity.errors.contents['bidQty']!''}</span>
  <text-field 
   name="bidQty"
   placeholder="bid qty">${iA.bidQty!''}</text-field></td>
  <td style="text-align: right;">
  <span class="tempo-text-color--red">${entity.errors.contents['askQty']!''}</span>
  <text-field 
   name="askQty"
   placeholder="ask qty">${iA.askQty!''}</text-field></td><td style="text-align:center;" ><checkbox name="items.${iA?index}.selected" /></td><td style="text-align:center;" ><button name="items[${iA?index}].table-edit-row">Edit</button></td>
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
<#-- ending template -->
</messageML>