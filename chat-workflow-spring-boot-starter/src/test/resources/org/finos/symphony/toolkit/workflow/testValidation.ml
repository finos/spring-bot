
<#-- starting template -->
<form 
  id="org.finos.symphony.toolkit.workflow.fixture.TestObject">
 <table>
 <tr><td><b>isin:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['isin']!''}</span>
  <text-field 
   name="isin"
   placeholder="isin">${entity.formdata.isin!''}</text-field></td></tr>
 <tr><td><b>bidAxed:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['bidAxed']!''}</span>
  <checkbox 
   name="bidAxed"
   checked="${entity.formdata.bidAxed?string('true', 'false')}"
   value="true">bid axed</checkbox></td></tr>
 <tr><td><b>askAxed:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['askAxed']!''}</span>
  <checkbox 
   name="askAxed"
   checked="${entity.formdata.askAxed?string('true', 'false')}"
   value="true">ask axed</checkbox></td></tr>
 <tr><td><b>creator:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['creator']!''}</span>
  <text-field 
   name="creator"
   placeholder="creator">${entity.formdata.creator!''}</text-field></td></tr>
 <tr><td><b>bidQty:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['bidQty']!''}</span>
  <text-field 
   name="bidQty"
   placeholder="bid qty">${entity.formdata.bidQty!''}</text-field></td></tr>
 <tr><td><b>askQty:</b></td><td>
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
