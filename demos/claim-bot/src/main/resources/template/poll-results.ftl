<div style='display:flex;padding-top:8px'>
    <div><img src="https://symphony.com/wp-content/uploads/2019/08/favicon.png" style='height:20px' /></div>
    <div style='padding-top:1px;padding-left:5px;'>
        <b>Poll Results: ${entity['workflow_001'].question}</b> by <b>${entity['workflow_001'].creatorDisplayName}</b>
    </div>
</div>

<div style='height:2px;background:#0098ff;margin-top:10px;margin-bottom:10px'> </div>

<table>
    <tr>
        <th>Answer</th>
        <th style="text-align:right">Votes</th>
        <th></th>
    </tr>
    <#list entity['workflow_001'].results as result>
        <tr>
            <td>${result.answer}</td>
            <td style="text-align:right">${result.count}</td>
            <td><div style='background-color:#29b6f6;width:${result.width}px'> </div></td>
        </tr>
    </#list>
</table>
