<messageML>
  <p>${entity.koreai.messageML}</p>
  <form id="koreai-choice">
  <#list entity.koreai.quick_replies as b>
     <button name="${b.payload}">${b.title}</button>
  </#list>
  </form>
</messageML>