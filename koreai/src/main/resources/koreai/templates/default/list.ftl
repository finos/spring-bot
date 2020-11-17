<messageML>
  <p>LIST TEMPLATE</p>
  
    <#list entity.koreai.elements as e>
      <h2>${e.title}</h2>
      <img src="${e.image_url}" />
      <p>${e.subtitle}</p>
      <br />
    </#list>
</messageML>