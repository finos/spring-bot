package com.symphony.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

/**
  * Request body for extension app authentication.  Manually generated due to name collision with
  * AuthenticationRequest.
 **/
@Schema(description="Request body for extension app authentication")
public class AppAuthenticateRequest  {
  
  @Schema(description = "application generated token")
 /**
   * application generated token  
  **/
  private String appToken = null;
  

 /**
   * application generated token
   * @return appToken
  **/
  @JsonProperty("appToken")
  public String getAppToken() {
    return appToken;
  }

  public void setAppToken(String appToken) {
    this.appToken = appToken;
  }

  public AppAuthenticateRequest appToken(String appToken) {
    this.appToken = appToken;
    return this;
  }



  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AuthenticateExtensionAppRequest {\n");
    
    sb.append("    appToken: ").append(toIndentedString(appToken)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private static String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
