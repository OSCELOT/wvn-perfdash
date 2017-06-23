function hideEWSLink() {
	var modules = document.getElementsByClassName("moduleTitle");
	for (var i = 0; i < modules.length; i++) if(modules[i].innerHTML == "Tools") var tools = modules[i];
	if(tools) {
		tools = tools.parentElement.parentElement.getElementsByClassName("portletList");
		tools[0].childElements().forEach(
			function (item) { if(item.innerHTML.includes("wvn-earlywarning")) item.remove() }
		)
	}
}

Event.observe(document,"dom:loaded", function() {
	if(document.getElementsByClassName('active')[0].innerHTML.includes('My Blackboard')) {
		checkEWSAccess(function(obj) {
			if(obj.responseText == "0") {
			    hideEWSLink();
			}
		});
	}
});