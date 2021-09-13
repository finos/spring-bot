<messageML>
  <card iconSrc="data:image/svg+xml,%0A%3Csvg width='28px' height='28px' fill='%23000000' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' version='1.1' x='0px' y='0px' viewBox='0 0 100 100' enable-background='new 0 0 20 20' xml:space='preserve'%3E%3Cpath d='M87.2,54.039c-0.722,0-1.406,0.148-2.038,0.397c-3.721-9.993-12.229-17.654-22.741-20.203V21.047 c1.607-0.392,2.989-1.565,3.551-3.251c0.867-2.604-0.543-5.416-3.149-6.285c-2.604-0.867-5.418,0.545-6.285,3.151 c-0.865,2.603,0.545,5.419,3.15,6.285c0.133,0.043,0.267,0.057,0.4,0.089v12.725c-1.734-0.287-3.504-0.473-5.32-0.473h-9.515 c-1.741,0-3.438,0.176-5.105,0.44V21.047c1.608-0.392,2.988-1.565,3.551-3.251c0.867-2.604-0.543-5.416-3.149-6.285 c-2.604-0.867-5.419,0.545-6.286,3.151c-0.865,2.603,0.545,5.419,3.15,6.285c0.133,0.043,0.267,0.057,0.401,0.089v13.143 c-10.614,2.495-19.213,10.194-22.96,20.257c-0.631-0.249-1.314-0.396-2.035-0.396c-3.064,0-5.548,2.483-5.548,5.547v8.946 c0,3.063,2.484,5.547,5.547,5.547v12.615H87.2V74.079c3.063,0,5.546-2.483,5.546-5.547v-8.946 C92.746,56.522,90.264,54.039,87.2,54.039z M33.577,69.465c-2.983,0-5.405-2.42-5.405-5.404c0-2.987,2.422-5.406,5.405-5.406 c2.985,0,5.405,2.419,5.405,5.406C38.982,67.045,36.562,69.465,33.577,69.465z M66.661,69.465c-2.987,0-5.406-2.42-5.406-5.404 c0-2.987,2.419-5.406,5.406-5.406c2.983,0,5.4,2.419,5.4,5.406C72.062,67.045,69.645,69.465,66.661,69.465z'%3E%3C/path%3E%3C/svg%3E" 
    accent="tempo-bg-color--blue">
    <header> 
      <form id="example.symphony.demoworkflow.poll.PollCreateForm">
       <span class="tempo-text-color--red">${entity.errors['question']!''}</span>
       <textarea 
        name="question" required="true"
        placeholder="question">${entity.form.question!''}</textarea>
        
       <div style='height:2px;background:#0098ff;margin-top:10px;margin-bottom:10px'> </div>
      
        
       <div style='margin-bottom:-10px'>
         <h6>Choices</h6>  
       <span class="tempo-text-color--red">${entity.errors['option1']!''}</span>
       <span class="tempo-text-color--red">${entity.errors['option2']!''}</span>
       <text-field 
        name="option1"
        placeholder="option1">${entity.form.option1!''}</text-field>
       <text-field 
        name="option2"
        placeholder="option2">${entity.form.option2!''}</text-field>
        </div> 
         
       <div style='margin-bottom:-10px'>
       <span class="tempo-text-color--red">${entity.errors['option3']!''}</span>
       <span class="tempo-text-color--red">${entity.errors['option4']!''}</span>
       <text-field 
        name="option3"
        placeholder="option3">${entity.form.option3!''}</text-field>
       <text-field 
        name="option4"
        placeholder="option4">${entity.form.option4!''}</text-field>
        </div>  
        
       <div style='margin-bottom:-10px'>
       <span class="tempo-text-color--red">${entity.errors['option5']!''}</span>
       <span class="tempo-text-color--red">${entity.errors['option6']!''}</span>
        <text-field 
           name="option5"
           placeholder="option5">${entity.form.option5!''}</text-field>
       <text-field 
           name="option6"
           placeholder="option6">${entity.form.option6!''}</text-field>
       <span class="tempo-text-color--red">${entity.errors['time']!''}</span>
        </div>
        
       <div style='height:2px;background:#0098ff;margin-top:10px;margin-bottom:10px'> </div>
        <h6>Duration</h6>
          
          <checkbox 
        name="endAutomatically"
        checked="${entity.form.endAutomatically?string('true', 'false')}"
        value="true">End automatically after ...</checkbox>
          
       <text-field 
        name="time"
        placeholder="time">${entity.form.time!''}</text-field>
        
        
        <select 
        name="timeUnit"
        required="false"
        data-placeholder="Choose time unit">
       <option 
        value="MINUTES"
        selected="${((entity.form.timeUnit!'') == 'MINUTES')?then('true', 'false')}">MINUTES</option>
       <option 
        value="HOURS"
        selected="${((entity.form.timeUnit!'') == 'HOURS')?then('true', 'false')}">HOURS</option>
       <option 
        value="DAYS"
        selected="${((entity.form.timeUnit!'') == 'DAYS')?then('true', 'false')}">DAYS</option></select>
          
        <div style='height:2px;background:#0098ff;margin-top:10px;margin-bottom:10px'> </div>
      
      
        <p><#list entity.buttons.contents as button>
          <button 
               name="${button.name}"
               type="${button.buttonType?lower_case}">
            Start Poll
          </button>
        </#list></p>
      </form>
    </header> 
    <body>
      <#if entity.header.tags?size != 0>
        <ul>
          <#list entity.header.tags as tag>
            <li><hash tag="${tag.value}" /></li>
          </#list>
        </ul>
      </#if>
    </body>
  </card>
</messageML>