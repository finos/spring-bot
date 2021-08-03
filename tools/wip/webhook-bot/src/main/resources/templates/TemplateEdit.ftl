<form 
  id="org.finos.symphony.webhookbot.domain.Template">
 <table>
 <tr><td><b>name:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['name']!''}</span>
  <text-field 
   name="name"
   placeholder="name">${entity.formdata.name!''}</text-field></td></tr>
 <tr><td><b>shared:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['shared']!''}</span>
  <checkbox 
   name="shared"
   checked="${entity.formdata.shared?string('true', 'false')}"
            value="true">shared</checkbox><i>Check to make available to other Symphony users</i></td></tr>
 <tr><td><b>contents:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['contents']!''}</span>
  <textarea
   name="contents" 
   placeholder="contents">${entity.formdata.contents?html}</textarea></td></tr>
 </table>
  <p><#list entity.buttons.contents as button>
    <button 
         name="${button.name}"
         type="${button.buttonType?lower_case}">
      ${button.text}
    </button>
  </#list></p>
</form>