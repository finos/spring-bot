<p style="color:red;"> ${entity.workflow_001.description!''} </p> <p style="color:blue;">at ${entity.workflow_001.instant!''}</p> <p style="color:black;">(${entity.workflow_001.author!''}) </p>

 <table>
 <tr><td><b>Description:</b></td><td>${entity.workflow_001.description!''}</td></tr>
 <tr><td><b>Instant:</b></td><td>${entity.workflow_001.instant!''}</td></tr>
 <tr><td><b>Author:</b></td><td>${entity.workflow_001.author!''}</td></tr>
 </table>

  </header>
  <body>
    <p>${entity.header.description}</p>
    <ul>
      <#list entity.header.tags as tag>
        <li><hash tag="${tag.id}" /></li>
      </#list>
    </ul>
  </body>
