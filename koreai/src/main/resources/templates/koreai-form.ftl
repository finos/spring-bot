<messageML>
  <p>${entity.koreai.messageML}</p>
  <form id="koreai-choice">
    <#list entity['koreai'].options as o>
      <button type="action" name="${o}">${o}</button>
    </#list>
  </form>
</messageML>