<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/bbNG" prefix="bbNG"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<bbNG:includedPage ctxId="ctx">
	<bbNG:cssBlock>
		<style type="text/css">
			div.spd {
				background-color: beige;
				text-align: center;
				position: relative;
			}
			.spd label {
				display: inline-block;
				width: 100px;
			}
			img.spdbulb { /*Light Bulb by Ben from the Noun Project 3654*/
				width: 40px;
				height: 40px;
				margin-top: 2px;
			}
			.spd span {
				font-weight: bold;
			}
		</style>
	</bbNG:cssBlock>

	<div class='spd'>
		<a href="${pageContext.request.contextPath}/courseDisplay">
			<c:if test="${!empty error}">
				<p>${error}</p>
			</c:if>
			<c:if test="${empty error}">
				<label class='spdgreen'><img class="spdbulb" src="${pageContext.request.contextPath}/images/light_green.svg"/><br/><span class='spdgreen'>${totals['green']}</span></label>
				<label class='spdyellow'><img class="spdbulb" src="${pageContext.request.contextPath}/images/light_yellow.svg"/><br/><span class='spdyellow'>${totals['yellow']}</span></label>
				<label class='spdred'><img class="spdbulb" src="${pageContext.request.contextPath}/images/light_red.svg"/><br/><span class='spdred'>${totals['red']}</span></label>
			</c:if>
		</a>
	</div>

</bbNG:includedPage>