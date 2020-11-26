<%@ include file="/include.jsp" %>

<script type="text/javascript">
  function saveSettings(form) {
     $('spin').show();
     BS.ajaxRequest($('saveSettingsForm').action, {
        parameters: $('saveSettingsForm').serialize(true),
        onComplete: function(transport) {
          if (transport.getStatus() == 206) {
            alert("problem: "+transport.responseText);
          } else {
            alert("Connection settings tested and working");
          }
          $('saveSettingsContainer').refresh();
          $('spin').hide();
        }
     });
  }
</script>

<bs:refreshable containerId="saveSettingsContainer" pageUrl="${pageUrl}">
  <c:url var="actionUrl" value="/saveSettings.html"/>
  <form action="${actionUrl}" id="saveSettingsForm" method="POST">
  
    <h3>Bot Details</h3>
    <div class="grayNote">Please create a symphony bot that TeamCity can use to send messages</div>
  
    <table class="parametersTable">
      <tr>
        <th><label for="identityProperties.email">Bot Email Address:</label></th>
        <td><input type="text" name="identityProperties.email" value="${identityProperties.email}" class="textField" /></td>
      </tr>
      <tr>
        <th><label for="identityProperties.commonName">Bot Common Name:</label></th>
        <td><input type="text" name="identityProperties.commonName" value="${identityProperties.commonName}" class="textField" /></td>
      </tr>
      <tr>
        <th><label for="identityProperties.privateKey">Bot Private Key (PEM):<span class="mandatoryAsterix" title="Mandatory field">*</span></label></th>
        <td><textarea name="identityProperties.privateKey">${identityProperties.privateKey}</textarea></td>
      </tr>
      <tr>
        <th><label for="certificates">Bot Certificate Chain (PEMs):</label>
         <div class="grayNote">If supplying multiple certificates, leave a blank line between each one</div>
         </th>
        <td><textarea name="certificates">${certificates}</textarea></td>
      </tr>
    </table>
    
    <h3>Pod Details</h3>
    <div class="grayNote">This is the pod that will receive the bot's build notifications.    
    </div>
    
      
    <table class="parametersTable">
      <!-- pod -->
      <c:forEach var="ep" items="${endpoints}">
      <tr>
        <th colspan="3"><h4>${ep} Endpoint</h4>
        </th>
      </tr>
      <tr>
        <td><label for="podProperties.${ep}.url">Endpoint (URL)<span class="mandatoryAsterix" title="Mandatory field">*</span></label>
        <div class="grayNote">e.g. http://develop.symphony.com/${ep}</div>
        </td>
        <td colspan="2"><input type="text" name="podProperties.${ep}.url" value="${podProperties[ep].url}" class="textField" /></td>
      </tr>
      <tr>
        <td rowspan="4">Proxy</td>
        <td><label for="podProperties.${ep}.proxy.host">Host</label></td>
        <td><input type="text" name="podProperties.${ep}.proxy.host" value="${podProperties[ep].proxy.host}" class="textField" /></td>
      </tr>
      <tr>
        <td><label for="podProperties.${ep}.proxy.port">Port</label></td>
        <td><input type="number" name="podProperties.${ep}.proxy.port" value="${podProperties[ep].proxy.port}" class="textField" /></td>
      </tr>
      <tr>
        <td><label for="podProperties.${ep}.proxy.user">User</label></td>
        <td><input type="text" name="podProperties.${ep}.proxy.user" value="${podProperties[ep].proxy.user}" class="textField" /></td>
      </tr>
      <tr>
        <td><label for="podProperties.${ep}.proxy.password">Password</label></td>
        <td><input type="password" name="podProperties.${ep}.proxy.password" value="${podProperties[ep].proxy.password}" class="textField" /></td> 
      </tr>
      </c:forEach>
    </table>  
      
    <h3>Override Template</h3>
    <div class="grayNote">Override the main symphony template here (Apache Freemarker format)</div>
    <table class="parametersTable">
      <tr>
        <th>Template</th>
      </tr>
      <tr>
        <td><textarea name="template">${template}</textarea></td>
      </tr>
    </table>
    
    
    <h3>Trust Store Details</h3>
    <div class="grayNote">A list of PEMs to be used as a trust store.  Leave empty to use the default</div>
     <table class="parametersTable">
      <tr>
        <th><label for="certificates">Trusted Certificates (PEMs):</label>
         <div class="grayNote">If supplying multiple certificates, leave a blank line between each one</div>
         </th>
        <td><textarea name="certificates">${trustedPems}</textarea></td>
      </tr>
    </table>
    
    
    <input type="button" value="Save" class="btn btn_primary submitButton" onClick="saveSettings(this.form)" />
    
    <i id="spin" style="display: none" class="ring-loader-inline progressRing progressRingInline" title="Please wait..."></i>
    
  </form>
</bs:refreshable>