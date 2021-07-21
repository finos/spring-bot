
<#-- starting template -->
 <table>
 <tr><td><b>Ob4:</b></td><td>
  <table>
  <tr><td><b>The Id:</b></td><td>
   <#if entity.workflow_001.ob4.theId??><hash 
    tag="${entity.workflow_001.ob4.theId.name!''}" /></#if></td></tr>
  <tr><td><b>C:</b></td><td>${entity.workflow_001.ob4.c!''}</td></tr>
  <tr><td><b>B:</b></td><td>${entity.workflow_001.ob4.b?string("Y", "N")}</td></tr>
  <tr><td><b>A:</b></td><td><#if entity.workflow_001.ob4.a??><mention 
    uid="${entity.workflow_001.ob4.a.id}" /></#if></td></tr>
  <tr><td><b>Some User:</b></td><td><#if entity.workflow_001.ob4.someUser??><mention 
    uid="${entity.workflow_001.ob4.someUser.id}" /></#if></td></tr>
  </table></td></tr>
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
