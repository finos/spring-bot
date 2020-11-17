<messageML>
  <p>CAROUSEL</p>
  <form id="koreai-form">
 <#list entity.koreai.elements as e>
      <h2>${e.title}</h2>
      <img src="${e.image_url}" />
      <p>${e.subtitle}</p>
      <#list e.buttons as b>
        <button name="${b.payload}">${b.title}</button>
      </#list>
      <br />
      
    </#list>
    
  </form>
</messageML>