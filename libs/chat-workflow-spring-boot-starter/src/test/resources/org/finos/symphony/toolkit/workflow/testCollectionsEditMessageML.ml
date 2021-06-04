
<#-- starting template -->
<form 
  id="org.finos.symphony.toolkit.workflow.fixture.TestCollections">
 <table>
 <tr><td><b>String List:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['stringList']!''}</span>
  
  <table><thead><tr><td><b>Value</b></td>
   <td style="text-align:center;" ><button name="stringList.table-delete-rows">Delete</button></td>
   <td style="text-align:center;" ><button name="stringList.table-add-row">New</button></td>
  </tr></thead><tbody>
  <#list entity.formdata.stringList as iB>
  <tr><td>${iB!''}</td>
   <td style="text-align:center; width:10%" ><checkbox name="stringList.${iB?index}.selected" /></td>
   <td style="text-align:center;" ><button name="stringList[${iB?index}].table-edit-row">Edit</button></td>
  </tr>
  </#list>
  </tbody></table></td></tr>
 <tr><td><b>Enum List:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['enumList']!''}</span>
  
  <table><thead><tr><td><b>Value</b></td>
   <td style="text-align:center;" ><button name="enumList.table-delete-rows">Delete</button></td>
   <td style="text-align:center;" ><button name="enumList.table-add-row">New</button></td>
  </tr></thead><tbody>
  <#list entity.formdata.enumList as iB>
  <tr><td>${iB!''}</td>
   <td style="text-align:center; width:10%" ><checkbox name="enumList.${iB?index}.selected" /></td>
   <td style="text-align:center;" ><button name="enumList[${iB?index}].table-edit-row">Edit</button></td>
  </tr>
  </#list>
  </tbody></table></td></tr>
 <tr><td><b>Min Bean List:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['minBeanList']!''}</span>
  
  <table><thead><tr>
  
    <td><b>Some String</b></td>
  
    <td style="text-align: right;"><b>Some Integer</b></td>
  
    <td><b>Some More Strings</b></td>
   <td style="text-align:center;" ><button name="minBeanList.table-delete-rows">Delete</button></td>
   <td style="text-align:center;" ><button name="minBeanList.table-add-row">New</button></td>
  </tr></thead><tbody>
  <#list entity.formdata.minBeanList as iB>
  <tr>
  
   <td >${iB.someString!''}</td>
  
   <td style="text-align: right;">${iB.someInteger!''}</td>
  
   <td >...</td>
   <td style="text-align:center; width:10%" ><checkbox name="minBeanList.${iB?index}.selected" /></td>
   <td style="text-align:center;" ><button name="minBeanList[${iB?index}].table-edit-row">Edit</button></td>
  </tr>
  </#list>
  </tbody></table></td></tr>
 <tr><td><b>Some Hash Tags:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['someHashTags']!''}</span>
  
  <table><thead><tr><td><b>Value</b></td>
   <td style="text-align:center;" ><button name="someHashTags.table-delete-rows">Delete</button></td>
   <td style="text-align:center;" ><button name="someHashTags.table-add-row">New</button></td>
  </tr></thead><tbody>
  <#list entity.formdata.someHashTags as iB>
  <tr><td>
  <#if iB??><cash 
   tag="${iB.name!''}" /></#if></td>
   <td style="text-align:center; width:10%" ><checkbox name="someHashTags.${iB?index}.selected" /></td>
   <td style="text-align:center;" ><button name="someHashTags[${iB?index}].table-edit-row">Edit</button></td>
  </tr>
  </#list>
  </tbody></table></td></tr>
 </table>
  <p><#list entity.buttons.contents as button>
    <button 
         name="${button.name}"
         type="${button.buttonType?lower_case}">
      ${button.text}
    </button>
  </#list></p>
</form>
<#-- ending template -->
