<messageML>
    <div class="entity" data-entity-id="event">
        <card class="barStyle" accent="tempo-bg-color--green" iconSrc="https://vscjava.gallerycdn.vsassets.io/extensions/vscjava/vscode-maven/0.18.1/1563248098892/Microsoft.VisualStudio.Services.Icons.Default">
            <header>
                <div>
                    <span class="tempo-text-color--secondary">Description:</span>
                    <a class="tempo-text-color--link" href="${entity['event'].url}">
                        ${entity['event'].title}
                    </a>
                    <span class="tempo-text-color--secondary">Status:</span>
                    <#if entity['event'].passed>
                    <span class="tempo-bg-color--green tempo-text-color--white tempo-token">PASSED</span>
                    </#if>
                    <#if !entity['event'].passed>
                    <span class="tempo-bg-color--red tempo-text-color--white tempo-token">FAILED</span>
                    </#if> 
                </div>
            </header>
            <body>
                <div>
                    <div>
                        <table>
                          <#list entity['event'].projects as p>
                           <tr>
                             <td>${p.name}</td>
                             <td>
                              <#if p.status == 'BuildSuccess'>
                                <span class="tempo-bg-color--green tempo-text-color--white tempo-token">PASSED</span>
                              </#if>
                              <#if p.status == 'BuildFailure'>
                                <span class="tempo-bg-color--red tempo-text-color--white tempo-token">FAILED</span>
                              </#if>
                               <#if p.status == 'Skipped'>
                                <span class="tempo-bg-color--orange tempo-text-color--white tempo-token">SKIPPED</span>
                              </#if>
                             </td>
                             <td>
                               ${p.time } ms
                             </td>
                          </tr>
                          </#list>
                      </table>  
                      
                      <#if entity['event'].exceptions?size != 0>
                      <br />
                      <h3>Exceptions</h3>
                      <#list entity['event'].exceptions as e>
                      <code>${e}</code>
                      <hr />
                      </#list>
                      </#if>
                 

                      <#if entity['event'].developers?size != 0>
                      <h3>Developers</h3>
                      <ul>
                        <#list entity['event'].developers as d>
                        <li>${d.name}
                        <#if d.email??>
                          <mention email="${d.email}" />
                        </#if>  
                        </li>
                        </#list>
                      </ul>
                      <hr />
                      </#if>   
                      
                        <#if entity['event'].hashtags?size != 0>
                            <span class="tempo-text-color--secondary">Labels:</span>
                            <#list entity['event'].hashtags as label>
                                <hash tag="${label}" />
                            </#list>
                            <hr />
                        </#if>
                        
                        <span class="tempo-text-color--secondary">Maven Symphony Build Reporter</span>
                        <hr />
                        <a href="https://github.com/finos/symphony-java-toolkit/tree/master/symphony-maven-build-reporter" />
                        
                    </div>
                </div>
            </body>
        </card>
    </div>
</messageML>