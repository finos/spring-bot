<messageML>
  ${entity.koreai.messageML}
  <#if entity.koreai.options?size &gt; 0>
  <form id="koreai-choice">
    <#list entity.koreai.options as o>
      <button type="action" name="${o.text}">${o.text}</button>
    </#list>
  </form>
  </#if>
</messageML>