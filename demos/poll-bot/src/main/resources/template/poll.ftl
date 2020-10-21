  
  <p>Poll has begun.  Check your personal messages from this bot</p>
  
   <div style='height:2px;background:#0098ff;margin-top:10px;margin-bottom:10px'> </div>

<form 
 id="example.symphony.demoworkflow.poll.Question">
  <p><#list entity.buttons.contents as button>
    <button 
         name="${button.name}"
         type="${button.buttonType?lower_case}">
      ${button.text}
    </button>
  </#list></p>
</form>