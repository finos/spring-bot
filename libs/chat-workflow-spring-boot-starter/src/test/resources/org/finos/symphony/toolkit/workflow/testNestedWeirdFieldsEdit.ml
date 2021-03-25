
<#-- starting template -->
<form 
  id="org.finos.symphony.toolkit.workflow.fixture.TestOb5">
 <table>
 <tr><td><b>ob4:</b></td><td>
  <table>
  <tr><td><b>theId:</b></td><td></td></tr>
  <tr><td><b>c:</b></td><td><select 
    name="c"
    required="false"
    data-placeholder="Choose c">
   <option 
    value="A"
    selected="${((entity.formdata.ob4.c!'') == 'A')?then('true', 'false')}">A</option>
   <option 
    value="B"
    selected="${((entity.formdata.ob4.c!'') == 'B')?then('true', 'false')}">B</option>
   <option 
    value="C"
    selected="${((entity.formdata.ob4.c!'') == 'C')?then('true', 'false')}">C</option></select></td></tr>
  <tr><td><b>b:</b></td><td>
   <span class="tempo-text-color--red">${entity.errors.contents['b']!''}</span>
   <checkbox 
    name="b"
    checked="${entity.formdata.ob4.b?string('true', 'false')}"
    value="true">b</checkbox></td></tr>
  <tr><td><b>a:</b></td><td><#if entity.formdata.ob4.a??><mention 
    uid="${entity.formdata.ob4.a.id}" /></#if></td></tr>
  <tr><td><b>someUser:</b></td><td>
   <span class="tempo-text-color--red">${entity.errors.contents['someUser']!''}</span>
   <person-selector 
    name="someUser"
    placeholder="some user" required="false"/></td></tr>
  </table></td></tr>
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
