  <#if entity.workflow_001.feeds?size == 0>
    
    <b>No RSS Feeds Configured: Click Add below to add a new one</b>
    
    
    <hr />
    
    <form id="just-buttons-form"><p>
        <button name="add" type="action">Add</button>
    </p></form>  
    
  </#if>  
  <#if entity.workflow_001.feeds?size &gt; 0>

    <table><thead><tr>

      <td ><b>name</b></td>

      <td ><b>description</b></td>
    </tr></thead><tbody>
    <#list entity.workflow_001.feeds as iB>
    <tr>
     <td ><a href="${iB.url!''}">${iB.name!''}</a></td>
     <td >${iB.description!''}</td>
    </tr>
    </#list>
    </tbody></table>
    <#if entity.workflow_001.paused == false>
      <i>Feeding Every Hour</i>
    <hr />
      <form id="just-buttons-form"><p>
        <button name="add" type="action">Add</button>
        <button name="wf-edit" type="action">Edit</button>
        <button name="pause" type="action">Pause</button>
    </p></form>  
    </#if>
    <#if entity.workflow_001.paused == true>
      <b>Feeds are currently suspended. Click "resume" below to continue feeding in this room</b>
    <hr />
    <form id="just-buttons-form"><p>
        <button name="resume" type="action">Resume</button>
    </p></form>    
    </#if>
    
  </#if>