<messageML>
  <p>${entity.koreai.template.payload.text}</p>
  <form id="koreai-choice">
    <#list entity['koreai'].template.payload.buttons[1] as o>
      <button type="action" name="${o.payload}">${o.title}</button>
    </#list>
  </form>
</messageML>