<form id="example.symphony.demoworkflow.poll.bot.CreatePollResponse">
    <div style='display:flex;padding-top:8px'>
        <div><img src="https://symphony.com/wp-content/uploads/2019/08/favicon.png" style='height:20px' /></div>
        <div style='padding-top:1px;padding-left:5px;'>
            <b>Poll: ${entity['workflow_001'].question}</b> by <b>${entity['workflow_001'].creatorDisplayName}</b>
        </div>
    </div>

    <div style='height:2px;background:#0098ff;margin-top:10px;margin-bottom:10px'> </div>

    <#list entity['workflow_001'].answers as answer>
        <button name="option_${answer?index}+0" type="action">${answer}</button>
    </#list>

    <div style='height:1px;background:#0098ff;margin-top:10px;margin-bottom:10px'> </div>

    <i>This poll
        <#if entity['workflow_001'].timeLimit == 0>
            does not have a time limit
        <#else>
            will end in ${entity['workflow_001'].timeLimit} minute<#if entity['workflow_001'].timeLimit gt 1>s</#if>
        </#if>
    </i>
    <radio name="id" value="${entity['workflow_001'].id}" checked="true">${entity['workflow_001'].id}</radio>
</form>
