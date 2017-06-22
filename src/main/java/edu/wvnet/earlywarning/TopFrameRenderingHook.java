package edu.wvnet.earlywarning;

import blackboard.servlet.renderinghook.RenderingHook;

public class TopFrameRenderingHook implements RenderingHook {

	@Override
	public String getKey() {
		return "jsp.topFrame.start";
	}
	
	@Override
	public String getContent() {
		//return "<script type='text/javascript' src='/webapps/wvn-earlywarning-BBTSTLRN/renderinghook.js'></script>";
		return "hey hey here is my hook lol";
	}

}
