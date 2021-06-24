
<form
  id="org.finos.symphony.toolkit.tools.reminders.Reminder">
 <table>
 <tr><td><b>Description:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['description']!''}</span>
  <textarea
   name="description"
   placeholder="description">${entity.formdata.description!''}</textarea></td></tr>
 <tr><td><b>Remind At:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['instant']!''}</span>
  <text-field
   name="instant"
   placeholder="instant">${entity.formdata.instant!''}</text-field></td></tr>
 </table>
  <p><#list entity.buttons.contents as button>
    <button
         name="${button.name}"
         type="${button.buttonType?lower_case}">
      ${button.text}
    </button>
  </#list></p>
</form>