
<#-- starting template -->
<form 
 id="com.github.deutschebank.symphony.workflow.fixture.TestObject">
 <span class="tempo-text-color--red">${entity.errors.contents['isin']!''}</span>
 <text-field 
  name="isin"
  placeholder="isin">${entity.formdata.isin!''}</text-field>
 <span class="tempo-text-color--red">${entity.errors.contents['bidAxed']!''}</span>
 <checkbox 
  name="bidAxed"
  checked="${entity.formdata.bidAxed?string('true', 'false')}"
  value="true">bid axed</checkbox>
 <span class="tempo-text-color--red">${entity.errors.contents['askAxed']!''}</span>
 <checkbox 
  name="askAxed"
  checked="${entity.formdata.askAxed?string('true', 'false')}"
  value="true">ask axed</checkbox>
 <span class="tempo-text-color--red">${entity.errors.contents['creator']!''}</span>
 <text-field 
  name="creator"
  placeholder="creator">${entity.formdata.creator!''}</text-field>
 <span class="tempo-text-color--red">${entity.errors.contents['bidQty']!''}</span>
 <text-field 
  name="bidQty"
  placeholder="bid qty">${entity.formdata.bidQty!''}</text-field>
 <span class="tempo-text-color--red">${entity.errors.contents['askQty']!''}</span>
 <text-field 
  name="askQty"
  placeholder="ask qty">${entity.formdata.askQty!''}</text-field>
  <p><#list entity.buttons.contents as button>
    <button 
         name="${button.name}"
         type="${button.buttonType?lower_case}">
      ${button.text}
    </button>
  </#list></p>
</form>
<#-- ending template -->
