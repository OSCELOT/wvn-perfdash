package edu.wvnet.earlywarning;

import blackboard.servlet.renderinghook.RenderingHook;

public class EWSRenderingHook implements RenderingHook {

	@Override
	public String getContent() {
		return "jsp.topFrame.start";
	}

	@Override
	public String getKey() {
		//return "<script type='text/javascript' src='/webapps/wvn-earlywarning-BBTSTLRN/renderinghook.js'></script>";
		return "hey hey hey here is my hook lol";
	}

}
