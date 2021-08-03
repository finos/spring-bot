
<#-- starting template -->
 <table>
 <tr><td><b>String List:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['stringList']!''}</span>
  
  <table><thead><tr><td><b>Value</b></td>
  </tr></thead><tbody>
  <#list entity.workflow_001.stringList as iB>
  <tr><td>${iB!''}</td>
  </tr>
  </#list>
  </tbody></table></td></tr>
 <tr><td><b>Enum List:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['enumList']!''}</span>
  
  <table><thead><tr><td><b>Value</b></td>
  </tr></thead><tbody>
  <#list entity.workflow_001.enumList as iB>
  <tr><td>${iB!''}</td>
  </tr>
  </#list>
  </tbody></table></td></tr>
 <tr><td><b>Min Bean List:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['minBeanList']!''}</span>
  
  <table><thead><tr>
  
    <td><b>Some String</b></td>
  
    <td style="text-align: right;"><b>Some Integer</b></td>
  
    <td><b>Some More Strings</b></td>
  </tr></thead><tbody>
  <#list entity.workflow_001.minBeanList as iB>
  <tr>
  
   <td >${iB.someString!''}</td>
  
   <td style="text-align: right;">${iB.someInteger!''}</td>
  
   <td >...</td>
  </tr>
  </#list>
  </tbody></table></td></tr>
 <tr><td><b>Some Hash Tags:</b></td><td>
  <span class="tempo-text-color--red">${entity.errors.contents['someHashTags']!''}</span>
  
  <table><thead><tr><td><b>Value</b></td>
  </tr></thead><tbody>
  <#list entity.workflow_001.someHashTags as iB>
  <tr><td>
  <#if iB??><cash 
   tag="${iB.name!''}" /></#if></td>
  </tr>
  </#list>
  </tbody></table></td></tr>
 </table>
<form 
  id="just-buttons-form">
  <p><#list entity.buttons.contents as button>
    <button 
         name="${button.name}"
         type="${button.buttonType?lower_case}">
      ${button.text}
    </button>
  </#list></p>
</form>
<#-- ending template -->
