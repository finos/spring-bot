  </header> 
  <body>
    <p>${entity.header.description}</p>
    <ul>
      <#list entity.header.tags as tag>
        <li><hash tag="${tag.id}" /></li>
      </#list>
    </ul>
  </body>
</card>
     
     