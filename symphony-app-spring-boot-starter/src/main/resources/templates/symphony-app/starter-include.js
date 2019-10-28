function init(inModules, outService) {
	const modulesService = inModules['modules'];
	const navService = inModules['applications-nav'];

	const appId = /*[[${@appIdentity.commonName}]]*/ 'testing-app-id';
	const appName = /*[[${@appProperties.name}]]*/ 'My App Name';
	
	// register a new menu item on the left
	navService.add(appId+'-nav', appName, outService.name)


	// register a callback for the menu item

	outService.implement({
	        
	    select: function (id) {
	      if (id === appId+"-nav") {
	        modulesService.show(
	          appId+"-app-panel",
	          { title: appName },
	          outService.name,
	          /*[[${applicationRoot+@appProperties.getAppPath()+'/starter-app-page.html'}]]*/,
	          { 'canFloat': true }
	        )
	      }
	    }
	});	
}
