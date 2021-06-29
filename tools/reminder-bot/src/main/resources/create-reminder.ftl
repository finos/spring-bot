
<form
  id="org.finos.symphony.toolkit.tools.reminders.Reminder">
 <table>
 <tr><td><b>Description:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['description']!''}</span>
  <textarea
   name="description"
   placeholder="description">${entity.formdata.description!''}</textarea></td></tr>
 <tr><td><b>Remind At (Local Time):</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['localTime']!''}</span>
  <text-field
   name="localTime"
   placeholder="localTime">${entity.formdata.localTime!''}</text-field></td></tr>
 </table>
  <p><#list entity.buttons.contents as button>
    <button
         name="${button.name}"
         type="${button.buttonType?lower_case}">
      ${button.text}
    </button>
  </#list></p>
</form>