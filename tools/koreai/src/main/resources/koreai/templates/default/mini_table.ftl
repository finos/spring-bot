<messageML>
  <p>${entity.koreai.messageML}</p>
  <#list entity.koreai.elements as t>
    <table style="box-shadow: 5px 5px 5px #aaaaaa;width: 75%">
      <thead>
        <tr>
          <#list t.primary as h>
            <#assign align = '${(h[1])!"left"}' />
            <th style="text-align: ${align}">${h[0]}</th>
          </#list>
        </tr>
      </thead>
      <#if t.additional??>
        <#list t.additional as r>
          <tr>
            <#list r as c>
             <#assign align = '${(t.primary[c?index][1])!"left"}' />
             <td style="text-align: ${align}">${c}</td>
            </#list>
          </tr>
        </#list>
      </#if>
    </table>
    <hr />
  </#list>
</messageML>