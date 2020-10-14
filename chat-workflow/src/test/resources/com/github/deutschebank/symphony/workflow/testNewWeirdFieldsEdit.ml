
<#-- starting template -->
<form 
 id="com.github.deutschebank.symphony.workflow.fixture.TestOb4"><select 
  name="c"
  required="false"
  data-placeholder="Choose c">
 <option 
  value="A"
  selected="${((entity.formdata.c!'') == 'A')?then('true', 'false')}">A</option>
 <option 
  value="B"
  selected="${((entity.formdata.c!'') == 'B')?then('true', 'false')}">B</option>
 <option 
  value="C"
  selected="${((entity.formdata.c!'') == 'C')?then('true', 'false')}">C</option></select>
 <span class="tempo-text-color--red">${entity.errors['b']!''}</span>
 <checkbox 
  name="b"
  checked="${entity.formdata.b?string('true', 'false')}"
  value="true">b</checkbox>
 <span class="tempo-text-color--red">${entity.errors['someUser']!''}</span>
 <person-selector 
  name="someUser"
  placeholder="some user" required="false"/>
  <p><#list entity.buttons.contents as button>
    <button 
         name="${button.name}"
         type="${button.buttonType?lower_case}">
      ${button.text}
    </button>
  </#list></p>
</form>
<#-- ending template -->
