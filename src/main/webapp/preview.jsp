<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/bbNG" prefix="bbNG"%>

<bbNG:includedPage ctxId="ctx">
	<bbNG:cssBlock>
		<style type="text/css">
			div.spd {
				text-align: center;
				position: relative;
				padding-bottom: 30px;
			}
			label.spdgreen {
				position: absolute;
				top: 0px;
				left: 30%;
			}
			label.spdyellow {
				position: absolute;
				top: 0px;
				display: inline-block;
				margin: 0 auto;
			}
			label.spdred {
				position: absolute;
				top: 0px;
				right: 30%;
			}
			//Light Bulb by Blake Thompson from the Noun Project
			.spdgreen img {
				// nothing
			}
		</style>
	</bbNG:cssBlock>

	<div class='spd'>
		<label class='spdgreen'><img src="noun_9573_cc.svg"/><br/><span class='spdgreen'>${totals['green']}</span></label>
		<label class='spdyellow'><img src="noun_9573_cc.svg"/><br/><span class='spdyellow'>${totals['yellow']}</span></label>
		<label class='spdred'><img src="noun_9573_cc.svg"/><br/><span class='spdred'>${totals['red']}</span></label>
	</div>

</bbNG:includedPage>