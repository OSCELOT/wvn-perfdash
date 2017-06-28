package edu.wvnet.perfdash;

import blackboard.platform.plugin.PlugInUtil;
import blackboard.servlet.renderinghook.RenderingHook;

public class TopFrameRenderingHook implements RenderingHook {

	@Override
	public String getKey() {
		return "jsp.topFrame.start";
	}
	
	@Override
	public String getContent() {
		// We have to define checkSPDAccess here, because there's no way to get the URI stem via javascript
		return "<!-- Hide WVNET Student Performance Dashboard from users without access -->\n"
		        + "<script type='text/javascript'>\n"
		        + "function checkSPDAccess(callback) {\n"
		        + "  new Ajax.Request('" + PlugInUtil.getUriStem("wvn", "perfdash") + "checkAccess', {\n"
		        + "    method: 'get', onSuccess: function(transport) { callback(transport) }"
		        + "  });"
				+ "}\n"
				+ "</script>\n"
				+ "<script type='text/javascript' src='"
				+ PlugInUtil.getUriStem("wvn", "perfdash")
				+ "hide-link.js'></script>\n";
	}

}
