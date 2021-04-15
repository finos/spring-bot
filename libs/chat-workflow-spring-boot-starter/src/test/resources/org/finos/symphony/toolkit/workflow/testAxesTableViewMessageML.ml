
<#-- starting template -->
 <table>
 <tr><td><b>Items:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['items']!''}</span>
  
  <table><thead><tr>
  
    <td ><b>Isin</b></td>
  
    <td style="text-align:center;" ><b>Bid Axed</b></td>
  
    <td style="text-align:center;" ><b>Ask Axed</b></td>
  
    <td ><b>Creator</b></td>
  
    <td style="text-align: right;"><b>Bid Qty</b></td>
  
    <td style="text-align: right;"><b>Ask Qty</b></td>
  </tr></thead><tbody>
  <#list entity.workflow_001.items as iB>
  <tr>
  
   <td >${iB.isin!''}</td>
  
   <td style="text-align:center;" >${iB.bidAxed?string("Y", "N")}</td>
  
   <td style="text-align:center;" >${iB.askAxed?string("Y", "N")}</td>
  
   <td >${iB.creator!''}</td>
  
   <td style="text-align: right;">${iB.bidQty!''}</td>
  
   <td style="text-align: right;">${iB.askQty!''}</td>
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
