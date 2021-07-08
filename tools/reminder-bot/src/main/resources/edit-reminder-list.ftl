<form
  id="org.finos.symphony.toolkit.tools.reminders.ReminderList">
 <table>
 <tr><td></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['reminders']!''}</span>

  <table><thead><tr>

    <td><b>Description</b></td>

    <td><b>Local Time</b></td>

    <td><b>Author</b></td>

  </tr></thead><tbody>
  <#list entity.formdata.reminders as iB>
  <tr>

   <td >${iB.description!''}</td>

   <td >${iB.localTime!''}</td>

   <td >${iB.author!''}</td>
   <td style="text-align:center; width:10%" ><checkbox name="reminders.${iB?index}.selected" /></td>
   <td style="text-align:center;" ><button name="reminders[${iB?index}].table-edit-row">Edit</button></td>
  </tr>
  </#list>
  </tbody></table></td></tr>
 </table>
  <p><#list entity.buttons.contents as button>
    <button
         name="${button.name}"
         type="${button.buttonType?lower_case}">
      ${button.text}
    </button>
  </#list></p>
</form>

  </header>
  <body>
    <p>${entity.header.description}</p>
    <ul>
      <#list entity.header.tags as tag>
        <li><hash tag="${tag.id}" /></li>
      </#list>
    </ul>
  </body>

