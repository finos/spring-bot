<header>${entity.header.name}

<form
  id="org.finos.symphony.toolkit.tools.reminders.Reminder">
 <table>
 <tr><td><b>Description:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['description']!''}</span>
  <textarea
   name="description"
   placeholder="description">${entity.formdata.description!''}</textarea></td></tr>
 <tr><td><b>Instant:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['instant']!''}</span>
  <text-field
   name="instant"
   placeholder="instant">${entity.formdata.instant!''}</text-field></td></tr>
 <tr><td><b>Author:</b></td><td>
  <span class="tempo-text-color--red" >${entity.errors.contents['author']!''}</span>
  <text-field
   name="author"
   placeholder="author"
   >${entity.formdata.author!''}
   </text-field></td></tr>
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

