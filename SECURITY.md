@Component
public class SomeComponent {


   @Autowire
   MessagesApi api;  


	public void postMessage(String s) throws Exception {
		messagesApi.v4StreamSidMessageCreatePost(null, streamId, "<messageML>"+s+"</messageML>" , null, null, null, null, null);
	}
}public interface ApiInstanceFactory {

	public ApiInstance createApiInstance(SymphonyIdentity id, PodProperties pp, TrustManager[] trustManagers) throws Exception;
	
}	SystemApi systemApi = apiInstance.getAgentApi(SystemApi.class);
	api.v2HealthCheckGet(null, null);1048576013292236800UP {diskSpace=UP {total=75159826432, free=13292236800, threshold=10485760}, symphony-api-<hostname>-<podid>=UP {podConnectivity=true, keyManagerConnectivity=true, encryptDecryptSuccess=true, agentServiceUser=true, ceServiceUser=false, podConnectivityError=null, keyManagerConnectivityError=null, encryptDecryptError=null, podVersion=1.54.2, agentVersion=2.54.3, agentServiceUserError=null, ceServiceUserError=Ceservice authentication credentials missing or misconfigured}}75159826432demos/demo-bot# Security Policy

## Supported Versions

Use this section to tell people about which versions of your project are
currently being supported with security updates.

| Version | Supported          |
| ------- | ------------------ |
| 10.x.x   | :white_check_mark: |
| 9.x.x   | :white_check_mark: |
| 8.x.x   | :x: |
| < 8.0.0 | :x:                |

## Reporting a Vulnerability

If you uncover vulnerabilities in this software, please contact `help@finos.org` privately.  We aim to respond within 3 working days.

## Whitesource

All project maintainers should be able to view vulnerabilities on `mend.io`.  If you can't access this, please contact [FINOS Help](mailto:help@finos.org).
