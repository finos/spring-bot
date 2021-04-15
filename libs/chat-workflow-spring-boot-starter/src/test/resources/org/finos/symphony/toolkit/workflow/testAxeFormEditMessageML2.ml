
<#-- starting template -->
<form 
  id="org.finos.symphony.toolkit.workflow.fixture.TestObject">
 <table>
 <tr><td><b>Isin:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['isin']!''}</span>
  <text-field 
   name="isin"
   placeholder="isin">${entity.formdata.isin!''}</text-field></td></tr>
 <tr><td><b>Bid Axed:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['bidAxed']!''}</span>
  <checkbox 
   name="bidAxed"
   checked="${entity.formdata.bidAxed?string('true', 'false')}"
   value="true">bid axed</checkbox></td></tr>
 <tr><td><b>Ask Axed:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['askAxed']!''}</span>
  <checkbox 
   name="askAxed"
   checked="${entity.formdata.askAxed?string('true', 'false')}"
   value="true">ask axed</checkbox></td></tr>
 <tr><td><b>Creator:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['creator']!''}</span>
  <text-field 
   name="creator"
   placeholder="creator">${entity.formdata.creator!''}</text-field></td></tr>
 <tr><td><b>Bid Qty:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['bidQty']!''}</span>
  <text-field 
   name="bidQty"
   placeholder="bid qty">${entity.formdata.bidQty!''}</text-field></td></tr>
 <tr><td><b>Ask Qty:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['askQty']!''}</span>
  <text-field 
   name="askQty"
   placeholder="ask qty">${entity.formdata.askQty!''}</text-field></td></tr>
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
