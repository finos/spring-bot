<form id="example.symphony.demoworkflow.poll.bot.CreatePoll">
    <div style='display:flex;padding-top:8px'>
        <div><img src="https://symphony.com/wp-content/uploads/2019/08/favicon.png" style='height:20px' /></div>
        <div style='padding-top:1px;padding-left:5px;'><b>Create New Poll</b></div>
    </div>

    <div style='height:2px;background:#0098ff;margin-top:10px;margin-bottom:10px'> </div>
    <h6>Question</h6>
    <textarea name="question" placeholder="Enter your poll question.." required="true"></textarea>

    <h6>Answers</h6>
    <#list (1..entity['workflow_001'].count)?chunk(2) as row>
        <div style='margin-bottom:-10px'>
        <#list row as option>
            <#if option < 3 >
                <#assign required="true">
            <#else>
                <#assign required="false">
            </#if>
            <text-field name="option${option}" placeholder="Option ${option}" required="${required}" />
        </#list>
        </div>
    </#list>
    <br />	    
	
    <h6>Time Limit</h6>
    <div style='display:flex'>
        <#list entity['workflow_001'].timeLimits as timeLimit>
            <#if timeLimit?index==0>
                <#assign checked="true">
            <#else>
                <#assign checked="false">
            </#if>
            <#if timeLimit==0>
                <#assign label="None">
            <#elseif timeLimit==1>
                <#assign label="1 minute">
            <#else>
                <#assign label="${timeLimit} minutes">
            </#if>
            <div style='padding-right:6px;'><radio name="timeLimit" checked="${checked}" value="${timeLimit}">${label}</radio></div>
        </#list>
    </div>

    <div style='height:1px;background:#0098ff;margin-top:10px;margin-bottom:10px'> </div>
    <button name="createpoll+0" type="action">Create Poll</button>
    <radio name="id" value="${entity['workflow_001'].id}" checked="true">${entity['workflow_001'].id}</radio>
</form>
