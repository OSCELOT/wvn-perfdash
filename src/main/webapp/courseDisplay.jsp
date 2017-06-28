<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/bbNG" prefix="bbNG"%>

<bbNG:genericPage>
	<bbNG:pageHeader instructions="${pageHelp}">
		<bbNG:pageTitleBar title="WVNET Student Performance Dashboard"/>
		<bbNG:cssFile href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css" />
		<bbNG:jsFile href="//code.jquery.com/jquery-1.11.2.js" />
		<bbNG:jsFile href="//code.jquery.com/ui/1.11.2/jquery-ui.js" />
		<bbNG:jsBlock>
			<script type="text/javascript">
				jQuery(document).ready(function() {
					$('.accordion h2').click(function() {
						$(this).next().toggle('slow');
						return false;
					}).next().hide();
					$('.accordion h2').click(function() {
						$(this).find('.coursename').toggle('slow');
						return false;
					}).find('.coursename').hide();
				});
				function expandAll() {
					$(".accordion").find("div").not(".expando").filter(function() {
						return $(this).css('display') == 'none';
					}).toggle("slow");
					$(".accordion").find(".coursename").filter(function() {
						return $(this).css('display') == 'none';
					}).toggle("slow");
				}
				function collapseAll() {
					$(".accordion").find("div").not(".expando").filter(function() {
						return $(this).css('display') == 'block';
					}).toggle("slow");
					$(".accordion").find(".coursename").filter(function() {
						return $(this).css('display') == 'inline-block';
					}).toggle("slow");
				}
			</script>
		</bbNG:jsBlock>
		<bbNG:cssBlock>
			<style type="text/css">
			table.classtable {
				margin-left: 10px;
				margin-bottom: 5px;
				width: 50%;
				font-family: georgia,verdana;
				border-collapse: separate;
				border-spacing: 2px;
				font-size: 16px;
				font-weight: 400;
				white-space: nowrap;
			}
			//tr:nth-child(even) {background: #CCC}
			//tr:nth-child(odd) {background: #FFF}
			table.classtable td, th {
				border: 1px solid black;
			}
			table.classtable th {
				font-weight: bold;
				text-align: center;
				background-color: grey;
				font-size: 75%;
			}
			h2.classheader {
				font-size: 1.67em !important;
			}
			h2.classheader, h2.classaverage {
				margin-top: 5px;
				margin-bottom: 5px;
			}
			a.headerlink {
				color: black;
				text-decoration: none;
			}
			div.accordion {
				margin-bottom: 15px;
				border-style: dashed;
				border-width: 1px;
				background-color: beige;
				position: relative;
			}
			h2.classaverage {
				position: absolute;
				top: 4px;
				right: 0.5%;
				text-shadow: -1px 0 black, 0 1px black, 1px 0 black, 0 -1px black;
				font-size: 190%;
			}
			a.classlink {
				position: absolute;
				top: 40px;
				right: 0.5%;
			}
			.sechead {
				white-space: nowrap;
				overflow: hidden;
				text-overflow: ellipsis;
			}
			
			.expando {
				position: relative;
				width:12px;
				height:12px;
				margin: 16px;
				margin-left: 5px;
			}
			.expando .sechead {
				position: relative;
				top: -7px;
				left: 16px;
				white-space: nowrap;
			}
			.expando-plus {
				background-color: #000;
				border-radius:12px;
				-webkit-border-radius:12px;
				-moz-border-radius:12px;
				width: 12px;
				height: 12px;
				position: relative;
				top:0;
				left:0;
			}
			.expando-plus:after {
				background-color: #fff;
				width: 6px;
				height: 2px;
				border-radius: 1px;
				-webkit-border-radius: 1px;
				-moz-border-radius: 1px;
				position: absolute;
				top: 5px;
				left: 3px;
				content:"";
			}
			.expando-plus:before {
				background-color: #fff;
				width: 2px;
				height: 6px;
				border-radius: 1px;
				-webkit-border-radius: 1px;
				-moz-border-radius: 1px;
				position: absolute;
				top: 3px;
				left: 5px;
				content:"";
			}
			</style>
		</bbNG:cssBlock>
	</bbNG:pageHeader>
	<bbNG:actionControlBar>
		<bbNG:actionButton title="Export" url="?export=true" primary="true"/>
		<bbNG:actionButton title="Expand All" url="javascript:expandAll();"/>		
		<bbNG:actionButton title="Collapse All" url="javascript:collapseAll();"/>
	</bbNG:actionControlBar>

${spdtable}

<bbNG:okButton/>

</bbNG:genericPage>