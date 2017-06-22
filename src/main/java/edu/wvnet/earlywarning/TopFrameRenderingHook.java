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
		return "<!-- Hide WVNET Early Warning System from users without access -->\n" +
		       "<script type='text/javascript' src='"
				+ PlugInUtil.getUriStem("wvn", "earlywarning")
				+ "hide-link.js'></script>";
	}

}
