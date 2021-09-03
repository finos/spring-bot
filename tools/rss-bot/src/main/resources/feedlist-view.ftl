  <#if entity.form.feeds?size == 0>
    
    <b>No RSS Feeds Configured: Click Add below to add a new one</b>
    
    
    <hr />
    
    <form id="just-buttons-form"><p>
        <button name="add" type="action">Add</button>
    </p></form>  
    
  </#if>  
  <#if entity.form.feeds?size &gt; 0>

    <table><thead><tr>

      <td ><b>name</b></td>

      <td ><b>description</b></td>
    </tr></thead><tbody>
    <#list entity.form.feeds as iB>
    <tr>
     <td >${iB.name!''}</td>
     <td >${iB.description!''}</td>
    </tr>
    </#list>
    </tbody></table>
    <#if entity.form.paused == false>
      <form id="just-buttons-form"><p>
        <button name="add" type="action">Add</button>
        <button name="wf-edit" type="action">Edit</button>
        <button name="pause" type="action">Pause</button>
    </p></form>  
    </#if>
    
    <#if entity.form.adminOnly == true>
       <p><i>Only room admins can add feeds</i></p>
       <hr />    
    </#if>
    
    <#if entity.form.paused == true>
      <b>Feeds are currently suspended. Click "resume" below to continue feeding in this room</b>
    <hr />
    <form id="just-buttons-form"><p>
        <button name="resume" type="action">Resume</button>
    </p></form>    
    </#if>
    
    <#if entity.form.paused == false>
      <b>Feeds news every ${entity.form.updateIntervalMinutes} minutes</b>
      <hr />
    </#if>
    
  </#if>