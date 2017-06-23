package edu.wvnet.earlywarning;

import blackboard.platform.plugin.PlugInUtil;
import blackboard.servlet.renderinghook.RenderingHook;

public class TopFrameRenderingHook implements RenderingHook {

	@Override
	public String getKey() {
		return "jsp.topFrame.start";
	}
	
	@Override
	public String getContent() {		
		return "<!-- Hide WVNET Early Warning System from users without access -->\n"
		        + "<script type='text/javascript'>\n"
		        + "function checkEWSAccess(callback) {\n"
		        + "  new Ajax.Request('" + PlugInUtil.getUriStem("wvn", "earlywarning") + "checkAccess', {\n"
		        + "    method: 'get', onSuccess: function(transport) { callback(transport) }"
		        + "  });"
				+ "}\n"
				+ "</script>\n"
				+ "<script type='text/javascript' src='"
				+ PlugInUtil.getUriStem("wvn", "earlywarning")
				+ "hide-link.js'></script>\n";
	}

}
