<#ftl output_format="HTML">
<messageML>
  <form id=".">
  <expandable-card state="collapsed">
    <header>
      <h6>This is custom help page</h6>
      <table style="table-layout: fixed;width:100%">
        <thead>
          <tr>
            <th>Description</th><th/><th/><th/><th/><th/><th/>
            <th>Type In Chat</th><th/><th/>
            <th>Or Click</th><th/>
          </tr>
        </thead>
        <tbody>
          <#list entity.form.commands as command>
            <#if command?counter gt 3>
              <#break>
            </#if>
            <tr>
            <td colspan="7">${command.description}</td>
            <td colspan="3">
                <ul>
                  <#list command.examples as example>
                    <li><pre style="display:inline">/${example}</pre></li>
                  </#list>
                </ul>
            </td>
            <td colspan="2">
                <#if command.button>
                    <button name="${command.buttonName}" type="action">${command.buttonName}</button>
                <#else>
                </#if>
            </td>
            </tr>
          </#list>
        </tbody>
      </table>
    </header>
    <#if entity.form.commands?size gt 3>
    <body>
      <table style="table-layout: fixed;width:100%">
        <tbody>
          <#list entity.form.commands as command>
            <#if command?counter lte 3>
              <#continue>
            </#if>
            <tr>
            <td colspan="7">${command.description}</td>
            <td colspan="3">
                <ul>
                  <#list command.examples as example>
                    <li><pre style="display:inline">/${example}</pre></li>
                  </#list>
                </ul>
            </td>
            <td colspan="2">
            	<#if command.button>
			    	<button name="${command.buttonName}" type="action">${command.buttonName}</button>
			    <#else>
		      	</#if>
            </td>
            </tr>
          </#list>
        </tbody>
      </table>
    </body>
    </#if>
  </expandable-card>
  </form>
</messageML>