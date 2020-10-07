  
<form 
 id="example.symphony.demoworkflow.poll.PollCreateForm">
 <span class="tempo-text-color--red">${entity.errors['question']!''}</span>
 <textarea 
  name="question" required="true"
  placeholder="question">${entity.formdata.question!''}</textarea>
  
 <div style='height:2px;background:#0098ff;margin-top:10px;margin-bottom:10px'> </div>

  
 <div style='margin-bottom:-10px'>
   <h6>Choices</h6>  
 <span class="tempo-text-color--red">${entity.errors['option1']!''}</span>
 <span class="tempo-text-color--red">${entity.errors['option2']!''}</span>
 <text-field 
  name="option1"
  placeholder="option1">${entity.formdata.option1!''}</text-field>
 <text-field 
  name="option2"
  placeholder="option2">${entity.formdata.option2!''}</text-field>
  </div> 
   
 <div style='margin-bottom:-10px'>
 <span class="tempo-text-color--red">${entity.errors['option3']!''}</span>
 <span class="tempo-text-color--red">${entity.errors['option4']!''}</span>
 <text-field 
  name="option3"
  placeholder="option3">${entity.formdata.option3!''}</text-field>
 <text-field 
  name="option4"
  placeholder="option4">${entity.formdata.option4!''}</text-field>
  </div>  
  
 <div style='margin-bottom:-10px'>
 <span class="tempo-text-color--red">${entity.errors['option5']!''}</span>
 <span class="tempo-text-color--red">${entity.errors['option6']!''}</span>
  <text-field 
     name="option5"
     placeholder="option5">${entity.formdata.option5!''}</text-field>
 <text-field 
     name="option6"
     placeholder="option6">${entity.formdata.option6!''}</text-field>
 <span class="tempo-text-color--red">${entity.errors['time']!''}</span>
  </div>
  
 <div style='height:2px;background:#0098ff;margin-top:10px;margin-bottom:10px'> </div>
  <h6>Duration</h6>
 <text-field 
  name="time"
  placeholder="time">${entity.formdata.time!''}</text-field>
  
  
  <select 
  name="timeUnit"
  required="false"
  data-placeholder="Choose time unit">
 <option 
  value="MINUTES"
  selected="${((entity.formdata.timeUnit!'') == 'MINUTES')?then('true', 'false')}">MINUTES</option>
 <option 
  value="HOURS"
  selected="${((entity.formdata.timeUnit!'') == 'HOURS')?then('true', 'false')}">HOURS</option>
 <option 
  value="DAYS"
  selected="${((entity.formdata.timeUnit!'') == 'DAYS')?then('true', 'false')}">DAYS</option></select>
  
 <p>Leave time blank for an open-ended poll</p>
  
  <div style='height:2px;background:#0098ff;margin-top:10px;margin-bottom:10px'> </div>


  <p><#list entity.buttons.contents as button>
    <button 
         name="${button.name}"
         type="${button.buttonType?lower_case}">
      Start Poll
    </button>
  </#list></p>
</form>