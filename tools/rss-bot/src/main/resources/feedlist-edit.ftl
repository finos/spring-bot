<form 
  id="org.finos.symphony.rssbot.feed.FeedList">
  <span class="tempo-text-color--red">${entity.errors.contents['feeds']!''}</span>
  
  <h3>Feeds</h3>
  
  <table><thead><tr>
  
    <td ><b>name</b></td>
  
    <td ><b>description</b></td>
  
   <td style="text-align:center;" ><button name="feeds.table-delete-rows">Delete</button></td>
  </tr></thead><tbody>
  <#list entity.formdata.feeds as iB>
  <tr>
  
    <td >${iB.name!''}</td>
  
   <td >${iB.description!''}</td>
  
   <td style="text-align: center;" ><checkbox name="feeds.${iB?index}.selected" /></td>
  </tr>
  </#list>
  
  
  
  </tbody></table>
  
  
  <h3>Filters</h3>
  
  <table><thead><tr>
  
    <td><b>To Match</b></td>
  
    <td ><b>Usage</b></td>
   <td style="text-align:center;" ><button name="filters.table-delete-rows">Delete</button></td>
  </tr></thead><tbody>
  <#list entity.formdata.filters as iB>
  <tr>
  
   <td >${iB.toMatch!''}</td>
  
   <td >${iB.usage!''}</td>
   <td style="text-align:center; width:10%" ><checkbox name="filters.${iB?index}.selected" /></td>
  </tr>
  </#list>
  </tbody></table>
  
  <p><#list entity.buttons.contents as button>
    <button 
         name="${button.name}"
         type="${button.buttonType?lower_case}">
      ${button.text}
    </button>
  </#list></p>
</form>