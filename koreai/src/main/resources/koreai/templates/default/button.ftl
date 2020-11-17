<messageML>
  ${entity.koreai.messageML}
  <form id="koreai-choice">
    <#list entity.koreai.buttons as o>
      <button type="action" name="${o.payload}">${o.title}</button>
    </#list>
  </form>
</messageML>