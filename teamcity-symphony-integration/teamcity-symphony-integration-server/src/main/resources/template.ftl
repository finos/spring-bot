<messageML>
    <div class="entity" data-entity-id="teamcity">
        <card class="barStyle" accent="tempo-bg-color--${entity.teamcity.statusColor}" iconSrc="data:image/svg+xml,%3Csvg data-name='Layer 1' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' viewBox='0 0 128.01 128.01' width='2500' height='2500'%3E%3Cdefs%3E%3ClinearGradient id='b' x1='66.73' y1='-662.84' x2='26.4' y2='-613.01' gradientTransform='matrix(1 0 0 -1 0 -585.01)' gradientUnits='userSpaceOnUse'%3E%3Cstop offset='.06' stop-color='%230cb0f2'/%3E%3Cstop offset='.21' stop-color='%2310adf2'/%3E%3Cstop offset='.37' stop-color='%231ea5f3'/%3E%3Cstop offset='.54' stop-color='%233497f5'/%3E%3Cstop offset='.71' stop-color='%235283f7'/%3E%3Cstop offset='.88' stop-color='%23796af9'/%3E%3Cstop offset='.97' stop-color='%23905cfb'/%3E%3C/linearGradient%3E%3ClinearGradient id='a' x1='20.9' y1='-706.33' x2='41.11' y2='-659.59' gradientTransform='matrix(1 0 0 -1 0 -585.01)' gradientUnits='userSpaceOnUse'%3E%3Cstop offset='.06' stop-color='%230cb0f2'/%3E%3Cstop offset='.2' stop-color='%230db2ee'/%3E%3Cstop offset='.35' stop-color='%2312b7e0'/%3E%3Cstop offset='.51' stop-color='%2319c0ca'/%3E%3Cstop offset='.68' stop-color='%2323ccac'/%3E%3Cstop offset='.85' stop-color='%2330dc85'/%3E%3Cstop offset='.97' stop-color='%233bea62'/%3E%3C/linearGradient%3E%3ClinearGradient id='c' x1='48.62' y1='-644.91' x2='88.12' y2='-594.24' xlink:href='%23a'/%3E%3ClinearGradient id='d' x1='63.99' y1='-609.92' x2='63.99' y2='-689.92' gradientTransform='matrix(1 0 0 -1 0 -585.01)' gradientUnits='userSpaceOnUse'%3E%3Cstop offset='0'/%3E%3Cstop offset='1'/%3E%3C/linearGradient%3E%3C/defs%3E%3Ctitle%3Eicon_TeamCity%3C/title%3E%3Cpath d='M45.92 2.92a25 25 0 0 0-5.53-2c-25.7-6.1-44.8 19.1-34.3 42 .1.1 18.7 40.9 21.7 47.4 0 0 34.2-15.7 41.2-31.8 6.48-15-4.45-42.38-6.45-46.49z' fill='url(%23b)'/%3E%3Cpath d='M123.19 48.43a24.36 24.36 0 0 0-8.6-8c-.1 0-24.1-13.2-24.1-13.2S8.09 75 8 75.13c-9.3 10.6-11.4 27.4-.7 42a25.22 25.22 0 0 0 13 9.2c9.7 3 18.3 1.8 25.5-2 .2-.1 67.7-35.9 67.9-36.1 13.19-6.9 19.3-24.5 9.49-39.8z' fill='%230cb0f2'/%3E%3Cpath d='M38.42 37.94l-22 27-8.53 10.19c-9.3 10.6-11.4 27.4-.7 42a25.22 25.22 0 0 0 13 9.2c9.7 3 18.4 1.8 25.6-2 0 0 1.7-.9 4.6-2.5 9.14-4.94 21.75-38.15 30.35-63.83z' fill='url(%23a)'/%3E%3Cpath d='M73 63.93L90.49 27a.1.1 0 0 1 .1-.1c.1-.3 1.5-3.9 1.6-4.1a17 17 0 0 0-1.8-15.9 14.29 14.29 0 0 0-8.9-6.3 16.4 16.4 0 0 0-17.1 5.7c-.1.1-3.9 4.5-3.9 4.5l-28 34.5z' fill='url(%23c)'/%3E%3Cg%3E%3Cpath fill='url(%23d)' d='M23.99 23.93h80v80h-80z'/%3E%3Cpath fill='%23fff' d='M42.79 41.63h-10.8v-7h29.3v7h-10.7v27.59h-7.8V41.63zM61.29 52c0-10 7.4-18.1 18.1-18.1 6.5 0 10.5 2.2 13.7 5.4l-4.9 5.6c-2.7-2.4-5.4-3.9-8.9-3.9-5.8 0-10.1 4.9-10.1 10.8v.1c0 6 4.1 10.9 10.1 10.9 4 0 6.4-1.6 9.1-4.1l4.9 4.9c-3.6 3.8-7.5 6.2-14.2 6.2A17.41 17.41 0 0 1 61.29 52M31.99 87.93h29.3v5.3h-29.3z'/%3E%3C/g%3E%3C/svg%3E%0A">
            <header>
                <div>
                    <span class="tempo-text-color--secondary">Build:</span>
                    <a class="tempo-text-color--link" href="${entity.teamcity.url}">
                        ${entity.teamcity.project}
                    </a>
                    <span class="tempo-text-color--secondary">Status:</span>
                  <span class="tempo-bg-color--${entity.teamcity.statusColor} tempo-text-color--white tempo-token">${entity.teamcity.statusText}</span>
                    
                </div>
            </header>
            <body>
                <div>
                      <p class="tempo-text-color--secondary">Build Number: ${entity.teamcity.build} </p>
                      <hr/>
                      <p>${entity.teamcity.detail}</p>
                        
                        <hr />
                        <p class="tempo-text-color--secondary">TeamCity Symphony Build Reporter <br/>
                        <a href="https://github.com/finos/symphony-java-toolkit/tree/master/symphony-teamcity-integration" />
                  </p>
                </div>
            </body>
        </card>
    </div>
</messageML>