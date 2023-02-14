/*
*	程式設計師:???
*	程式名稱:mops2.js(utf-8)
*	程式功能:T88K 新版mops所需之javascript, T88G 測試機()
*	修改紀錄:(格式=>西元日期+版次+程式設計師)
	2022112121max:(T88K)修改文字 永續發展債券之投資計畫書/外部評估報告/資金運用情形公告 => 永續發展債券之計畫書/評估報告/發行後報告
	2022080820bristol:(T88K)0808修改 t127sb00_q1 t127sb00_q2 標題 普通公司債暨金融債券公告 => 各項公告查詢作業
							0805修改 t198sb04_q1 債券到期前餘額變動各項公告查詢作業 => 債券到期前變動各項公告查詢作業
							0729修正 cookie問題
	2022060718max:(T88K)加回 t214sb01,t214sb02
	2022060717max:(T88K)先移除 t214sb01,t214sb02
	2022060716max:(T88K)先移除 t214sb01,t214sb02
	2022053015max:(T88K)增加 t214sb01,t214sb02
	2022030714max:(T88K)增加 t213sb01
	2022011013max:(T88K)修改弱掃問題
	2022010512max:(T88K)修改弱掃與增加t57sb01_q8,t57sb01_q9
	2021122911max:(T88K)重上 (複製mops1)
	2021122410max:(T88K)新增 (複製mops1)
*/


//Google Analytics
(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
})(window,document,'script','//www.google-analytics.com/analytics.js','ga');

ga('create', 'UA-68490422-1', 'auto');
ga('send', 'pageview');

//增加監聽事件
function addListener(object,func,act){
	if (window.attachEvent)
		object.attachEvent("on"+func,act);
	else
		object.addEventListener(func,act,false);
}

var exp = new Date();
exp.setTime(exp.getTime()+(24*60*60*1000));

/// steven 20220729 改用session 紀錄公司代號(因為cookie不能用) start
// function setCookie(name, value, expires) {
// 	if (document.cookie.replace(/(?:(?:^|.*;\s*)_gid\s*\=\s*([^;]*).*$)|^.*$/, '$1')) {
// 		document.cookie =
// 			name +
// 			'=' +
// 			escape(value) +
// 			'; path=/' +
// 			(expires == null ? '' : ';expires=' + expires.toGMTString()) +
// 			';';
// 	}
// }

function setSession(name, value, expires) {
	sessionStorage.setItem(name, encodeURI(value) + '; path=/' + (expires == null ? '' : ';'));
}
// function getCookie(name) {
// 	var cname = name + "=";
// 	var dc = document.cookie;
// 	if (dc.length > 0) {
// 		begin = dc.indexOf(cname);
// 		if (begin != -1) {
// 			begin += cname.length;
// 			end = dc.indexOf(";", begin);
// 			if (end == -1) {
// 				end = dc.length;
// 			}
// 			return unescape(dc.substring(begin, end));
// 		}
// 	}

// 	return null;
//  }

// function getCookie(name) {
// 	if (document.cookie.replace(/(?:(?:^|.*;\s*)newmops2\s*\=\s*([^;]*).*$)|^.*$/, '$1')) {
// 		var cname = name + '=';
// 		var dc = document.cookie;

// 		if (dc.length > 0) {
// 			begin = dc.indexOf(cname);

// 			if (begin != -1) {
// 				begin += cname.length;
// 				end = dc.indexOf(';', begin);
// 				if (end == -1) {
// 					end = dc.length;
// 				}

// 				return decodeURI(dc.substring(begin, end));
// 			}
// 		}

// 		return null;
// 	}
// }
function getSession(name) {
	var cname = name + '=';
	var se = window.sessionStorage;
	if (se.length > 0) {
		var se = cname + window.sessionStorage.getItem(name);
	}

	if (se.length > 0) {
		begin = se.indexOf(cname);
		if (begin != -1) {
			begin += cname.length;
			end = se.indexOf(';', begin);
			if (end == -1) {
				end = se.length;
			}
			return decodeURI(se.substring(begin, end));
		}
	}
	return null;
}

// function delCookie(name) {
// 	document.cookie = name + '=; expires=Thu, 01-Jan-70 00:00:01 GMT' + '; path=/' + ';';
// }

function delSession(name) {
	window.sessionStorage.removeItem(name);
}
function getRadioValue(ns1) {
	if (ns1 != null) {
		for (var i = 0; i < ns1.length; i++) {
			if (ns1[i].checked == true) return ns1[i].value;
			//alert(ns1[i].value);
		}
	}
	return null;
}

// function setChip(cookie, name, value) {
// 	//	if(name=='TYPEK1') alert('--- set chip '+name+' = '+value);
// 	var dc = getCookie(cookie);
// 	var cv = getChip(cookie, name);
// 	var cn = name + '=';
// 	if (cv != null) {
// 		var start = dc.indexOf(cn);
// 		if (start != -1) {
// 			var end = dc.indexOf('|', start);
// 			setCookie(cookie, dc.substring(0, start) + cn + value + '|' + dc.substring(end + 1, dc.length), exp);
// 		}
// 	} else {
// 		if (dc != null) {
// 			dc += cn + value + '|';
// 		} else {
// 			dc = cn + value + '|';
// 		}
// 		setCookie(cookie, dc, exp);
// 	}
// }
function setChip(Session, name, value) {
	var se = getSession(Session);
	var cv = getChip(Session, name);
	var cn = name + '=';
	if (cv != null) {
		var start = se.indexOf(cn);
		if (start != -1) {
			var end = se.indexOf('|', start);
			setSession(Session, se.substring(0, start) + cn + value + '|' + se.substring(end + 1, se.length), exp);
		}
	} else {
		if (se != null) {
			se += cn + value + '|';
		} else {
			se = cn + value + '|';
		}
		setSession(Session, se, exp);
	}
}
// setChip('cookieName', 'ChipName', 'ChipValue');

// function getChip(cookie, name) {
// 	var cn = name + '=';
// 	var dc = getCookie(cookie);

// 	if (dc != null) {
// 		var start = dc.indexOf(cn);
// 		if (start != -1) {
// 			start += cn.length;
// 			var end = dc.indexOf('|', start);
// 			if (end != -1) {
// 				return unescape(dc.substring(start, end));
// 			}
// 		}
// 	}
// 	return null;
// }
setChip('SessionName', 'ChipName', 'ChipValue');

function getChip(session, name) {
	var cn = name + '=';
	var dc = getSession(session);

	if (dc != null) {
		var start = dc.indexOf(cn);
		if (start != -1) {
			start += cn.length;
			var end = dc.indexOf('|', start);
			if (end != -1) {
				return unescape(dc.substring(start, end));
			}
		}
	}
	return null;
}
// getChip('CookieName', 'ChipName');

// function delChip(cookie, name) {
// 	var dc = getCookie(cookie);
// 	var cv = getChip(cookie, name);
// 	var cn = name + '=';
// 	if (cv != null) {
// 		var start = dc.indexOf(cn);
// 		var end = dc.indexOf('|', start);
// 		setCookie(cookie, dc.substring(0, start) + dc.substring(end + 1, dc.length), exp);
// 	}
// }
// delChip('CookieName', 'ChipName');
getChip('SessionName', 'ChipName');

function delChip(session, name) {
	var dc = getSession(session);
	var cv = getChip(session, name);
	var cn = name + '=';
	if (cv != null) {
		var start = dc.indexOf(cn);
		var end = dc.indexOf('|', start);
		setSession(session, dc.substring(0, start) + dc.substring(end + 1, dc.length), exp);
	}
}
delChip('SessionName', 'ChipName');
// steven 20220729 End


function doZoom(size){
	var zoom = document.getElementById("zoom");
	if (zoom != null){
		document.getElementById("zoom").className = size;
	}
	
/*
	document.getElementById("fontSize1").src = "images/txt_b02_1.gif";
	document.getElementById("fontSize2").src = "images/txt_b02_2.gif";
	document.getElementById("fontSize3").src = "images/txt_b02_3.gif";
	document.getElementById("fontSize4").src = "images/txt_b02_4.gif";

	if (size=="fontSize1"){
		document.getElementById("fontSize1").src = "images/txt_b02_1s.gif";
	}
	if (size=="fontSize2"){
		document.getElementById("fontSize2").src = "images/txt_b02_2s.gif";
	}
	if (size=="fontSize3"){
		document.getElementById("fontSize3").src = "images/txt_b02_3s.gif";
	}
	if (size=="fontSize4"){
		document.getElementById("fontSize4").src = "images/txt_b02_4s.gif";
	}
*/
}

function showDetail() { 
	var bgObj=document.getElementById("bgDiv"); 
	bgObj.style.width = document.body.offsetWidth + "px";  
	bgObj.style.height = screen.height + "px"; 

	var msgObj=document.getElementById("msgDiv"); 
	msgObj.style.marginTop = -30 +  document.documentElement.scrollTop + "px"; 

	document.getElementById("msgShut").onclick = function(){ 
	bgObj.style.display = msgObj.style.display = "none"; 
	} 
	msgObj.style.display = bgObj.style.display = "block"; 
	msgDetail.style.display = "block" ; 
}

function closex(){
	if(document.getElementById('main').style.display =='inline'){
		document.getElementById('main').style.display ='none';
		document.getElementById('pic').src = 'images/pic24.gif';
//		document.getElementById('nav02').style.width = "985px";
	}else{
		document.getElementById('main').style.display ='inline';
		document.getElementById('pic').src = 'images/pic23.gif';
//		document.getElementById('nav02').style.width = "800px";
	}
}

function showhide(spanID,imgID){
	var what = document.getElementById(spanID);
	var what2 = document.getElementById(imgID);
	if (what.style.display=='none'){
		what.style.display='';
		what2.src='images/plus3.gif';
		what2.Open=""
	}
	else{
		what.style.display='none'
		what2.src='images/minus3.gif';
		what2.Closed=""
	}
}
/*steven 20220107 writeln問題 修改 start */
//20160315 EDWARD ALICE begin:增加彈跳視窗


function openWindow(form1,specs,title) {

	var input = form1.querySelectorAll('input');
	var action = form1.action;
	var date = new Date();
	var formName = "form" + date.getDate() + date.getTime(); // var str1 = "<html>"
	//  + "<head>"
	//  + "<script type=\"text/javascript\" src=\"js/mops.js\"></script>"
	//  + "<link href=\"css/css.css\" rel=\"stylesheet\" type=\"text/css\" />"
	//  + "</head>"
	//  + "<body style='background-color:white !important;'>"
	//  + "<div id='nav02'>"
	//  + "<div class='t01' id='caption'>"
	//  + caption_str
	//  + "</div>"
	//  + "<div id='zoom'><div id='table01'>"
	//  + "<form id='"+formName+"' name='autoRunScript' method='post' action='" + action + "'>"
	//  + parameter
	//  + "</form>"
	//  + "<script type='text/javascript'>"
	//  + "setTimeout(function(){ ajax1(document.getElementById('"+formName+"') , 'table01'); } , 1000);"
	//  + "</script>"
	//  + "</div></div>"
	//  + "</div>"
	//  + "</body>"
	//  + "</html>"
	// var win = window.open("" , "newWin"+date.getDate()+date.getTime() , specs);
	// win.document.writeln(str1);
  
	/*--*/
  
	if (specs.length == 0) {
	  specs = "toolbar=no, scrollbars=yes, resizable=yes, location=no, menubar=no, status=no, width=800, height=500";
	}
  
	var date = new Date();
	var win = window.open("", "newWin" + date.getDate() + date.getTime(), specs);
	win.document.write('<!DOCTYPE html><html><head></head><body></body></html>');
	var script = win.document.createElement("script");
	script.setAttribute("type","text/javascript")
	var lochref = document.location.href;
	var locdns = "";
  
	if (lochref.indexOf("http://") == 0) {
	  var x = lochref.indexOf("/", 7);
	  if (x == -1) x = lochref.length;
	  locdns = lochref.substring(7, x);
	  script.setAttribute("src", "http://" + locdns + "/mops/web/js/mops2.js");
	}
  
	if (lochref.indexOf("https://") == 0) {
	  var x = lochref.indexOf("/", 8);
	  if (x == -1) x = lochref.length;
	  locdns = lochref.substring(8, x);
	  script.setAttribute("src", "https://" + locdns + "/mops/web/js/mops2.js");
	}
	win.document.querySelector("head").appendChild(script);
  //   win.document.head.appendChild(script);
	var link = win.document.createElement("link");
	var locdns = "";
  
	if (lochref.indexOf("http://") == 0) {
	  var x = lochref.indexOf("/", 7);
	  if (x == -1) x = lochref.length;
	  locdns = lochref.substring(7, x);
	  link.setAttribute("href", "http://" + locdns + "/mops/web/css/css.css");
	}
  
	if (lochref.indexOf("https://") == 0) {
	  var x = lochref.indexOf("/", 8);
	  if (x == -1) x = lochref.length;
	  locdns = lochref.substring(8, x);
	  link.setAttribute("href", "https://" + locdns + "/mops/web/css/css.css");
	}
  
	var caption_str = "";
	var caption = document.getElementById("caption");
  
	if (caption != null && caption != undefined) {
	  caption_str = caption.innerHTML;
	}
  
	if (title != null) {
	  caption_str = title;
	}
  
	link.rel = "stylesheet";
	win.document.head.appendChild(link);
	win.document.body.setAttribute("style", "background-color:white !important;");
	var nav02 = win.document.createElement("div");
	nav02.setAttribute("id", "nav02");
	win.document.body.appendChild(nav02);
	var caption = win.document.createElement("div");
	caption.setAttribute("id", "caption");
	caption.setAttribute("class", "t01");
	caption.innerHTML = caption_str;
	nav02.appendChild(caption); // var caption = document.querySelector("#caption").cloneNode(true);
	// if (document.querySelector("#caption") && document.querySelector("#caption").innerHTML != "") {
	// 	nav02.appendChild(caption);
	// };
  
	var zoom = win.document.createElement("div");
	zoom.setAttribute("id", "zoom");
	var table01 = win.document.createElement("div");
	table01.setAttribute("id", "table01");
	zoom.appendChild(table01);
	var form01 = win.document.createElement("form");
	form01.setAttribute("id",formName);
	form01.setAttribute("name",'autoRunScript');
	form01.setAttribute("method",'post');
	form01.setAttribute("action",action);
  
	/*---*/
  
	for (var i = 0; i < input.length; i++) {
	  var parameter = win.document.createElement("input");
	  var itype = input[i].type;
	  var iname = input[i].name;
	  var ivalue = input[i].value;
  
	  if (itype == 'button') {
		continue;
	  }
  
	  parameter.type = 'hidden'; // parameter += "<input type='hidden' name='" + iname + "' value='" + ivalue + "'/>";
  
	  parameter.setAttribute("name", iname);
	  parameter.setAttribute("value", ivalue);
	  form01.appendChild(parameter);
	}
  
	var select = form1.getElementsByTagName('select');
  
	for (var i = 0; i < select.length; i++) {
	  var parameter = win.document.createElement("input");
	  var itype = select[i].type;
	  var iname = select[i].name;
	  var ivalue = select[i].value;
  
	  if (itype == 'button') {
		continue;
	  }
	  parameter.setAttribute("type","hidden");
	  // parameter.type = 'hidden'; // parameter += "<input type='hidden' name='" + iname + "' value='" + ivalue + "'/>";
	  parameter.setAttribute("name",iname);
	  // parameter.name = iname;
	  parameter.setAttribute("value","" + ivalue + "");
	  // parameter.value = "" + ivalue + "";
	  form01.appendChild(parameter);
	}
	/*---*/
  
  
	table01.appendChild(form01);
	var bodyscript = win.document.createElement("script");
	bodyscript.type = "text/javascript"; // bodyscript.innerHTML = "setTimeout(function(){ ajax1(document.getElementById("+ ""+formName+"" +") , 'table01'); } , 1000);"
  
  //   bodyscript.innerHTML = "setTimeout(function(){ ajax1(document.querySelector('#" + formName + "') , 'table01'); } , 1000);";
	bodyscript.text = "setTimeout(function(){ ajax1(document.querySelector('#"+formName+"') , 'table01'); } , 1000);";
	table01.appendChild(bodyscript);
	nav02.appendChild(zoom);
}
  
function openWindowAction(form1, specs) {
	var input = form1.getElementsByTagName('input');
	var action = form1.action; // var parameter = '';
  
	var date = new Date();
	var formName = "form" + date.getDate() + date.getTime(); // var str1 = "<html>"
	//  + "<head>"
	//  + "<script type=\"text/javascript\" src=\"/mops.js\"></script>"
	//  + "<link href=\"/style.css\" rel=\"stylesheet\" type=\"text/css\" />"
	//  + "</head>"
	//  + "<body style='background-color:white !important;'>"
	//  + "<form id='"+formName+"' name='"+formName+"' method='post' action='" + action + "'>"
	//  + parameter
	//  + "</form>"
	//  + "<script type='text/javascript'>"
	//  + "document."+formName+".submit();"
	//  + "</script>"
	//  + "</div></div>"
	//  + "</div>"
	//  + "</body>"
	//  + "</html>"
	// var win = window.open("" , "newWin"+date.getDate()+date.getTime() , specs);
	// win.document.writeln(str1);
  
	/**-- */
  
	if (specs.length == 0) {
	  specs = "toolbar=no, scrollbars=yes, resizable=yes, location=no, menubar=no, status=no, width=865, height=500";
	}
	var win = window.open("about:blank", "newWin" + date.getDate() + date.getTime(), specs);
	var script = win.document.createElement("script");
	script.setAttribute("type","text/javascript");
	var lochref = document.location.href;
	var locdns = "";
  
	if (lochref.indexOf("http://") == 0) {
	  var x = lochref.indexOf("/", 7);
	  if (x == -1) x = lochref.length;
	  locdns = lochref.substring(7, x);
	  script.setAttribute("src", "http://" + locdns + "/mops/web/js/mops2.js");
	}
  
	if (lochref.indexOf("https://") == 0) {
	  var x = lochref.indexOf("/", 8);
	  if (x == -1) x = lochref.length;
	  locdns = lochref.substring(8, x);
	  script.setAttribute("src", "https://" + locdns + "/mops/web/js/mops2.js");
	}
  
	var link = win.document.createElement("link");
	// link.href = code + "/mops/web/css/style.css";

	if (lochref.indexOf("http://") == 0) {
		var x = lochref.indexOf("/", 7);
		if (x == -1) x = lochref.length;
		locdns = lochref.substring(7, x);
		link.setAttribute("href", "http://" + locdns + "/mops/web/js/mops2.js");
	  }
	
	  if (lochref.indexOf("https://") == 0) {
		var x = lochref.indexOf("/", 8);
		if (x == -1) x = lochref.length;
		locdns = lochref.substring(8, x);
		link.setAttribute("href", "https://" + locdns + "/mops/web/js/mops2.js");
	  }



	link.rel = "stylesheet";
	link.type = "text/css";
	win.document.head.appendChild(link);
	var form01 = win.document.createElement("form");


	form01.setAttribute("id",formName)
	form01.setAttribute("name",formName)
	form01.setAttribute("method",'post')
	form01.setAttribute("action","" + action + "")

	for (var i = 0; i < input.length; i++) {
	  var parameter = win.document.createElement("input");
	  var itype = input[i].type;
	  var iname = input[i].name;
	  var ivalue = input[i].value;
  
	  if (itype == 'button') {
		continue;
	  }
  
	  parameter.type = 'hidden'; // parameter += "<input type='hidden' name='" + iname + "' value='" + ivalue + "'/>";
  
	  parameter.setAttribute("name", iname);
	  parameter.setAttribute("value", ivalue);
	  form01.appendChild(parameter);
	}
  
	var select = form1.getElementsByTagName('select');
  
	for (var i = 0; i < select.length; i++) {
	  var parameter = document.createElement("input");
	  var itype = select[i].type;
	  var iname = select[i].name;
	  var ivalue = select[i].value;
  
	  if (itype == 'button') {
		continue;
	  }
  
	  parameter.type = 'hidden'; // parameter += "<input type='hidden' name='" + iname + "' value='" + ivalue + "'/>";
  
	  parameter.name = iname;
	  parameter.value = "" + ivalue + "";
	  form01.appendChild(parameter);
	}
  
	var bodyscript = win.document.createElement("script");
	bodyscript.setAttribute("type",  "text/javascript");
	bodyscript.text = "document." + formName + ".submit();";

	win.document.head.appendChild(script);
	win.document.body.appendChild(form01);
	win.document.body.appendChild(bodyscript);
  
  
   
  
} //20160315 EDWARD ALICE end:增加彈跳視窗


  
function MM_openBrWindow(theURL, winName, features) {
	//v2.0
	window.open(theURL, winName, features);
}
  
function outputNewWindow() {
	if (document.getElementById("table01").innerHTML==""){
		return;
	}
	var str2="";
	var table01 = document.getElementById("table01");
	if (table01!=null && table01!=undefined){
		str2 = table01.innerHTML;
	}
	var caption_str="";
	var caption = document.getElementById("caption");
	if (caption!=null && caption!=undefined){
		caption_str = caption.innerHTML;
	}
	// var str1="<html>";
	// str1+="<head>";
	// str1+="<script type=\"text/javascript\" src=\"js/mops.js\"></script>";
	// str1+="<link href=\"css/css.css\" rel=\"stylesheet\" type=\"text/css\" />";
	//	str1+="<link href=\"css/tag.css\" rel=\"stylesheet\" type=\"text/css\" />";
	//	str1+="<link href=\"css/tablea.css\" rel=\"stylesheet\" type=\"text/css\" />";
	// str1+="</head>";
	// str1+="<body style='background-color:white !important;'>";
	// str1+="<div id='nav02'>";
	// str1+="<div class='t01' id='caption'>";
	// str1+=caption_str;
	// str1+="</div>";
	// str1+="<div id='zoom'><div id='table01'>";
	// str1+=str2;
	// str1+="</div></div>";
	// str1+="</div>";
	// str1+="</body>";
	// str1+="</html>";
	// str1+="";
	var date = new Date();
	var win = window.open("", "newWin" + date.getDate() + date.getTime());
	win.document.write('<!DOCTYPE html><html><head></head><body></body></html>');
	var script = win.document.createElement("script");
	script.setAttribute("type","text/javascript")
	var lochref = document.location.href;
	var locdns = "";
  
	if (lochref.indexOf("http://") == 0) {
	  var x = lochref.indexOf("/", 7);
	  if (x == -1) x = lochref.length;
	  locdns = lochref.substring(7, x);
	  script.setAttribute("src", "http://" + locdns + "/mops/web/js/mop2.js");
	}
  
	if (lochref.indexOf("https://") == 0) {
	  var x = lochref.indexOf("/", 8);
	  if (x == -1) x = lochref.length;
	  locdns = lochref.substring(8, x);
	  script.setAttribute("src", "https://" + locdns + "/mops/web/js/mops2.js");
	}
  
	win.document.head.appendChild(script);
	var link = win.document.createElement("link");
	if (lochref.indexOf("http://") == 0) {
	  var x = lochref.indexOf("/", 7);
	  if (x == -1) x = lochref.length;
	  locdns = lochref.substring(7, x);
	  link.setAttribute("href", "http://" + locdns + "/mops/web/css/css.css");
	}
  
	if (lochref.indexOf("https://") == 0) {
	  var x = lochref.indexOf("/", 8);
	  if (x == -1) x = lochref.length;
	  locdns = lochref.substring(8, x);
	  link.setAttribute("href", "https://" + locdns + "/mops/web/css/css.css");
	}
	link.rel = "stylesheet";
	win.document.head.appendChild(link);
	var nav02 = win.document.createElement("div");
	nav02.setAttribute("id", "nav02");
	win.document.body.appendChild(nav02);
  //   var str2 = document.querySelector("#table01").cloneNode(true);
  //   var caption = document.querySelector("#caption").cloneNode(true);
  var caption = win.document.createElement("div");
  caption.setAttribute("id", "caption");
  caption.setAttribute("class", "t01");
  caption.innerHTML = caption_str;
  nav02.appendChild(caption);
  
  var zoom = win.document.createElement("div");
  zoom.setAttribute("id", "zoom");
  var table01 = win.document.createElement("div");
  table01.setAttribute("id", "table01");
  zoom.appendChild(table01);
  table01.innerHTML = str2;
  nav02.appendChild(zoom);
  //   if (document.querySelector("#caption") && document.querySelector("#caption").innerHTML != "") {
  //     nav02.appendChild(caption);
  //   }
  
  //   ;
  
  //   if (document.querySelector("#table01") && document.querySelector("#table01").innerHTML != "") {
  //     nav02.appendChild(str2);
  //   }
  
	; //	win.document.body.innerHTML=str1;
	// win.document.writeln(str1);
	//	win.document.close();
	//	win.focus();
  
	/*
		var win = window.open("","newWin");
		win.document.writeln("<html>");
		win.document.writeln("<head>");
		win.document.writeln("<script type=\"text/javascript\" src=\"js/mops.js\"></script>");
		win.document.writeln("<link href=\"css/css.css\" rel=\"stylesheet\" type=\"text/css\" />");
		win.document.writeln("<link href=\"css/tag.css\" rel=\"stylesheet\" type=\"text/css\" />");
		win.document.writeln("<link href=\"css/tablea.css\" rel=\"stylesheet\" type=\"text/css\" />");
		win.document.writeln("</head>");
		win.document.writeln("<body style='background-color:white !important;'>");
		win.document.writeln("<div id='nav02'>");
		win.document.writeln("<div class='t01' id='caption'>");
		win.document.writeln("</div>");
		win.document.writeln("<div id='zoom'><div id='table01'>");
		win.document.writeln("</div></div>");
		win.document.writeln("</div>");
		win.document.writeln("</body>");
		win.document.writeln("</html>");
	
		//var caption = document.getElementById("caption");
		//if (caption!=null && caption!=undefined){
		//	win.document.getElementById("caption").innerHTML = caption.innerHTML;
		//}
		var table01 = document.getElementById("table01");
		if (table01!=null && table01!=undefined){
			win.document.getElementById("table01").innerHTML = table01.innerHTML;
		}
		win.document.close();
	*/
}
  
function outputNewWindowPrint() {
	if (document.getElementById("table01").innerHTML==""){
		return;
	}
	var str2="";
	var table01 = document.getElementById("table01");
	if (table01!=null && table01!=undefined){
		str2 = table01.innerHTML;
	}
	var caption_str="";
	var caption = document.getElementById("caption");
	if (caption!=null && caption!=undefined){
		caption_str = caption.innerHTML;
	}
	// var str1 = "<html>";
	// str1 += "<head>";
	//	str1+="<script type=\"text/javascript\" src=\"js/mops.js\"></script>";
	// str1 += "<link href=\"css/css.css\" rel=\"stylesheet\" type=\"text/css\" />";
	// str1 += "<link href=\"css/tag.css\" rel=\"stylesheet\" type=\"text/css\" />";
	// str1 += "<link href=\"css/tablea.css\" rel=\"stylesheet\" type=\"text/css\" />";
	// str1 += "</head>";
	// str1+="<body style='background-color:white !important;'>";
	// str1+="<div id='nav02'>";
	// str1+="<div class='t01' id='caption'>";
	// str1+=caption_str;
	// str1+="</div>";
	// str1+="<div id='zoom'><div id='table01'>";
	// str1+=str2;
	// str1+="</div></div>";
	// str1+="</div>";
	// str1+="</body>";
	// str1+="</html>";
	// str1+="";
	// var win = window.open("","newWin");
	// win.document.writeln(str1);
	var win = window.open("", "newWin");
	var link01 = win.document.createElement("link");
	var code = location.hash.substr(location.hash.indexOf("code=") + 5);
	link01.href = code + "/mops/web/css/css.css";
	link01.rel = "stylesheet";
	win.document.body.appendChild(link01);
	var link02 = win.document.createElement("link");
	link02.href = code + "/mops/web/css/tag.css";
	link02.rel = "stylesheet";
	win.document.body.appendChild(link02);
	var link03 = win.document.createElement("link");
	link03.href = code + "/mops/web/css/tablea.css";
	link03.rel = "stylesheet";
	win.document.body.appendChild(link03);
	var nav02 = win.document.createElement("div");
	nav02.setAttribute("id", "nav02");
	win.document.body.appendChild(nav02);

	// var str2 =document.querySelector("#table01").cloneNode(true);
	
  

	var caption = win.document.createElement("div");
	caption.setAttribute("id", "caption");
	caption.setAttribute("class", "t01");
	caption.innerHTML = caption_str;
	nav02.appendChild(caption);
	
	var zoom = win.document.createElement("div");
	zoom.setAttribute("id", "zoom");
	var table01 = win.document.createElement("div");
	table01.setAttribute("id", "table01");
	zoom.appendChild(table01);
	table01.innerHTML = str2;
	nav02.appendChild(zoom);

	win.document.close();
	win.print();
	win.close();
	/*
		var win = window.open("","newWin");
		win.document.writeln("<html>");
		win.document.writeln("<head>");
		win.document.writeln("<script type=\"text/javascript\" src=\"js/mops.js\"></script>");
		win.document.writeln("<link href=\"css/css.css\" rel=\"stylesheet\" type=\"text/css\" />");
		win.document.writeln("<link href=\"css/tag.css\" rel=\"stylesheet\" type=\"text/css\" />");
		win.document.writeln("<link href=\"css/tablea.css\" rel=\"stylesheet\" type=\"text/css\" />");
		win.document.writeln("</head>");
		win.document.writeln("<body style='background-color:white !important;'>");
		win.document.writeln("<div id='nav02'>");
		win.document.writeln("<div class='t01' id='caption'>");
		win.document.writeln("</div>");
		win.document.writeln("<div id='zoom'><div id='table01'>");
		win.document.writeln("</div></div>");
		win.document.writeln("</div>");
		win.document.writeln("</body>");
		win.document.writeln("</html>");
	
		//var caption = document.getElementById("caption");
		//if (caption!=null && caption!=undefined){
		//	win.document.getElementById("caption").innerHTML = caption.innerHTML;
		//}
		var table01 = document.getElementById("table01");
		if (table01!=null && table01!=undefined){
			win.document.getElementById("table01").innerHTML = table01.innerHTML;
		}
		win.document.close();
	*/
}

/*steven 20220107 writeln問題 修改 end */
function reportError() {
	//20160119 THOMAS SABRINA begin:修改問題回報(reportError)導向網址
	//var win = window.open("t146sb09?funcName="+document.fh.funcName.value,"newWin");
	//20160226 THOMAS SABRINA begin:修改問題回報(reportError)導向t146sb09
	//var win = window.open("http://suggestionbox.twse.com.tw/swsfront35/SWSF/SWSF01014.aspx","newWin");
	var win = window.open("t146sb09?funcName=" + document.fh.funcName.value, "newWin");
	//20160226 THOMAS SABRINA end:修改問題回報(reportError)導向t146sb09
	//20160119 THOMAS SABRINA end:修改問題回報(reportError)導向網址
	win.opener = window;
	/*
		win.document.writeln("<html>");
		win.document.writeln("<link href=\"css/css.css\" rel=\"stylesheet\" type=\"text/css\" />");
		win.document.writeln("<link href=\"css/tag.css\" rel=\"stylesheet\" type=\"text/css\" />");
		win.document.writeln("<link href=\"css/tablea.css\" rel=\"stylesheet\" type=\"text/css\" />");
		win.document.writeln("<body style='background-color:white !important;'>");
		win.document.writeln("<form action='' method='post'>");
	
		var nav_str = "";
		for (var i in navigator){
			nav_str += ","+i;
			win.document.writeln("<input type=\"hidden\" name=\"navigator_"+i+"\" value=\""+navigator[i]+"\">");
		}
		win.document.writeln("<input type=\"hidden\" name=\"navigator_properties_list\" value=\""+nav_str.substring(1)+"\">");
	
		win.document.writeln("<div style='padding:10px;'>");
		win.document.writeln("<h3>問題回報單</h3>");
		win.document.writeln("<div style='padding:10px;'>");
		win.document.writeln("問題提報人: <input type=\"text\" name=\"q_name\" size='10'>&nbsp;&nbsp;&nbsp;");
		win.document.writeln("稱謂: <select name='q_sex'><option value='M'>先生</option><option value='F'>小姐</option><option value='D'>教授</option></select>");
		win.document.writeln("</div>");
		win.document.writeln("<div style='padding:10px;'>");
		win.document.writeln("方便連絡電話: <input type=\"text\" name=\"q_tel\" size='20'>&nbsp;&nbsp;&nbsp;");
		win.document.writeln("常用E-mail: <input type=\"text\" name=\"q_email\" size='30'><br>");
		win.document.writeln("<font color='red' size='-1'>(連絡電話與e-mail請至少填寫其中一種)</font><br>");
		win.document.writeln("</div>");
		win.document.writeln("<div style='padding:10px;'>");
		win.document.writeln("問題種類: <select name='q_kind'><option value='1'>資料錯誤</option><option value='2'>畫面錯誤</option><option value='3'>其他問題</option></select>");
		win.document.writeln("</div>");
		win.document.writeln("<div style='padding:10px;'>");
		win.document.writeln("問題描述: <br><textarea name='content' cols='80' rows='6'></textarea><br>");
		win.document.writeln("</div>");
		win.document.writeln("<div style='padding:10px;'>");
		win.document.writeln("<input type='submit' value='填寫完畢，送出！'><br>");
		win.document.writeln("</div>");
		win.document.writeln("</div>");
	
		win.document.writeln("<input type='hidden' id='q_html' name='q_html'>");
	
		win.document.writeln("</form>");
	
		win.document.writeln("<div id='nav02' style='width:200px;height:150px;'></div>");
	
		win.document.writeln("</body>");
		win.document.writeln("</html>");
		win.document.writeln("<script>");
		win.document.writeln("document.getElementById('q_html').value=opener.document.getElementsByTagName('html')[0].innerHTML;");
		win.document.writeln("</script>");
	*/
	win.document.close();
}
function formSubmit(formname) {
	var formObj = document.forms[formname];
	formObj.submit();
}

function showCompanyData() {
	showValue("showCompanyID", "companyID");
	showValue("showCompanyName", "companyName");
	showValue("showCompanyMarket", "companyMarket");
}
/*steven 20220104 innerHTML 問題 start*/ 
function showValue(obj,val){
	var eO = document.getElementById(obj);
	var eV = document.getElementById(val);
	if (eO!=null && eO!=undefined){
		// if (eV!=null && eV!=undefined && eV.type=="hidden"){
		// 	eO.innerHTML = eV.value;
		// }else{
		// 	eO.innerHTML = "";
		// }
		if (eV!=null && eV!=undefined && eV.type=="hidden"){
			eO.innerText = eV.value;
		}else{
			eO.innerText = "";
		}
	}
}
/*steven 20220104 innerHTML 問題 end*/ 




var $$system_name = "newmops2";
//20180417 Garfield 將keyword從cookies自動填入中刪除
var $$keys = [
	//西元年份(yyyy)
	'YYYY',
	//民國年份(yyy)
	'year',
	'YY',
	'yy',
	'YEAR',
	//月份
	'month',
	'mm',
	'MONTH',
	//日
	'day',
	//市場別
	'TYPEK',
	'types',
	'typek',
	'sTYPEK',
	//季別
	'season',
	//公司代號
	'co_id',
	'COMPANY_ID',
	//資料年月
	'YM',
	//產業別
	'skind',
	'code',
	//開始年份(yyy)
	'startYear',
	//結束年份(yyy)
	'endYear',
	//開始月份(mm)
	'smonth',
	//結束月份(mm)
	'emonth',
	//開始日期(yyy/mm/dd)
	'yymmdd1',
	'yymm1',
	//結束日期日期(yyy/mm/dd)
	'yymmdd2',
	'yymm2',
	//開始日期(dd)
	'b_date',
	'SDAY',
	//結束日期(dd)
	'e_date',
	'EDAY',
	//開始查詢年月(yyy/mm)
	'ym1',
	//結束查詢年月(yyy/mm)
	'ym2',
	//開始-公司代號或簡稱(區間)
	'co_id_1',
	'coid1',
	'co_id1',
	'beg_co_id',
	//結束-公司代號或簡稱(區間)
	'co_id_2',
	'coid2',
	'co_id2',
	'end_co_id',
	//分離後認股權憑證代碼
	'bond',
	//公告日期
	'date',
	//開始-證券代號
	'sbond_id',
	//結束-證券代號
	'ebond_id',
	//開始-年月日(yyyy/mm/dd)
	'date_1',
	//結束-年月日(yyyy/mm/dd)
	'date_2',

	'warrant_id',
	'date1',
	'date2',
	'noticeDate',
	'noticeKind',
	'sort',
	'num',
	'BEG_SETDATE',
	'END_SETDATE',
	'index',
	'co1',
	'co2',
	'T1',
	'T2',
	'sid1',
	'sid2',
	'OSDATE',
	'OEDATE',
	'SDATE',
	'EDATE',
	'BOND_KIND',
	'BOND_YRN',
	'BOND_SUBN',
	'CK1',
	'CK2',
	'sortKey',
	'YYYYMM',
	'TK',
	'keyword',
	'bid1',
	'bid2',
	'kind',
	'qryType',
	'bond_kind',
	'r',
	'bond_id',
	'beg_occur_date',
	'end_occur_date',
	'd1',
	'd2',
	'rid',
	'wid',
	'sid',
	'slt',
	'name',
	'CHO1',
	'CHO2',
	'CHO3',
	'CHO4',
	'issuer_stock_code',
	'PUD1',
	'PUD2',
	'data_kind',
	'order',
	'desc',
	'warrant_class',
	'warrant_id',
	'stock_no',
	'publish_date',
	'close_date',
	'wt_1',
	'wt_2',
	'wt_3',
	'wt_4',
	'wt_5',
	'wt_6',
	'wt_7',
	'wt_8',
	'effective_date_1',
	'effective_date_2',
	'in_month'
];

var $$sub_name = "newmops2_sub";

function setFormVisible(){
	var f = document.form1;
	if (f!=null && f!=undefined){
		var isnew = null;
		if (f.isnew!=null && f.isnew!=undefined){
			isnew = f.isnew.value;
		}
		if (isnew==null){
			return;
		}
		var bVisible = (isnew!="true");
		for (var i=0;i<$$keys.length;i++){
			if ($$keys[i]=="isnew" || $$keys[i]=="TYPEK" || $$keys[i]=="co_id" || $$keys[i]=="warrant_id"){
				continue;
			}
			setObjectVisible(f[$$keys[i]],bVisible);
		}
	}
}

function setObjectVisible(obj,bVisible){
	if (obj==null || obj==undefined || obj.type==null || obj.type==undefined){
		return;
	}else{
		obj.style.visibility = (bVisible?"":"hidden");
	}
}

function setFormValue(){
	var f = document.form1;
	if (f!=null && f!=undefined){
		for (var i=0;i<$$keys.length;i++){
			setObjectValue(f[$$keys[i]],getChip($$system_name,$$keys[i]));
		}
	}
}

function saveFormValue() {
	var f = document.form1;
	if (f != null && f != undefined) {
		for (var i = 0; i < $$keys.length; i++) {
			var v = getObjectValue(f[$$keys[i]]);
			if (v != null) {
				setChip($$system_name, $$keys[i], v);
				//相同年分
				if ($$keys == 'year' || $$keys == 'YY' || $$keys == 'YEAR' || $$keys == 'yy') {
					setChip($$system_name, 'year', v);
					setChip($$system_name, 'YY', v);
					setChip($$system_name, 'YEAR', v);
					setChip($$system_name, 'yy', v);
				}
				//相同月份
				if ($$keys == 'month' || $$keys == 'mm' || $$keys == 'MONTH') {
					setChip($$system_name, 'month', v);
					setChip($$system_name, 'mm', v);
					setChip($$system_name, 'MONTH', v);
				}
				//相同市場別
				if ($$keys == 'TYPEK' || $$keys == 'typek' || $$keys == 'types' || $$keys == 'sTYPEK') {
					setChip($$system_name, 'TYPEK', v);
					setChip($$system_name, 'typek', v);
					setChip($$system_name, 'types', v);
					setChip($$system_name, 'sTYPEK', v);
				}
				//相同公司代號
				if ($$keys == 'co_id' || $$keys == 'COMPANY_ID') {
					setChip($$system_name, 'co_id', v);
					setChip($$system_name, 'COMPANY_ID', v);
				}
				//相同開始日期(yyy/mm/dd)
				if ($$keys == 'yymmdd1' || $$keys == 'yymm1') {
					setChip($$system_name, 'yymmdd1', v);
					setChip($$system_name, 'yymm1', v);
				}
				//結束日期日期(yyy/mm/dd)
				if ($$keys == 'yymmdd2' || $$keys == 'yymm2') {
					setChip($$system_name, 'yymmdd2', v);
					setChip($$system_name, 'yymm2', v);
				}
				//開始日期(dd)
				if ($$keys == 'b_date' || $$keys == 'SDAY') {
					setChip($$system_name, 'b_date', v);
					setChip($$system_name, 'SDAY', v);
				}
				//開始-公司代號或簡稱(區間)
				if ($$keys == 'co_id_1' || $$keys == 'coid1' || $$keys == 'co_id1' || $$keys == 'beg_co_id') {
					setChip($$system_name, 'co_id_1', v);
					setChip($$system_name, 'coid1', v);
					setChip($$system_name, 'co_id1', v);
					setChip($$system_name, 'beg_co_id', v);
				}
				//結束-公司代號或簡稱(區間)
				if ($$keys == 'co_id_2' || $$keys == 'coid2' || $$keys == 'co_id2' || $$keys == 'end_co_id') {
					setChip($$system_name, 'co_id_2', v);
					setChip($$system_name, 'coid2', v);
					setChip($$system_name, 'co_id2', v);
					setChip($$system_name, 'end_co_id', v);
				}
			}
		}
	}
}

function getObjectType(obj){
	if (obj==null || obj==undefined || obj.type==null || obj.type==undefined){
		return undefined;
	}else{
		return obj.type;
	}
}

function getObjectValue(obj){
	var oType = getObjectType(obj);
	if (oType==undefined){
		return null;
	}else{
		if (oType=="text" || oType=="textarea"){
			return obj.value;
		}else if (oType=="select-one"){
			return obj.value;
		}else if (oType=="radio"){
			for (var i=0;i<obj.length;i++){
				if (obj[i].checked){
					return obj[i].value;
				}
			}
		}else if (oType=="checkbox"){
			var ar = new Array();
			for (var i=0;i<obj.length;i++){
				if (obj[i].checked){
					ar[ar.length] = obj[i].value;
				}
			}
			return ar;
		}
	}

	return null;
}

function setObjectValue(obj,values){
	if (values==null){
		return;
	}

	var oType = getObjectType(obj);
	if (values instanceof Array){
		if (oType=="text" || oType=="textarea"){
			obj.value = values[0];
		}else if (oType=="select-one"){
			obj.value = values[0];
		}else if (oType=="radio"){
			for (var i=0;i<obj.length;i++){
				if (obj[i].value==values[0]){
					obj[i].checked=true;
				}
			}
		}else if (oType=="checkbox"){
			for (var i=0;i<obj.length;i++){
				for (var j=0;j<values.length;j++){
					if (obj[i].value==values[j]){
						obj[i].checked=true;
					}
				}
			}
		}
	}else{
		if (oType=="text" || oType=="textarea"){
			obj.value = values;
		}else if (oType=="select-one"){
			obj.value = values;
		}else if (oType=="radio"){
			for (var i=0;i<obj.length;i++){
				if (obj[i].value==values){
					obj[i].checked=true;
				}
			}
		}else if (oType=="checkbox"){
			for (var i=0;i<obj.length;i++){
				if (obj[i].value==values){
					obj[i].checked=true;
				}
			}
		}
	}
}

function openSubMenu(){
	var o = document.getElementById("subMenuID");
	if (o!=null && o!=undefined){
		var labelID = o.value;
		if (labelID!=null && labelID!=undefined && labelID!=""){
			if (labelID.substring(0,1)=="[" && labelID.substring(labelID.length-1)=="]"){
//				labelID = eval(labelID);
				labelID = labelID.replace("[" , "");
				labelID = labelID.replace("]" , "");
				var tmpLabel = labelID.split(",");

				for (var i=0;i<tmpLabel.length;i++){
					var test = document.getElementById("level-"+tmpLabel[i]);
					if (test!=null && test!=undefined){
						showhide("level-"+tmpLabel[i],"img-"+tmpLabel[i]);
					}
				}
			}else{
				var test = document.getElementById("level-"+labelID);
				if (test!=null && test!=undefined){
					showhide("level-"+labelID,"img-"+labelID);
				}
			}
		}
	}
}

function addListenerIsNew(){
	if (document.form1==null || document.form1==undefined){
		return;
	}
	if (document.form1.isnew==null || document.form1.isnew==undefined){
		return;
	}
	addListener(document.form1.isnew,"change",setFormVisible);
	setFormVisible();
}

addListener(window,"load",setFormValue);
addListener(window,"load",openSubMenu);
addListener(window,"load",addListenerIsNew);

//	控制顯示快速查詢
	function showsh3(id,obj){
		var dv = document.getElementById(""+id+"");
		dv.style.display = "block";
		var obj1 = document.getElementById(""+obj+"");  
		var x = getLeft(obj1);
		var y = getTop(obj1)+20;
		
		document.getElementById(""+id+"").style.left=x+"px";
		document.getElementById(""+id+"").style.top=y+"px";
	}
	//=function showsh(id,img){
	//=	var dv = document.getElementById(""+id+"");
		//var   version   =   parseFloat(navigator.appVersion.split("MSIE")[1]); 

		//if (version<=7){
			//=dv.style.display = "";

			//=var obj = document.getElementById(""+img+"");  
			//=dv.style.width = obj.clientWidth;
		//}
		//else{
		//	dv.style.display = "block";
		//	var obj = document.getElementById(""+img+"");  
		//	var x = realPosX(obj);
		//	var y = realPosY(obj)+17;
			
		//	document.getElementById(""+id+"").style.left=x+"px";
		//	document.getElementById(""+id+"").style.top=y+"px";

		//}

	//=}



	function realPosX1(oTarget) {
			var realX = oTarget.offsetLeft;
			if (oTarget.offsetParent.tagName.toUpperCase() != "BODY" && oTarget.offsetParent.tagName.toUpperCase() != "TD") {
				realX += realPosX1(oTarget.offsetParent); 
			}
			return realX;
	}
	function realPosY1(oTarget) {
			var realY = oTarget.offsetTop;
			if (oTarget.offsetParent.tagName.toUpperCase() != "BODY"  && oTarget.offsetParent.tagName.toUpperCase() != "TD") {
				realY += realPosY1(oTarget.offsetParent);

			}
			return realY;
	}

	function getLeft(obj) {
        if (obj == null)
            return null;
        var mendingObj = obj;
        var mendingLeft = mendingObj.offsetLeft;
        while (mendingObj != null && mendingObj.offsetParent != null && mendingObj.offsetParent.tagName != "BODY") {
            mendingLeft = mendingLeft + mendingObj.offsetParent.offsetLeft;
            mendingObj = mendingObj.offsetParent;
        }

        return mendingLeft;
    }
    function getTop(obj) {
        if (obj == null)
            return null;
        var mendingObj = obj;
        var mendingTop = mendingObj.offsetTop;
        while (mendingObj != null && mendingObj.offsetParent != null && mendingObj.offsetParent.tagName != "BODY") {
            mendingTop = mendingTop + mendingObj.offsetParent.offsetTop;
            mendingObj = mendingObj.offsetParent;
        }
        return mendingTop;
    }


	function GetAbsoluteY(Obj){
	  var TempObj = Obj; 
	  var rtData = 0;
	   
	  do{ 
		rtData = rtData + TempObj.offsetTop; 
		TempObj = TempObj.offsetParent; 
	  }while(TempObj != document.body)
	  
	  return rtData;
	}

	//找尋輸入物件的X軸絕對位置(指的是相對於網頁最左上端)
	function GetAbsoluteX(Obj){
	  var TempObj = Obj; 
	  var rtData = 0;
	  
	  do{ 
		rtData = rtData + TempObj.offsetLeft; 
		TempObj = TempObj.offsetParent; 
	  }while(TempObj != document.body)
	  
	  return rtData;
	} 

	function showsh2(id,obj){
//		alert('-- show'+id+' '+obj);
		var dv = document.getElementById(""+id+"");
		dv.style.display = "block";
		var obj1 = document.getElementById(""+obj+"");  
		var x = getLeft(obj1);
		var y = getTop(obj1)+50;
		
		document.getElementById(""+id+"").style.left=x+"px";
		document.getElementById(""+id+"").style.top=y+"px";
	}

	function hideIt2(div){
		document.getElementById(""+div+"").style.display = "none";
	}

//20160121 EDWARD ALICE begin:因改為全站檢索，所以下方的程式都用不到了
/*
	function showIt5(){ //公告
		document.getElementById("search_txt").style.display = "none";
		//document.getElementById("selflike").style.display = "none";
		document.getElementById("keyword").value = "請輸入公司代號或簡稱";
		document.getElementById("stockid").style.display = "inline";
		document.getElementById("sltdate").style.display = "inline";
		document.getElementById("sltitem").style.display = "inline";
		document.getElementById("keyword3").style.display = "none";
		document.getElementById("keyword").size = "20";

	}

	function showIt4(){ //重大訊息
		document.getElementById("search_txt").style.display = "inline";
		//document.getElementById("selflike").style.display = "none";
		document.getElementById("keyword").value = "請輸入公司代號或簡稱";
		document.getElementById("stockid").style.display = "inline";
		document.getElementById("sltdate").style.display = "inline";
		document.getElementById("sltitem").style.display = "none";
		document.getElementById("keyword3").style.display = "inline";
		document.getElementById("keyword3").value = "請輸入關鍵字";
		document.getElementById("keyword").size = "20";
		showBanner("1");
	}

	function showIt3(){ //精華版
		document.getElementById("search_txt").style.display = "none";
		//document.getElementById("selflike").style.display = "none";
		document.getElementById("keyword").value = "請輸入公司代號或簡稱";
		document.getElementById("stockid").style.display = "inline";
		document.getElementById("sltdate").style.display = "none";
		document.getElementById("sltitem").style.display = "none";
		document.getElementById("keyword3").style.display = "none";
		document.getElementById("keyword").size = "45";

	}

	function showIt2(){ //個股
		document.getElementById("search_txt").style.display = "none";
		//document.getElementById("selflike").style.display = "none";
		document.getElementById("keyword").value = "請輸入公司代號或簡稱";
		document.getElementById("stockid").style.display = "inline";
		document.getElementById("sltdate").style.display = "none";
		document.getElementById("sltitem").style.display = "none";
		document.getElementById("keyword3").style.display = "none";
		document.getElementById("keyword").size = "45";
	}
	function showIt1(){ //資訊項目
		document.getElementById("search_txt").style.display = "inline";
		//document.getElementById("keyword").value ="彙總表";
		document.getElementById("keyword").value = "請輸入報表名稱關鍵字";
		//document.getElementById("selflike").style.display = "none";
		document.getElementById("stockid").style.display = "none";
		document.getElementById("sltdate").style.display = "none";
		document.getElementById("sltitem").style.display = "none";
		document.getElementById("keyword3").style.display = "none";
		document.getElementById("keyword").size = "45";
		showBanner("0");
	}
	function chantxt1(){ //資訊項目
		document.getElementById("itm1").style.color = "yellow";
		document.getElementById("itm2").style.color = "white";
//		document.getElementById("itm3").style.color = "white";
		document.getElementById("itm4").style.color = "white";
		//document.getElementById("itm5").style.color = "white";
		document.getElementById("itm6").style.color = "white";
	}
	function chantxt2(){ //精華版2.0
		document.getElementById("itm1").style.color = "white";
		document.getElementById("itm2").style.color = "yellow";
//		document.getElementById("itm3").style.color = "white";
		document.getElementById("itm4").style.color = "white";
		//document.getElementById("itm5").style.color = "white";
		document.getElementById("itm6").style.color = "white";
	}

//	function chantxt3(){ //
//		document.getElementById("itm3").style.color = "yellow";

//		document.getElementById("itm1").style.color = "white";
//		document.getElementById("itm2").style.color = "white";
//		document.getElementById("itm4").style.color = "white";
		//document.getElementById("itm5").style.color = "white";
	
//	}

	function chantxt4(){ //重大訊息
		document.getElementById("itm4").style.color = "yellow";
		document.getElementById("itm1").style.color = "white";
		document.getElementById("itm2").style.color = "white";
		document.getElementById("itm6").style.color = "white";
//		document.getElementById("itm3").style.color = "white";
		//document.getElementById("itm5").style.color = "white";
	
	}

//	function chantxt5(){ //公告
//		document.getElementById("itm5").style.color = "yellow";

//		document.getElementById("itm1").style.color = "white";
//		document.getElementById("itm2").style.color = "white";
//		document.getElementById("itm3").style.color = "white";
//		document.getElementById("itm4").style.color = "white";
	
//	}


	function chantxt6(){ //個股
		document.getElementById("itm6").style.color = "yellow";
		document.getElementById("itm1").style.color = "white";
		document.getElementById("itm2").style.color = "white";
		document.getElementById("itm4").style.color = "white";
	
	}
	function closex1(){//個股時，打開左邊submenu
		document.getElementById('main').style.display ='none';
		document.getElementById('pic').src = 'images/pic24.gif';

	}
	function closex2(){//個股時，關閉左邊submenu
		document.getElementById('main').style.display ='inline';
		document.getElementById('pic').src = 'images/pic23.gif';

	}


	function goaction(menu){
		if (menu=="1"){
			document.fh.action="/mops/web/t146sb08";
			//closex2();
			document.fh.step.value="1";
			document.fh.keycon.value="1";
			document.fh.submit();

			
		}
		if (menu=="2"){
			document.fh.action="/mops/web/t146sb03";
			//closex2();
			//在將公司代號+公司名稱送出時，以空白為分隔符號，只將公司代號當作參數送出去
			var a=document.fh.keyword.value+" ";
			var b=a.search(" ");
			document.fh.keyword.value=a.substring(0,b);
			document.fh.co_id.value=document.fh.keyword.value;
			//================
			document.fh.step.value="1";
			document.fh.keycon.value="1";

			//twse1097
			setChip($$system_name,"co_id",document.fh.co_id.value);
			document.fh.submit();

		}
		if (menu=="3"){
			document.fh.action="/mops/web/t146sb03";
			//closex1();
			var a=document.fh.keyword.value+" ";
			var b=a.search(" ");
			document.fh.keyword.value=a.substring(0,b);
			document.fh.co_id.value=document.fh.keyword.value;
			document.fh.step.value="1";
			document.fh.keycon.value="1";

			document.fh.submit();

		}
		if (menu=="4"){
			document.fh.Stp.value="MH";
			document.fh.step.value="2";

			document.fh.action="/mops/web/t51sb10_q1";
			//document.fh.co_id.value=document.fh.keyword.value;
			//closex2();
			if (document.fh.keyword.value=="請輸入公司代號"){
				document.fh.keycon.value="0";
				alert("公司代號未輸入");
				
			}
			else{
				if (document.fh.keyword3.value=="請輸入關鍵字"){
					document.fh.keyword3.value="";
				}
				var a=document.fh.keyword.value+" ";
				var b=a.search(" ");
				document.fh.keyword.value=a.substring(0,b);
				document.fh.keycon.value="1";

				document.fh.submit();

			}
		}
		if (menu=="5"){
			document.fh.action="/mops/web/t146sb10";
			document.fh.date.value=document.fh.select.value;
			document.fh.noticeKind.value=document.fh.select1.value;

			var a=document.fh.keyword.value+" ";
			var b=a.search(" ");
			document.fh.co_id_1.value=a.substring(0,b);
			document.fh.co_id_2.value=a.substring(0,b);
			document.fh.step.value="1";
			document.fh.keycon.value="1";

			document.fh.submit();

			//closex2();
		}
		if (menu=="6"){
			document.fh.action="/mops/web/t05st03";
			var a=document.fh.keyword.value+" ";
			var b=a.search(" ");
			document.fh.keyword.value=a.substring(0,b);
			document.fh.co_id.value=document.fh.keyword.value;
			document.fh.step.value="1";
			document.fh.keycon.value="1";
			
			//twse1097
			setChip($$system_name,"co_id",document.fh.co_id.value);
			document.fh.submit();

			//closex2();
		}

	}

*/

	function goaction(thisValue , type , funcNo){

		if ( thisValue == '' ){
			thisValue = document.fh.keyword.value;
		}

		if ( type != "0" ){//公司代號
			setChip($$system_name,"co_id",thisValue);

			document.fh.action="/mops/web/t146sb05";
			document.fh.co_id.value=thisValue;
			document.fh.firstin.value="Y";
			document.fh.step.value="1";
		}else{
			if ( funcNo == "" || funcNo == undefined ){
				funcNo = "t146sb08";

				document.fh.firstin.value="Y";
			}else{
				document.fh.firstin.value="";
			}

			document.fh.action="/mops/web/"+funcNo;
			document.fh.keyword.value=thisValue;
			document.fh.step.value="1";
		}

		document.fh.submit();
	}

//20160121 EDWARD ALICE end:因改為全站檢索，所以下方的程式都用不到了

	function saveCsv(a,b){//儲存csv要呼叫的函式，目前的execcommand為ie only，firefox要想辦法
		var new_win = window.open('about:blank','Export','width=120,height=50');
		var f = document.forms[0];
		//for (var i=0;i<eval(f.csvCount.value);i++){
			new_win.document.writeln(a);
		//}
		new_win.document.execCommand('SaveAs',null,b);
		new_win.close();
	//	exportToText();
	}
	function bringv(a){//點選常用項目的連結後，將字帶到對應的欄位
		if (document.fh.menusave.value=="4")
			document.fh.keyword3.value=a;
		else
			document.fh.keyword.value=a;

	}
	function clskeyword(){
		if (document.fh.keyword.value=="請輸入公司代號"){
			document.fh.keyword.value="";
		}
		if (document.fh.keyword.value=="請輸入公司代號或簡稱"){
			document.fh.keyword.value="";
		}
		if (document.fh.keyword.value=="請輸入關鍵字"){
			document.fh.keyword.value="";
		}
		if (document.fh.keyword.value=="請輸入報表名稱關鍵字"){
			document.fh.keyword.value="";
		}
	}
	function clskeyword1(){
		if (document.fh.keyword3.value=="請輸入關鍵字"){
			document.fh.keyword3.value="";
		}
	}
	function proceval(){

	}
/*
	//表格加入mouseover及mouseout事件
	function chgColor(){
		var ptr = new Array();
		if (document.getElementById("zoom01") != null){
			ptr = document.getElementById("zoom01").getElementsByTagName("tr");
		}
		if (document.getElementById("zoom") != null){
			ptr = document.getElementById("zoom").getElementsByTagName("tr");
		}
		if (ptr != null){
			for(var i=0;i<ptr.length;i++) {
				if (ptr[i].className=="even" || ptr[i].className=="odd"){
					var obj = ptr[i];
					var clasName = ptr[i].className;
					onMouseOv(obj);
					onMouseOt(obj,clasName);
				}
			}
		}
	}
	

	function onMouseOv(obj){
		//alert(ptt.className);
		if (obj.addEventListener){
			obj.addEventListener("mouseover",function(){obj.className = "mouseOn";},false);
		}else{
			obj.attachEvent("onmouseover",function(){obj.className = "mouseOn";});
		}
	}

	function onMouseOt(obj,clasName){
		//alert(ptt.className);
		if (obj.addEventListener){
			obj.addEventListener("mouseout",function(){obj.className=clasName;},false);
		}else{
			obj.attachEvent("onmouseout",function(){obj.className=clasName;});
		}
	}
*/
	function showBanner(step){
		var str = "";
		str += "<table border='0'><tr><td>";
		str += "<div style=\"background-image: url(images/map01.jpg) ; height: 19px; width:32px;background-position:-161px 0px;\"></div>";
		if (step=="0"){
			str += "</td><td>";
			str += " <a href=\"#\" onclick=\"bringv('股東會');fh.key1h.value=fh.keyword3.value;fh.keyh.value=fh.keyword.value;fh.slth.value=fh.select.value;fh.slt1h.value=fh.select1.value;hideIt2('quicksearch');hideIt2('quicksearch2');goaction(fh.menusave.value);\">股東會</a>";
			str += " <a href=\"#\" onclick=\"bringv('除權息');fh.key1h.value=fh.keyword3.value;fh.keyh.value=fh.keyword.value;fh.slth.value=fh.select.value;fh.slt1h.value=fh.select1.value;hideIt2('quicksearch');hideIt2('quicksearch2');goaction(fh.menusave.value);\">除權息</a>";
			str += " <a href=\"#\" onclick=\"bringv('電子書');fh.key1h.value=fh.keyword3.value;fh.keyh.value=fh.keyword.value;fh.slth.value=fh.select.value;fh.slt1h.value=fh.select1.value;hideIt2('quicksearch');hideIt2('quicksearch2');goaction(fh.menusave.value);\">電子書</a>";
			str += " <a href=\"#\" onclick=\"bringv('法說會');fh.key1h.value=fh.keyword3.value;fh.keyh.value=fh.keyword.value;fh.slth.value=fh.select.value;fh.slt1h.value=fh.select1.value;hideIt2('quicksearch');hideIt2('quicksearch2');goaction(fh.menusave.value);\">法說會</a>";
			str += " <a href=\"#\" onclick=\"bringv('庫藏股');fh.key1h.value=fh.keyword3.value;fh.keyh.value=fh.keyword.value;fh.slth.value=fh.select.value;fh.slt1h.value=fh.select1.value;hideIt2('quicksearch');hideIt2('quicksearch2');goaction(fh.menusave.value);\">庫藏股</a>";
			str += " <a href=\"#\" onclick=\"bringv('董監持股');fh.key1h.value=fh.keyword3.value;fh.keyh.value=fh.keyword.value;fh.slth.value=fh.select.value;fh.slt1h.value=fh.select1.value;hideIt2('quicksearch');hideIt2('quicksearch2');goaction(fh.menusave.value);\">董監持股</a>";
			str += " <a href=\"#\" onclick=\"bringv('獨立董事');fh.key1h.value=fh.keyword3.value;fh.keyh.value=fh.keyword.value;fh.slth.value=fh.select.value;fh.slt1h.value=fh.select1.value;hideIt2('quicksearch');hideIt2('quicksearch2');goaction(fh.menusave.value);\">獨立董事</a>";
			str += " <a href=\"#\" onclick=\"bringv('董監酬金');fh.key1h.value=fh.keyword3.value;fh.keyh.value=fh.keyword.value;fh.slth.value=fh.select.value;fh.slt1h.value=fh.select1.value;hideIt2('quicksearch');hideIt2('quicksearch2');goaction(fh.menusave.value);\">董監酬金</a>";
			str += " <a href=\"#\" onclick=\"bringv('ETF');fh.key1h.value=fh.keyword3.value;fh.keyh.value=fh.keyword.value;fh.slth.value=fh.select.value;fh.slt1h.value=fh.select1.value;hideIt2('quicksearch');hideIt2('quicksearch2');goaction(fh.menusave.value);\">ETF</a>";
			str += " <a href=\"#\" onclick=\"bringv('TDR');fh.key1h.value=fh.keyword3.value;fh.keyh.value=fh.keyword.value;fh.slth.value=fh.select.value;fh.slt1h.value=fh.select1.value;hideIt2('quicksearch');hideIt2('quicksearch2');goaction(fh.menusave.value);\">TDR</a>";
		}
		if (step=="1"){
			str += "</td><td><a href=\"#\" onclick=\"bringv('股東會');\">股東會</a>";
			str += " <a href=\"#\" onclick=\"bringv('股利');\">股利</a>";
			str += " <a href=\"#\" onclick=\"bringv('收購');\">收購</a>";
			str += " <a href=\"#\" onclick=\"bringv('增資');\">增資</a>";
			str += " <a href=\"#\" onclick=\"bringv('減資');\">減資</a>";
			str += " <a href=\"#\" onclick=\"bringv('重整');\">重整</a>";
		}
		str += "</td></tr></table>";
		document.getElementById("search_txt").innerHTML = str;
	}

/* 備份

function ajax1(form1,targets) {
	saveFormValue();
	var str='encodeURIComponent=1';
	var es = form1.elements;
	for (var i=0;i<es.length;i++){
		var value1=encodeURIComponent(es[i].value);
		if(es[i].name=='') continue;
		if(es[i].type=="checkbox") { if(!es[i].checked) continue;}
		if(es[i].type=="radio") { if(!es[i].checked) continue;}
		str=str+'&'+es[i].name+'='+value1;
	}
//	alert(str);
	var url=form1.action;
	var xmlDoc;
	if (window.XMLHttpRequest)
	{
		xmlDoc = new XMLHttpRequest();
		xmlDoc.open("POST", url, false);
		xmlDoc.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
		xmlDoc.send(str);
	} else if (window.ActiveXObject) { // IE
		isIE = true;
		try {
			xmlDoc = new ActiveXObject ("Msxml2.XMLHTTP");
		} catch (e) {
			xmlDoc = new ActiveXObject ("Microsoft.XMLHTTP");
		}

		if (xmlDoc)
		{
			xmlDoc.open ("POST", url, false);
			xmlDoc.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
			xmlDoc.send(str);
		}
	}
	var target1=document.getElementById(targets);
	if(target1==null){
		alert('Targets '+targets+' not found!');
	} else {
		target1.innerHTML=xmlDoc.responseText;
	}
	return false;
}
*/




// global variables //
var TIMER = 5;
var SPEED = 10;
var WRAPPER = 'content';

// calculate the current window width //
function pageWidth() {
  return window.innerWidth != null ? window.innerWidth : document.documentElement && document.documentElement.clientWidth ? document.documentElement.clientWidth : document.body != null ? document.body.clientWidth : null;
}

// calculate the current window height //
function pageHeight() {
  return window.innerHeight != null? window.innerHeight : document.documentElement && document.documentElement.clientHeight ? document.documentElement.clientHeight : document.body != null? document.body.clientHeight : null;
}

// calculate the current window vertical offset //
function topPosition() {
  return typeof window.pageYOffset != 'undefined' ? window.pageYOffset : document.documentElement && document.documentElement.scrollTop ? document.documentElement.scrollTop : document.body.scrollTop ? document.body.scrollTop : 0;
}

// calculate the position starting at the left of the window //
function leftPosition() {
  return typeof window.pageXOffset != 'undefined' ? window.pageXOffset : document.documentElement && document.documentElement.scrollLeft ? document.documentElement.scrollLeft : document.body.scrollLeft ? document.body.scrollLeft : 0;
}

// build/show the dialog box, populate the data and call the fadeDialog function //
function showDialog(title,message,type,autohide) {
  if(!type) {
    type = 'error';
  }
  var dialog;
  var dialogheader;
  var dialogclose;
  var dialogtitle;
  var dialogcontent;
  var dialogmask;
//  alert(document.getElementById('dialog'));
  if(!document.getElementById('dialog')) {
    dialog = document.createElement('div');
    dialog.id = 'dialog';
    dialogheader = document.createElement('div');
    dialogheader.id = 'dialog-header';
    dialogtitle = document.createElement('div');
    dialogtitle.id = 'dialog-title';
    dialogclose = document.createElement('div');
    dialogclose.id = 'dialog-close'
    dialogcontent = document.createElement('div');
    dialogcontent.id = 'dialog-content';
    dialogmask = document.createElement('div');
    dialogmask.id = 'dialog-mask';
    document.body.appendChild(dialogmask);
    document.body.appendChild(dialog);
    dialog.appendChild(dialogheader);
    dialogheader.appendChild(dialogtitle);
    dialogheader.appendChild(dialogclose);
    dialog.appendChild(dialogcontent);;
    dialogclose.setAttribute('onclick','hideDialog()');
    dialogclose.onclick = hideDialog;
  } else {
    dialog = document.getElementById('dialog');
    dialogheader = document.getElementById('dialog-header');
    dialogtitle = document.getElementById('dialog-title');
    dialogclose = document.getElementById('dialog-close');
    dialogcontent = document.getElementById('dialog-content');
    dialogmask = document.getElementById('dialog-mask');
    dialogmask.style.visibility = "visible";
    dialog.style.visibility = "visible";
  }
  dialog.style.opacity = .00;
  dialog.style.filter = 'alpha(opacity=0)';
  dialog.alpha = 0;
  var width = pageWidth();
  var height = pageHeight();
  var left = leftPosition();
  var top = topPosition();
  var dialogwidth = dialog.offsetWidth;
  var dialogheight = dialog.offsetHeight;
  var topposition = top + (height / 3) - (dialogheight / 2)+60;
  var leftposition = left + (width / 2) - (dialogwidth / 2);
  dialog.style.top = topposition + "px";
  dialog.style.left = leftposition + "px";
  dialogheader.className = type + "header";
  dialogtitle.innerHTML = title;
  dialogcontent.className = type;
//  dialogcontent.innerHTML = '<iframe frameBorder=0 style="width:100%;height:100%;"><html><body>'+message+'</html></iframe>';
  dialogcontent.innerHTML = message;
//  var content = document.getElementById(WRAPPER);
  var content = document.body;
  dialogmask.style.height = content.offsetHeight + 'px';
//  alert(' dialog.timer1='+dialog.timer);
  if(dialog.timer!=null){
    clearInterval(dialog.timer);
  }
  dialog.timer = setInterval("fadeDialog(1)", TIMER);

  if(autohide) {
    dialogclose.style.visibility = "hidden";
    window.setTimeout("hideDialog()", (autohide * 1000));
  } else {
    dialogclose.style.visibility = "visible";
  }


}
// hide the dialog box //
function hideDialog() {
  var dialog = document.getElementById('dialog');
//	alert(dialog);
  clearInterval(dialog.timer);
  dialog.timer = setInterval("fadeDialog(0)", TIMER);
}

// fade-in the dialog box //
function fadeDialog(flag) {
  if(flag == null) {
    flag = 1;
  }
  var dialog = document.getElementById('dialog');
  var value;
  if(flag == 1) {
    value = dialog.alpha + SPEED;
  } else {
    value = dialog.alpha - SPEED;
  }
  dialog.alpha = value;
  dialog.style.opacity = (value / 100);
  dialog.style.filter = 'alpha(opacity=' + value + ')';
//  alert('value='+value+' dialog.timer1='+dialog.timer);
  if(value >= 99) {
    clearInterval(dialog.timer);
    dialog.timer = null;
  } else if(value <= 1) {
    dialog.style.visibility = "hidden";
//20121226 leo begin: 加上判斷物件是否存在 
	if (document.getElementById('dialog-mask') != null) {
		document.getElementById('dialog-mask').style.visibility = "hidden";
	}
//20121226 leo end: 加上判斷物件是否存在 
    clearInterval(dialog.timer);
  }
}
var ajax_target=new Array();

var xmlDoc=new Array();
function monitor1(index,targets){
	if(ajax_target[index]!=null){
		var rsData=xmlDoc[index].readyState+',';
		if(xmlDoc[index].readyState==1){
//			alert('rsData='+rsData+' ajax_target='+ajax_target);
//			showDialog('','資料載入中，請稍候..','SUCESS');
			showDialog('','<div style="width:100%;height:100%;border:ridge 0px #777;color:#0000ff;font-size:16;vertical-align:middle;text-align:center;"><img src="images/Clock1.gif"></img></div>','SUCESS');
		} else if(xmlDoc[index].readyState==4){
//			alert('rsData='+rsData+' ajax_target'+ajax_target);
			var target1=document.getElementById(ajax_target[index]);
			if(target1==null){
				alert('Targets '+targets+' not found!');
				ajax_target[index]=null;
			} else {
				REC1.push(target1);
				REC2.push(target1.innerHTML);

				if(REC3.lneght==0){

					var param1=new Array();
					var param2=new Array();
					try{
					var es = form1.elements;
					for (var i=0;i<es.length;i++){
						var value1=es[i].value;
						if(es[i].name=='') continue;
						if(es[i].type=="checkbox") { if(!es[i].checked) continue;}
						if(es[i].type=="radio") { if(!es[i].checked) continue;}
						param1.push(es[i]);
						param2.push(value1);
					}
					} catch(ee){}
					REC3.push(param1);
					REC4.push(param2);

				}

				var param1=new Array();
				var param2=new Array();
				try{
				var es = form1.elements;
				for (var i=0;i<es.length;i++){
					var value1=es[i].value;
					if(es[i].name=='') continue;
					if(es[i].type=="checkbox") { if(!es[i].checked) continue;}
					if(es[i].type=="radio") { if(!es[i].checked) continue;}
					param1.push(es[i]);
					param2.push(value1);
				}
				} catch(ee){}

				var POSY=0;
				try{
					POSY=f_scrollTop();

//			alert('-- sc_x='+f_scrollTop());
				} catch(ee){}

				REC5.push(POSY);

				REC3.push(param1);
				REC4.push(param2);
				RECP++;

				target1.innerHTML=xmlDoc[index].responseText;
				ajax_target[index]=null;

				if(document.getElementById('ajax_back_button')!=null){
					document.getElementById('ajax_back_button').style.visibility='visible';
				}

				if (fullVersion=="7"){
					
					setTimeout("doZoom('fontSize2');", 100);

//					setTimeout("changeZoom();", 100);
	
				}
				window.scrollTo(0,0);


			}
			var allOK=true;
			for (var i=0;i<ajax_target.length;i++){
				if (ajax_target[i]!=null){
					allOK = false;
				}
			}
			if (allOK){
				hideDialog();
				if(document.getElementById('__special_script')!=null){
					var text1=document.getElementById('__special_script').innerHTML;
					document.getElementById('__special_script').innerHTML='';
//					var text1=document.getElementById('__special_script').innerText;
//					document.getElementById('__special_script').innerText='';
//					alert('text1');
					if(text1!=null){
						eval(text1);
					}
				}
			}
		}
	}
//	var stData=xmlDoc.status+',';
//	var rtData=xmlDoc.statusText+',';
//	alert('rsData='+rsData);
}
function f_scrollTop() {
	var t1=0;
	var t2=0;
	var t3=0;
	if(window.pageYOffset) t1=window.pageYOffset;
	if(document.documentElement) t2=document.documentElement.scrollTop;
	if(document.body) t3=document.body.scrollTop;

	var max=0;
	if(t1>max) max=t1;
	if(t2>max) max=t2;
	if(t3>max) max=t3;
	return max;
}

function ajax_back(){
	if(RECP<0) return;
	if(REC1.length==0) return;
	var t1=REC1.pop();
	var t2=REC2.pop();

	var t5=REC5.pop();
//	alert('set to '+t5+' RECP='+RECP+' '+REC5.length);

	RECP--;

	var t3=REC3[RECP];
	var t4=REC4[RECP];
	REC3.pop();
	REC4.pop();
	if(t3!=null){
		for(i=0;i<t3.length;i++){
			t3[i].value=t4[i];
		}
	}


	if(REC1.length==0){
		if(document.getElementById('ajax_back_button')!=null){
			document.getElementById('ajax_back_button').style.visibility='hidden';
		}
	}

	t1.innerHTML=t2;

	if(window.pageYOffset) window.pageYOffset=t5;
	if(document.documentElement) document.documentElement.scrollTop=t5;
	if(document.body) document.body.scrollTop=t5;


}

var REC1=new Array();
var REC2=new Array();
var REC3=new Array();
var REC4=new Array();
var REC5=new Array();
var RECP=-1;

function ajax1(form1,targets) {
	saveFormValue();
	if(document.getElementById('__special_script')!=null){
		document.getElementById('__special_script').innerHTML='';
//		document.getElementById('__special_script').innerText='';
	}
	var str='encodeURIComponent=1';
	var es = form1.elements;
	for (var i=0;i<es.length;i++){
//		if(es[i].name=='co_id') alert('co_id='+es[i].value);
		var value1=encodeURIComponent(es[i].value);
		if(es[i].name=='') continue;
		if(es[i].type=="checkbox") { if(!es[i].checked) continue;}
		if(es[i].type=="radio") { if(!es[i].checked) continue;}
		str=str+'&'+es[i].name+'='+value1;
	}
//	alert(str);
	var index = -1;
	for (var i=0;i<ajax_target.length && index==-1;i++){
		if (ajax_target[i]==null){
			index = i;
		}
	}
	if (index==-1){
		index = ajax_target.length;
	}
	ajax_target[index]=targets;

	var url=form1.action;
//	var xmlDoc;
	if (window.XMLHttpRequest)
	{
		xmlDoc[index] = new XMLHttpRequest();
		xmlDoc[index].onreadystatechange=function(){monitor1(index,targets);}
		xmlDoc[index].open("POST", url, true);
		xmlDoc[index].setRequestHeader("Content-Type","application/x-www-form-urlencoded");
		xmlDoc[index].send(str);
	} else if (window.ActiveXObject) { // IE
		isIE = true;
		try {
			xmlDoc[index] = new ActiveXObject ("Msxml2.XMLHTTP");
		} catch (e) {
			xmlDoc[index] = new ActiveXObject ("Microsoft.XMLHTTP");
		}

		if (xmlDoc[index])
		{
			xmlDoc[index].onreadystatechange=function(){monitor1(index,targets);}
			xmlDoc[index].open ("POST", url, true);
			xmlDoc[index].setRequestHeader("Content-Type","application/x-www-form-urlencoded");
			xmlDoc[index].send(str);
		}
	}
	/*
	var target1=document.getElementById(targets);
	if(target1==null){
		alert('Targets '+targets+' not found!');
	} else {
		target1.innerHTML=xmlDoc.responseText;
	}
	*/
	return false;
}

function getVersion() {
	var nAgt = navigator.userAgent;
	var fullVersion  = 0;
	if ((verOffset=nAgt.indexOf("MSIE"))!=-1) {
		fullVersion  = parseFloat(nAgt.substring(verOffset+5));
	}
	return fullVersion;
}

var fullVersion=getVersion();

//20191005 THOMAS EASON begin:簡化查詢
endnum1=new Array();
endnum1[0]=["當日重大訊息", "歷史重大訊息", "最近三月歷史重大訊息"];	
endnum1[1]=["合併資產負債表", "合併損益表", "合併現金流量表", "合併股東權益變動表", "合併會計師查核(核閱)報告", "合併財務報告書"];	
endnum1[2]=["毛利率", "存貨週轉率", "應收帳款週轉率", "財務分析資料"];			
endnum1[3]=["赴大陸投資資訊(實際數)", "赴大陸投資資訊(自結數)", "赴大陸投資實際數與自結數差異公告"];
endnum1[4]=["年報及股東會相關資料", "股東常會及股利分派情形-經董事會通過擬議者適用", "股東常會及股利分派情形-經股東會確認後適用", "員工紅利之經理人姓名及配發情形", "召開股東常(臨時)會及受益人大會(94.5.5後之上市櫃公司)", "決定分配股息及紅利或其他利益(94.5.5後之上市櫃公司)"];
endnum1[5]=["董監事持股餘額明細資料", "董監事股權異動統計彙總表"];
endnum1[6]=["合併資產負債表", "合併綜合損益表", "合併現金流量表", "合併權益變動表", "合併會計師查核(核閱)報告", "合併財務報告書"];

endnum2=new Array();
endnum2[0]=["外國發行人重大訊息(一)(98年10月30日以前)", "外國發行人重大訊息(二)"];
endnum2[1]=["毛利率", "存貨週轉率", "應收帳款週轉率", "財務分析資料", "營益分析表"];
endnum2[2]=["年報及股東會相關資料(含存託憑證資料)", "股東常會及股利分派情形-經董事會通過擬議者適用", "股東常會及股利分派情形-經股東會確認後適用", "召開股東常(臨時)會及受益人大會(94.5.5後之上市櫃公司)", "決定分配股息及紅利或其他利益(94.5.5後之上市櫃公司)"];
endnum2[3]=["採用IFRS後-合併資產負債表", "採用IFRS後-合併綜合損益表", "採用IFRS後-合併現金流量表", "採用IFRS前-合併資產負債表", "採用IFRS前-合併損益表"];


arallmonth=new Array();
arallmonth[0]=["全年度","一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"];
arallmonth[1]=["all","01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"];

armonth=new Array();
armonth[0]=["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"];
armonth[1]=["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"];

ardate=new Array();
ardate[0]=["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"];
ardate[1]=["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"];

arseason=new Array();
arseason[0]=["第一季", "第二季", "第三季", "第四季"];
arseason[1]=["01", "02", "03", "04"];

function mainQuery(index){
	var TYPEK = document.form1.TYPEK.value;

	getaction(index , 0)
	deleteQ();

	if(index == 2){
		document.getElementById("div_member").style.visibility="visible";
		for(var i=0;i<endnum1[0].length;i++){
			document.myForm.member.options[i]=new Option(endnum1[0][i], i);	// 設定新選項
			document.myForm.member.length=endnum1[0].length;
		}
		defaultQuery(index , 0);

	//}else if(index == 3 || index == 4){
	}else if(index == 8 || index == 9){
		document.getElementById("div_member").style.visibility="visible";
		for(var i=0;i<endnum1[1].length;i++){
			document.myForm.member.options[i]=new Option(endnum1[1][i], i);	// 設定新選項
			document.myForm.member.length=endnum1[1].length;
		}
		defaultQuery(index , 1);
	//}else if(index == 5){
	}else if(index == 10){
		document.myForm.member.length=0;
		document.getElementById("div_member").style.visibility="hidden";
		defaultQuery(index , 0);
	//}else if(index == 6){
	}else if(index == 11){
		document.getElementById("div_member").style.visibility="visible";
		for(var i=0;i<endnum1[2].length;i++){
			document.myForm.member.options[i]=new Option(endnum1[2][i], i);	// 設定新選項
			document.myForm.member.length=endnum1[2].length;
		}
		defaultQuery(index , 2);
	//}else if(index == 7){
	}else if(index == 12){
		document.myForm.member.length=0;
		document.getElementById("div_member").style.visibility="hidden";
		defaultQuery(index , 0);
	//}else if(index == 8){
	}else if(index == 13){
		document.myForm.member.length=0;
		document.getElementById("div_member").style.visibility="hidden";
		defaultQuery(index , 0);
	//}else if(index == 9){
	}else if(index == 14){
		document.myForm.member.length=0;
		document.getElementById("div_member").style.visibility="hidden";
		defaultQuery(index , 0);
	//}else if(index == 10){
	}else if(index == 7){
		document.getElementById("div_member").style.visibility="visible";
		for(var i=0;i<endnum1[4].length;i++){
			document.myForm.member.options[i]=new Option(endnum1[4][i], i);	// 設定新選項
			document.myForm.member.length=endnum1[4].length;
		}
		defaultQuery(index , 10);
	//}else if(index == 12){
	}else if(index == 4){
		document.getElementById("div_member").style.visibility="visible";
		for(var i=0;i<endnum1[5].length;i++){
			document.myForm.member.options[i]=new Option(endnum1[5][i], i);	// 設定新選項
			document.myForm.member.length=endnum1[5].length;
		}
		defaultQuery(index , 0);
	//}else if(index == 13){
	}else if(index == 6){
		defaultQuery(index , 0);
	}else{
		document.myForm.member.length=0;
		document.getElementById("div_member").style.visibility="hidden";
	}


	(b = document.createElement("button")).innerHTML = "查詢";
	b.id = "qbtn";
	if(index == 1){
		b.setAttribute("onclick", "showtable01();ajax1(this.form,\"table01\");");
		document.getElementById("spbtn0").appendChild(b);
	//}else if(index == 9){
	}else if(index == 14){
		b.setAttribute("onclick", "submit();");
		document.getElementById("spbtn").appendChild(b);
	//}else if(index == 10){
	}else if(index == 7){
		b.setAttribute("onclick", "submit();");
		document.getElementById("spbtn").appendChild(b);
	//}else if(index == 11){
	}else if(index == 3 || index == 5){
		b.setAttribute("onclick", "showtable01();ajax1(this.form,\"table01\");");
		document.getElementById("spbtn0").appendChild(b);
	}else{
		b.setAttribute("onclick", "showtable01();ajax1(this.form,\"table01\");");
		document.getElementById("spbtn").appendChild(b);
	}
}

function getaction(index , subIndex){
	var TYPEK = document.form1.TYPEK.value;

	if(index == 1){
		document.myForm.action ="/mops/web/ajax_t05st03";
		document.myForm.step.value="1";

	}else if(index == 2){
		if(subIndex == 0){
			document.myForm.action ="ajax_t05st02";
			document.myForm.off.value="1";
		}else if(subIndex == 1){
			document.myForm.action ="ajax_t05st01";
			document.myForm.step.value="1A";
			document.myForm.off.value="1";
		}else if(subIndex == 2){
			document.myForm.action ="/mops/web/ajax_t21sc05";
			document.myForm.step.value="1";
			document.myForm.type.value="0";
		}
	}else if(index == 3){
		document.myForm.action ="ajax_t114sb09"; 
		document.myForm.step.value="1";
	//}else if(index == 3){
	}else if(index == 8){
		if(subIndex == 0){
			document.myForm.action ="ajax_t05st33";
			document.myForm.off.value="1";
			document.myForm.isnew.value="false";
		}else if(subIndex == 1){
			document.myForm.action ="ajax_t05st34";
			document.myForm.off.value="1";
			document.myForm.isnew.value="false";
		}else if(subIndex == 2){
			document.myForm.action ="ajax_t05st39";
			document.myForm.off.value="1";
			document.myForm.isnew.value="false";
		}else if(subIndex == 3){
			document.myForm.action ="ajax_t05st38";
			document.myForm.off.value="1";
			document.myForm.isnew.value="false";
		}else if(subIndex == 4){
			document.myForm.action ="ajax_t05st40";
			document.myForm.off.value="1";
			document.myForm.isnew.value="false";
		}else if(subIndex == 5){
			document.myForm.action ="https://doc.twse.com.tw/server-java/t57sb01";
			document.myForm.target="_blank";
			document.myForm.step.value="1";
			document.myForm.mtype.value="A";
			document.myForm.dtype.value="A02";
		}
	//}else if(index == 4){
	}else if(index == 9){
		if(subIndex == 0){
			document.myForm.action ="ajax_t164sb03";
			document.myForm.step.value="1";
			document.myForm.off.value="1";
			document.myForm.isnew.value="false";
		}else if(subIndex == 1){
			document.myForm.action ="ajax_t164sb04";
			document.myForm.step.value="1";
			document.myForm.off.value="1";
			document.myForm.isnew.value="false";
		}else if(subIndex == 2){
			document.myForm.action ="ajax_t164sb05";
			document.myForm.step.value="1";
			document.myForm.off.value="1";
			document.myForm.isnew.value="false";
		}else if(subIndex == 3){
			document.myForm.action ="ajax_t164sb06";
			document.myForm.step.value="1";
			document.myForm.off.value="1";
			document.myForm.isnew.value="false";
		}else if(subIndex == 4){
			document.myForm.action ="ajax_t163sb03";
			document.myForm.step.value="1";
			document.myForm.off.value="1";
			document.myForm.isnew.value="false";
		}else if(subIndex == 5){
			document.myForm.action ="https://doc.twse.com.tw/server-java/t57sb01";
			document.myForm.target="_blank";
			document.myForm.step.value="1";
			document.myForm.mtype.value="A";
			document.myForm.dtype.value="A02";
		}
	//}else if(index == 5){
	}else if(index == 10){
		document.myForm.action ="ajax_t05st10_ifrs";
		document.myForm.isnew.value="false";
	//}else if(index == 6){
	}else if(index == 11){
		if(subIndex == 0){
			document.myForm.action ="ajax_t05st27";
		}else if(subIndex == 1){
			document.myForm.action ="ajax_t05st25"; 
		}else if(subIndex == 2){
			document.myForm.action ="ajax_t05st26"; 
		}else if(subIndex == 3){
			document.myForm.action ="ajax_t05st22";
			document.myForm.step.value="1";
		}
		document.myForm.off.value="1";
		document.myForm.isnew.value="false";
	//}else if(index == 7){
	}else if(index == 12){
		document.myForm.off.value="1";
		document.myForm.isnew.value="false";
		document.myForm.step.value="1";
		if(TYPEK == 'rotc'){
			document.myForm.action ="ajax_t05st11"; 
		}else{
			document.myForm.action ="ajax_t65sb04"; 
		}
	//}else if(index == 8){
	}else if(index == 13){
		document.myForm.action ="ajax_t15sf"; 
		document.myForm.isnew.value="false";
		document.myForm.statef.value="100";
	//}else if(index == 9){
	}else if(index == 14){
		document.myForm.action ="https://doc.twse.com.tw/server-java/t57sb01";
		document.myForm.target="_blank";
		document.myForm.step.value="1";
		document.myForm.mtype.value="B";
	//}else if(index == 10){
	}else if(index == 7){
		if(subIndex == 0){
			document.myForm.action ="https://doc.twse.com.tw/server-java/t57sb01";
			document.myForm.target="_blank";
			document.myForm.step.value="1";
			document.myForm.mtype.value="F";
		}else if(subIndex == 1){
			document.myForm.action ="ajax_t05st09_1";
			document.myForm.off.value="1";
			document.myForm.isnew.value="false";
		}else if(subIndex == 2){
			document.myForm.action ="ajax_t05st09";
			document.myForm.off.value="1";
			document.myForm.isnew.value="false";
		}else if(subIndex == 3){
			document.myForm.action ="/mops/web/ajax_t114sb07";
			document.myForm.step.value="00";
		}else if(subIndex == 4){
			document.myForm.action ="ajax_t108sb16";
			document.myForm.step.value="1";
			document.myForm.isnew.value="false";
		}else if(subIndex == 5){
			document.myForm.action ="ajax_t108sb19";
			document.myForm.step.value="1";
			document.myForm.isnew.value="false";
		}
	//}else if(index == 11){
	}else if(index == 5){
		document.myForm.action ="ajax_t100sb07"; 
		document.myForm.step.value="1";
	//}else if(index == 12){
	}else if(index == 4){
		if(subIndex == 0){
			document.myForm.action ="ajax_stapap1";
			document.myForm.step.value="1";
			document.myForm.off.value="1";
		}else if(subIndex == 1){
			document.myForm.action ="ajax_t132sb04";
			document.myForm.step.value ="0";
			document.myForm.bitem.value ="11";
			document.myForm.sitem.value ="1";
		}
	//}else if(index == 13){
	}else if(index == 6){
		document.myForm.action ="ajax_t16sn02";
		document.myForm.step.value ="2a";
		document.myForm.off.value ="1";
		document.myForm.isnew.value ="false";
	}
}

function defaultQuery(index , subIndex){
	var Today = new Date();

	//if(index == 2 || index == 3 || index == 4 || index == 5 || index == 6 || index == 7 || index == 8 || index == 9 || index == 10 || index == 12 || index == 13){
	if(index == 2 || index == 8 || index == 9 || index == 10 || index == 11 || index == 12 || index == 13 || index == 14 || index == 7 || index == 4 || index == 6){
		document.getElementById("spYear").innerHTML='<b>年度 : </b>';
		yytext = document.createElement("input");
		yytext.type = 'text';
		yytext.size = 3;
		//if(index == 10){
		if(index == 7){
			yytext.id='YEAR';
			yytext.name='YEAR';
		}else{
			yytext.id='year';
			yytext.name='year';
		}
		yytext.value= Today.getFullYear()-1911;
		document.getElementById("spYear").appendChild(yytext);
	}

	
	//if( (index == 2 && subIndex == 0)  || (index == 5) || (index == 7) || (index == 8) || (index == 10 && subIndex == 4) || (index == 11 && (subIndex == 0 || subIndex == 1)) ){
	if( (index == 2 && subIndex == 0)  || (index == 10) || (index == 12) || (index == 13) || (index == 7 && subIndex == 4) || (index == 5 && (subIndex == 0 || subIndex == 1)) || (index == 4)){
		document.getElementById("spMonth").innerHTML='<b>月份 : </b>';
		var mmSelect = document.createElement("select");
		//if(index == 10 && subIndex == 4){
		if(index == 7 && subIndex == 4){
			mmSelect.id = "MONTH";
			mmSelect.name = "MONTH";
			document.getElementById("spMonth").appendChild(mmSelect);
			for(var i=0;i<armonth[0].length;i++){
				document.myForm.MONTH.options[i]=new Option(armonth[0][i], armonth[1][i]);	// 設定新選項
				document.myForm.MONTH.length=armonth[0].length;
			}
		}else{
			mmSelect.id = "month";
			mmSelect.name = "month";
			document.getElementById("spMonth").appendChild(mmSelect);
			for(var i=0;i<armonth[0].length;i++){
				document.myForm.month.options[i]=new Option(armonth[0][i], armonth[1][i]);	// 設定新選項
				document.myForm.month.length=armonth[0].length;
			}
		}
	}

	//if( (index == 2 && subIndex == 1) || (index == 10 && subIndex == 5) ){
	if( (index == 2 && subIndex == 1) || (index == 7 && subIndex == 5) ){
		document.getElementById("spMonth").innerHTML='<b>月份 : </b>';
		var mmSelect = document.createElement("select");
		//if(index == 10 && subIndex == 5){
		if(index == 7 && subIndex == 5){
			mmSelect.id = "MONTH";
			mmSelect.name = "MONTH";
			document.getElementById("spMonth").appendChild(mmSelect);
			for(var i=0;i<arallmonth[0].length;i++){
				document.myForm.MONTH.options[i]=new Option(arallmonth[0][i], arallmonth[1][i]);	// 設定新選項
				document.myForm.MONTH.length=arallmonth[0].length;
			}
		}else{
			mmSelect.id = "month";
			mmSelect.name = "month";
			document.getElementById("spMonth").appendChild(mmSelect);
			for(var i=0;i<arallmonth[0].length;i++){
				document.myForm.month.options[i]=new Option(arallmonth[0][i], arallmonth[1][i]);	// 設定新選項
				document.myForm.month.length=arallmonth[0].length;
			}
		}
	}

	if( (index == 2 && subIndex == 0) ){
		document.getElementById("spDay").innerHTML='<b>日期 : </b>';
		var ddSelect = document.createElement("select");
		ddSelect.id = "day";
		ddSelect.name = "day";
		document.getElementById("spDay").appendChild(ddSelect);
		for(var i=0;i<ardate[0].length;i++){
			document.myForm.day.options[i]=new Option(ardate[0][i], ardate[1][i]);	// 設定新選項
			document.myForm.day.length=ardate[0].length;
		}
	}

	//if( ((index == 3 || index == 4) && (subIndex == 0 || subIndex == 1 || subIndex == 2 || subIndex == 3 || subIndex == 4)) ){
	if( ((index == 8 || index == 9) && (subIndex == 0 || subIndex == 1 || subIndex == 2 || subIndex == 3 || subIndex == 4)) ){
		document.getElementById("spSeason").innerHTML='<b>季別 : </b>';
		var ssSelect = document.createElement("select");
		ssSelect.id = "season";
		ssSelect.name = "season";
		document.getElementById("spSeason").appendChild(ssSelect);
		for(var i=0;i<arseason[0].length;i++){
			document.myForm.season.options[i]=new Option(arseason[0][i], arseason[1][i]);	// 設定新選項
			document.myForm.season.length=arseason[0].length;
		}
	}
}

function deleteQ(index){

	var yytext = document.getElementById("year");
	if(yytext != null){
		yytext.parentNode.removeChild(yytext);
	}
	var YYtext = document.getElementById("YEAR");
	if(YYtext != null){
		YYtext.parentNode.removeChild(YYtext);
	}
	var mmSelect = document.getElementById("month");
	if(mmSelect != null){
		mmSelect.parentNode.removeChild(mmSelect);
	}
	var MMSelect = document.getElementById("MONTH");
	if(MMSelect != null){
		MMSelect.parentNode.removeChild(MMSelect);
	}
	var ddSelect = document.getElementById("day");
	if(ddSelect != null){
		ddSelect.parentNode.removeChild(ddSelect);
	}
	var SDSelect = document.getElementById("SDAY");
	if(SDSelect != null){
		SDSelect.parentNode.removeChild(SDSelect);
	}
	var EDSelect = document.getElementById("EDAY");
	if(EDSelect != null){
		EDSelect.parentNode.removeChild(EDSelect);
	}
	var bdSelect = document.getElementById("b_date");
	if(bdSelect != null){
		bdSelect.parentNode.removeChild(bdSelect);
	}
	var edSelect = document.getElementById("e_date");
	if(edSelect != null){
		edSelect.parentNode.removeChild(edSelect);
	}
	var ssSelect = document.getElementById("season");
	if(ssSelect != null){
		ssSelect.parentNode.removeChild(ssSelect);
	}
	var spYear = document.getElementById("spYear");
	if(spYear != null){
		document.getElementById("spYear").innerHTML='';
	}
	var spMonth = document.getElementById("spMonth");
	if(spMonth != null){
		document.getElementById("spMonth").innerHTML='';
	}
	var spDay = document.getElementById("spDay");
	if(spDay != null){
		document.getElementById("spDay").innerHTML='';
	}
	var spSeason = document.getElementById("spSeason");
	if(spSeason != null){
		document.getElementById("spSeason").innerHTML='';
	}
	var spbDate = document.getElementById("spbDate");
	if(spbDate != null){
		document.getElementById("spbDate").innerHTML='';
	}
	var speDate = document.getElementById("speDate");
	if(speDate != null){
		document.getElementById("speDate").innerHTML='';
	}
	var qbtn = document.getElementById("qbtn");
	if(qbtn != null){
		qbtn.parentNode.removeChild(qbtn);
	}
}

function Query(subIndex){
	deleteQ();

	var Today = new Date();
	var index = document.getElementById("MainProject").selectedIndex;

	getaction(index , subIndex);

	//if( (index == 2 && (subIndex == 0 || subIndex == 1)) 
	//	|| (index == 3) || (index == 4) || (index == 5) || (index == 6) || (index == 7) || (index == 8) || (index == 9) 
	//	|| (index == 10 && (subIndex == 0 || subIndex == 1 || subIndex == 2 || subIndex == 4 || subIndex == 5)) 
	//	|| (index == 12) || (index == 13) ){
	if( (index == 2 && (subIndex == 0 || subIndex == 1)) 
		|| (index == 8) || (index == 9) || (index == 10) || (index == 11) || (index == 12) || (index == 13) || (index == 14) 
		|| (index == 7 && (subIndex == 0 || subIndex == 1 || subIndex == 2 || subIndex == 4 || subIndex == 5)) 
		|| (index == 4) || (index == 6) ){
		document.getElementById("spYear").innerHTML='<b>年度 : </b>';
		yytext = document.createElement("input");
		yytext.type = 'text';
		yytext.size = 3;
		//if(index == 10 & (subIndex == 4 || subIndex == 5)){
		if(index == 7 & (subIndex == 4 || subIndex == 5)){
			yytext.id='YEAR';
			yytext.name='YEAR';
		}else{
			yytext.id='year';
			yytext.name='year';
		}
		yytext.value= Today.getFullYear()-1911;
		document.getElementById("spYear").appendChild(yytext);
	}

	//if( (index == 2 && subIndex == 0) 
	//	|| (index == 5) || (index == 7) || (index == 8) 
	//	|| (index == 10 && subIndex == 4) 
	//	|| (index == 12 && (subIndex == 0 || subIndex == 1)) ){
	if( (index == 2 && subIndex == 0) 
		|| (index == 10) || (index == 12) || (index == 13) 
		|| (index == 7 && subIndex == 4) 
		|| (index == 4 && (subIndex == 0 || subIndex == 1)) ){
		document.getElementById("spMonth").innerHTML='<b>月份 : </b>';
		var mmSelect = document.createElement("select");
		//if(index == 10 && subIndex == 4){
		if(index == 7 && subIndex == 4){
			mmSelect.id = "MONTH";
			mmSelect.name = "MONTH";
			document.getElementById("spMonth").appendChild(mmSelect);
			for(var i=0;i<armonth[0].length;i++){
				document.myForm.MONTH.options[i]=new Option(armonth[0][i], armonth[1][i]);	// 設定新選項
				document.myForm.MONTH.length=armonth[0].length;
			}
		}else{
			mmSelect.id = "month";
			mmSelect.name = "month";
			document.getElementById("spMonth").appendChild(mmSelect);
			for(var i=0;i<armonth[0].length;i++){
				document.myForm.month.options[i]=new Option(armonth[0][i], armonth[1][i]);	// 設定新選項
				document.myForm.month.length=armonth[0].length;
			}
		}
		
	}

	//if( (index == 2 && subIndex == 1) || (index == 10 && subIndex == 5) ){
	if( (index == 2 && subIndex == 1) || (index == 7 && subIndex == 5) ){
		document.getElementById("spMonth").innerHTML='<b>月份 : </b>';
		var mmSelect = document.createElement("select");
		//if(index == 10 && subIndex == 5){
		if(index == 7 && subIndex == 5){
			mmSelect.id = "MONTH";
			mmSelect.name = "MONTH";
			document.getElementById("spMonth").appendChild(mmSelect);
			for(var i=0;i<arallmonth[0].length;i++){
				document.myForm.MONTH.options[i]=new Option(arallmonth[0][i], arallmonth[1][i]);	// 設定新選項
				document.myForm.MONTH.length=arallmonth[0].length;
			}
		}else{
			mmSelect.id = "month";
			mmSelect.name = "month";
			document.getElementById("spMonth").appendChild(mmSelect);
			for(var i=0;i<arallmonth[0].length;i++){
				document.myForm.month.options[i]=new Option(arallmonth[0][i], arallmonth[1][i]);	// 設定新選項
				document.myForm.month.length=arallmonth[0].length;
			}
		}
	}

	if( (index == 2 && subIndex == 0) ){
		document.getElementById("spDay").innerHTML='<b>日期 : </b>';
		var ddSelect = document.createElement("select");
		ddSelect.id = "day";
		ddSelect.name = "day";
		document.getElementById("spDay").appendChild(ddSelect);
		for(var i=0;i<ardate[0].length;i++){
			document.myForm.day.options[i]=new Option(ardate[0][i], ardate[1][i]);	// 設定新選項
			document.myForm.day.length=ardate[0].length;
		}
	}

	//if( (index == 2 && subIndex == 1)
	//	|| (index == 10 && (subIndex == 4 || subIndex == 5)) ){
	if( (index == 2 && subIndex == 1)
		|| (index == 7 && (subIndex == 4 || subIndex == 5)) ){
		document.getElementById("spbDate").innerHTML='<b>日期 : </b>';
		var bdSelect = document.createElement("select");
		//if(index == 10 && (subIndex == 4 || subIndex == 5)){
		if(index == 7 && (subIndex == 4 || subIndex == 5)){
			bdSelect.id = "SDAY";
			bdSelect.name = "SDAY";
			document.getElementById("spbDate").appendChild(bdSelect);
			for(var i=0;i<ardate[0].length;i++){
				document.myForm.SDAY.options[i]=new Option(ardate[0][i], ardate[1][i]);	// 設定新選項
				document.myForm.SDAY.length=ardate[0].length;
			}
		}else{
			bdSelect.id = "bdate";
			bdSelect.name = "bdate";
			document.getElementById("spbDate").appendChild(bdSelect);
			for(var i=0;i<ardate[0].length;i++){
				document.myForm.bdate.options[i]=new Option(ardate[0][i], ardate[1][i]);	// 設定新選項
				document.myForm.bdate.length=ardate[0].length;
			}
		}
		

		document.getElementById("speDate").innerHTML='<b> ~ </b>';
		var edSelect = document.createElement("select");
		//if(index == 10 && (subIndex == 4 || subIndex == 5)){
		if(index == 7 && (subIndex == 4 || subIndex == 5)){
			edSelect.id = "EDAY";
			edSelect.name = "EDAY";
			document.getElementById("speDate").appendChild(edSelect);
			for(var i=0;i<ardate[0].length;i++){
				document.myForm.EDAY.options[i]=new Option(ardate[0][i], ardate[1][i]);	// 設定新選項
				document.myForm.EDAY.length=ardate[0].length;
			}
		}else{
			edSelect.id = "edate";
			edSelect.name = "edate";
			document.getElementById("speDate").appendChild(edSelect);
			for(var i=0;i<ardate[0].length;i++){
				document.myForm.edate.options[i]=new Option(ardate[0][i], ardate[1][i]);	// 設定新選項
				document.myForm.edate.length=ardate[0].length;
			}
		}
		
	}

	//if( ((index == 3 || index == 4) && (subIndex == 0 || subIndex == 1 || subIndex == 2 || subIndex == 3 || subIndex == 4)) ){
	if( ((index == 8 || index == 9) && (subIndex == 0 || subIndex == 1 || subIndex == 2 || subIndex == 3 || subIndex == 4)) ){
		document.getElementById("spSeason").innerHTML='<b>季別 : </b>';
		var ssSelect = document.createElement("select");
		ssSelect.id = "season";
		ssSelect.name = "season";
		document.getElementById("spSeason").appendChild(ssSelect);
		for(var i=0;i<arseason[0].length;i++){
			document.myForm.season.options[i]=new Option(arseason[0][i], arseason[1][i]);	// 設定新選項
			document.myForm.season.length=arseason[0].length;
		}
	}

	(b = document.createElement("button")).innerHTML = "查詢";
	b.id = "qbtn";
	if(index == 1){
		b.setAttribute("onclick", "showtable01();ajax1(this.form,\"table01\");");
		document.getElementById("spbtn0").appendChild(b);
	//}else if( (index == 3 && subIndex == 5) || (index == 4 && subIndex == 5) ){
	}else if( (index == 8 && subIndex == 5) || (index == 9 && subIndex == 5) ){
		(b = document.createElement("button")).innerHTML = "查詢";
		b.id = "qbtn";
		b.setAttribute("onclick", "submit();");
		document.getElementById("spbtn").appendChild(b);
	//}else if(index == 9){
	}else if(index == 14){
		b.setAttribute("onclick", "submit();");
		document.getElementById("spbtn").appendChild(b);
	//}else if(index == 10 && subIndex == 0){
	}else if(index == 7 && subIndex == 0){
		b.setAttribute("onclick", "submit();");
		document.getElementById("spbtn").appendChild(b);
	//}else if(index == 11){
	}else if(index == 3 || index == 5){
		b.setAttribute("onclick", "showtable01();ajax1(this.form,\"table01\");");
		document.getElementById("spbtn0").appendChild(b);
	}else{
		b.setAttribute("onclick", "showtable01();ajax1(this.form,\"table01\");");
		document.getElementById("spbtn").appendChild(b);
	}
}

function mainQueryTW(index){
	var TYPEK = document.form1.TYPEK.value;

	getactionTW(index , 0)
	deleteQTW();

	if(index == 2){
		document.getElementById("div_member").style.visibility="visible";
		for(var i=0;i<endnum2[0].length;i++){
			document.myForm.member.options[i]=new Option(endnum2[0][i], i);	// 設定新選項
			document.myForm.member.length=endnum2[0].length;
		}
		defaultQueryTW(index , 0);

	}else if(index == 3 || index == 4 || index == 5 || index == 8 || index == 10 || index == 11 || index == 12 || index == 13){
		document.myForm.member.length=0;
		document.getElementById("div_member").style.visibility="hidden";
		defaultQueryTW(index , 0);

	}else if(index == 6){
		document.getElementById("div_member").style.visibility="visible";
		for(var i=0;i<endnum2[3].length;i++){
			document.myForm.member.options[i]=new Option(endnum2[3][i], i);	// 設定新選項
			document.myForm.member.length=endnum2[3].length;
		}
		defaultQueryTW(index , 3);

	}else if(index == 7){
		document.getElementById("div_member").style.visibility="visible";
		for(var i=0;i<endnum2[1].length;i++){
			document.myForm.member.options[i]=new Option(endnum2[1][i], i);	// 設定新選項
			document.myForm.member.length=endnum2[1].length;
		}
		defaultQueryTW(index , 0);

	}else if(index == 9){
		document.getElementById("div_member").style.visibility="visible";
		for(var i=0;i<endnum2[2].length;i++){
			document.myForm.member.options[i]=new Option(endnum2[2][i], i);	// 設定新選項
			document.myForm.member.length=endnum2[2].length;
		}
		defaultQueryTW(index , 0);
	}else{
		document.myForm.member.length=0;
		document.getElementById("div_member").style.visibility="hidden";
	}


	(b = document.createElement("button")).innerHTML = "查詢";
	b.id = "qbtn";
	if(index == 1 || index == 4 || index == 10 || index == 12){
		b.setAttribute("onclick", "showtable01();ajax1(this.form,\"table01\");");
		document.getElementById("spbtn0").appendChild(b);
	}else{
		b.setAttribute("onclick", "showtable01();ajax1(this.form,\"table01\");");
		document.getElementById("spbtn").appendChild(b);
	}
}

function getactionTW(index , subIndex){
	var TYPEK = document.form1.TYPEK.value;
	//var ifrs = document.form1.ifrs.value;
	var ifrs = "1";

	if(index == 1){
		document.myForm.action ="/mops/web/ajax_t05st03";
		document.myForm.step.value="0";
		document.myForm.off.value="1";

	}else if(index == 2){
		if(subIndex == 0){
			document.myForm.action ="ajax_t05st01";
			document.myForm.step.value="A1";
			document.myForm.off.value="1";
		}else if(subIndex == 1){
			document.myForm.action ="ajax_t59sb01";
			document.myForm.step.value="1";
			document.myForm.isnew.value="false";
		}

	}else if(index == 3){
		document.myForm.action ="/mops/web/ajax_t16sn02";
		document.myForm.step.value="2a";
		document.myForm.off.value="1";
		document.myForm.isnew.value="false";

	}else if(index == 4){
		document.myForm.action ="ajax_t05st05";
		document.myForm.step.value="0";
		document.myForm.off.value="1";

	}else if(index == 5){
		document.myForm.action ="ajax_t57sb01_q1";
		document.myForm.step.value="1";
		document.myForm.mtype.value="A";

	}else if(index == 6){
		if(subIndex == 0){
			document.myForm.action ="ajax_t145sb01_ifrs";
			document.myForm.step.value="1";
			document.myForm.off.value="1";
		}else if(subIndex == 1){
			document.myForm.action ="ajax_t145sb02_ifrs";
			document.myForm.step.value="1";
			document.myForm.off.value="1";
		}else if(subIndex == 2){
			document.myForm.action ="ajax_t145sb05_ifrs";
			document.myForm.step.value="1";
			document.myForm.off.value="1";
		}else if(subIndex == 3){
			document.myForm.action ="ajax_t145sb01";
			document.myForm.step.value="1";
			document.myForm.off.value="1";
		}else if(subIndex == 4){
			document.myForm.action ="ajax_t145sb02";
			document.myForm.step.value="1";
			document.myForm.off.value="1";
		}

	}else if(index == 7){
		if(subIndex == 0){
			if(ifrs == '1'){
				document.myForm.action ="ajax_t05st27"; 
			}else{
				document.myForm.action ="ajax_t163sb09"; 
			}
			document.myForm.off.value="1";
			document.myForm.isnew.value="false";
		}else if(subIndex == 1){
			if(ifrs == '1'){
				document.myForm.action ="ajax_t05st25"; 
			}else{
				document.myForm.action ="ajax_t05st25_q1"; 
			}
			document.myForm.off.value="1";
			document.myForm.isnew.value="false";
		}else if(subIndex == 2){
			if(ifrs == '1'){
				document.myForm.action ="ajax_t05st26"; 
			}else{
				document.myForm.action ="ajax_t05st26_q1"; 
			}
			document.myForm.off.value="1";
			document.myForm.isnew.value="false";
		}else if(subIndex == 3){
			if(ifrs == '1'){
				document.myForm.action ="ajax_t05st22"; 
			}else{
				document.myForm.action ="ajax_t05st22_q1"; 
			}
			document.myForm.step.value="0";
			document.myForm.off.value="1";
			document.myForm.isnew.value="false";
		}else if(subIndex == 4){
			if(ifrs == '1'){
				document.myForm.action ="ajax_t05st24"; 
			}else{
				document.myForm.action ="ajax_t163sb08"; 
			}
			document.myForm.step.value="0";
			document.myForm.off.value="1";
			document.myForm.isnew.value="false";
		}
	}else if(index == 8){
		document.myForm.action ="ajax_t57sb01_q3";
		document.myForm.step.value="1";
		document.myForm.mtype.value="B";

	}else if(index == 9){
		if(subIndex == 0){
			document.myForm.action ="ajax_t57sb01_q5";
			document.myForm.step.value="1";
			document.myForm.mtype.value="F";
		}else if(subIndex == 1){
			document.myForm.action ="ajax_t05st09_1";
			document.myForm.off.value="1";
			document.myForm.isnew.value="false";
		}else if(subIndex == 2){
			document.myForm.action ="ajax_t05st09";
			document.myForm.off.value="1";
			document.myForm.isnew.value="false";
		}else if(subIndex == 3){
			document.myForm.action ="ajax_t108sb16";
			document.myForm.step.value="1";
			document.myForm.isnew.value="false";
		}else if(subIndex == 4){
			document.myForm.action ="ajax_t108sb19";
			document.myForm.step.value="1";
			document.myForm.isnew.value="false";
		}
	}else if(index == 10){
		document.myForm.action ="ajax_t93sb06";
		document.myForm.step.value="0";
		document.myForm.off.value="1";
		document.myForm.isnew.value="false";

	}else if(index == 11){
		document.myForm.action ="ajax_t119sb04"; 
		document.myForm.step.value="0";

	}else if(index == 12){
		document.myForm.action ="ajax_t132sb15";
		document.myForm.step.value="0";

	}else if(index == 13){
		document.myForm.action ="ajax_t132sb20";
		document.myForm.step.value ="0";
	}
	//alert(document.myForm.action);
}

function defaultQueryTW(index , subIndex){
	var Today = new Date();
	//alert(index);
	//alert(subIndex);

	if(index == 2 || index == 3 || index == 5 || index == 6 || index == 7 || index == 8 || index == 9 || index == 11 || index == 13){
		document.getElementById("spYear").innerHTML='<b>年度 : </b>';
		yytext = document.createElement("input");
		yytext.type = 'text';
		yytext.size = 3;
		if(index == 9 && (subIndex == 3 || subIndex == 4)){
			yytext.id='YEAR';
			yytext.name='YEAR';
		}else{
			yytext.id='year';
			yytext.name='year';
		}
		yytext.value= Today.getFullYear()-1911;
		document.getElementById("spYear").appendChild(yytext);
	}

	
	if( index == 13 ){
		document.getElementById("spMonth").innerHTML='<b>月份 : </b>';
		var mmSelect = document.createElement("select");
		mmSelect.id = "month";
		mmSelect.name = "month";
		document.getElementById("spMonth").appendChild(mmSelect);
		for(var i=0;i<armonth[0].length;i++){
			document.myForm.month.options[i]=new Option(armonth[0][i], armonth[1][i]);	// 設定新選項
			document.myForm.month.length=armonth[0].length;
		}
	}

	if( index == 2 || (index == 9 && (subIndex == 3 || subIndex == 4)) ){
		document.getElementById("spMonth").innerHTML='<b>月份 : </b>';
		var mmSelect = document.createElement("select");
		if(index == 9 && (subIndex == 3 || subIndex == 4)){
			mmSelect.id = "MONTH";
			mmSelect.name = "MONTH";
			document.getElementById("spMonth").appendChild(mmSelect);
			for(var i=0;i<arallmonth[0].length;i++){
				document.myForm.MONTH.options[i]=new Option(arallmonth[0][i], arallmonth[1][i]);	// 設定新選項
				document.myForm.MONTH.length=arallmonth[0].length;
			}
		}else{
			mmSelect.id = "month";
			mmSelect.name = "month";
			document.getElementById("spMonth").appendChild(mmSelect);
			for(var i=0;i<arallmonth[0].length;i++){
				document.myForm.month.options[i]=new Option(arallmonth[0][i], arallmonth[1][i]);	// 設定新選項
				document.myForm.month.length=arallmonth[0].length;
			}
		}
	}

	if( (index == 2 && (subIndex == 0 || subIndex == 1) ) || (index == 9 && (subIndex == 3 || subIndex == 4)) ){
		document.getElementById("spbDate").innerHTML='<b>日期 : </b>';
		var bdSelect = document.createElement("select");
		if(index == 9 && (subIndex == 3 || subIndex == 4)){
			bdSelect.id = "SDAY";
			bdSelect.name = "SDAY";
			document.getElementById("spbDate").appendChild(bdSelect);
			for(var i=0;i<ardate[0].length;i++){
				document.myForm.SDAY.options[i]=new Option(ardate[0][i], ardate[1][i]);	// 設定新選項
				document.myForm.SDAY.length=ardate[0].length;
			}
		}else{
			bdSelect.id = "bdate";
			bdSelect.name = "bdate";
			document.getElementById("spbDate").appendChild(bdSelect);
			for(var i=0;i<ardate[0].length;i++){
				document.myForm.bdate.options[i]=new Option(ardate[0][i], ardate[1][i]);	// 設定新選項
				document.myForm.bdate.length=ardate[0].length;
			}
		}
		

		document.getElementById("speDate").innerHTML='<b> ~ </b>';
		var edSelect = document.createElement("select");
		if(index == 9 && (subIndex == 3 || subIndex == 4)){
			edSelect.id = "EDAY";
			edSelect.name = "EDAY";
			document.getElementById("speDate").appendChild(edSelect);
			for(var i=0;i<ardate[0].length;i++){
				document.myForm.EDAY.options[i]=new Option(ardate[0][i], ardate[1][i]);	// 設定新選項
				document.myForm.EDAY.length=ardate[0].length;
			}
		}else{
			edSelect.id = "edate";
			edSelect.name = "edate";
			document.getElementById("speDate").appendChild(edSelect);
			for(var i=0;i<ardate[0].length;i++){
				document.myForm.edate.options[i]=new Option(ardate[0][i], ardate[1][i]);	// 設定新選項
				document.myForm.edate.length=ardate[0].length;
			}
		}
		
	}

	if( index == 7 ){
		var spradio1 = document.getElementById("spradio1");
		var spradio11 = document.getElementById("spradio11");
		var e = document.createElement("input");
		e.type = "radio";
		e.id = "ifrs";
		e.name = "ifrs";
		e.value = "1";
		spradio1.appendChild(e);
		spradio11.innerHTML += 'IFRS前 ';
		
		var spradio2 = document.getElementById("spradio2");
		var spradio21 = document.getElementById("spradio21");
		var ee = document.createElement("input");
		ee.type = "radio";
		ee.id = "ifrs";
		ee.name = "ifrs";
		ee.value = "2";
		ee.checked = "checked";
		spradio2.appendChild(ee);
		spradio21.innerHTML += 'IFRS後 ';
	}

	if( index == 6 ){
		document.getElementById("spSeason").innerHTML='<b>季別 : </b>';
		var ssSelect = document.createElement("select");
		ssSelect.id = "season";
		ssSelect.name = "season";
		document.getElementById("spSeason").appendChild(ssSelect);
		for(var i=0;i<arseason[0].length;i++){
			document.myForm.season.options[i]=new Option(arseason[0][i], arseason[1][i]);	// 設定新選項
			document.myForm.season.length=arseason[0].length;
		}
	}
}

function deleteQTW(index){
	
	var yytext = document.getElementById("year");
	if(yytext != null){
		yytext.parentNode.removeChild(yytext);
	}
	var YYtext = document.getElementById("YEAR");
	if(YYtext != null){
		YYtext.parentNode.removeChild(YYtext);
	}
	var mmSelect = document.getElementById("month");
	if(mmSelect != null){
		mmSelect.parentNode.removeChild(mmSelect);
	}
	var MMSelect = document.getElementById("MONTH");
	if(MMSelect != null){
		MMSelect.parentNode.removeChild(MMSelect);
	}
	var ddSelect = document.getElementById("day");
	if(ddSelect != null){
		ddSelect.parentNode.removeChild(ddSelect);
	}
	var SDSelect = document.getElementById("SDAY");
	if(SDSelect != null){
		SDSelect.parentNode.removeChild(SDSelect);
	}
	var EDSelect = document.getElementById("EDAY");
	if(EDSelect != null){
		EDSelect.parentNode.removeChild(EDSelect);
	}
	var bdSelect = document.getElementById("b_date");
	if(bdSelect != null){
		bdSelect.parentNode.removeChild(bdSelect);
	}
	var edSelect = document.getElementById("e_date");
	if(edSelect != null){
		edSelect.parentNode.removeChild(edSelect);
	}
	var ssSelect = document.getElementById("season");
	if(ssSelect != null){
		ssSelect.parentNode.removeChild(ssSelect);
	}
	var spYear = document.getElementById("spYear");
	if(spYear != null){
		document.getElementById("spYear").innerHTML='';
	}
	var spMonth = document.getElementById("spMonth");
	if(spMonth != null){
		document.getElementById("spMonth").innerHTML='';
	}
	var spDay = document.getElementById("spDay");
	if(spDay != null){
		document.getElementById("spDay").innerHTML='';
	}
	var spSeason = document.getElementById("spSeason");
	if(spSeason != null){
		document.getElementById("spSeason").innerHTML='';
	}
	var spbDate = document.getElementById("spbDate");
	if(spbDate != null){
		document.getElementById("spbDate").innerHTML='';
	}
	var speDate = document.getElementById("speDate");
	if(speDate != null){
		document.getElementById("speDate").innerHTML='';
	}
	var radio1 = document.getElementById("spradio1");
	if(radio1 != null){
		document.getElementById("spradio1").innerHTML='';
	}
	var radio11 = document.getElementById("spradio11");
	if(radio11 != null){
		document.getElementById("spradio11").innerHTML='';
	}
	var radio2 = document.getElementById("spradio2");
	if(radio2 != null){
		document.getElementById("spradio2").innerHTML='';
	}
	var radio21 = document.getElementById("spradio21");
	if(radio21 != null){
		document.getElementById("spradio21").innerHTML='';
	}
	var ifrs = document.getElementById("ifrs");
	if(ifrs != null){
		ifrs.parentNode.removeChild(ifrs);
	}
	
	var qbtn = document.getElementById("qbtn");
	if(qbtn != null){
		qbtn.parentNode.removeChild(qbtn);
	}
}

function QueryTW(subIndex){
	deleteQTW();

	var Today = new Date();
	var index = document.getElementById("MainProject").selectedIndex;

	//alert(index);
	//alert(subIndex);
	getactionTW(index , subIndex);

	if(index == 2 || index == 3 || index == 5 || index == 6 || index == 7 || index == 8 || index == 9 || index == 11 || index == 13){
		document.getElementById("spYear").innerHTML='<b>年度 : </b>';
		yytext = document.createElement("input");
		yytext.type = 'text';
		yytext.size = 3;
		if(index == 9 & (subIndex == 3 || subIndex == 4)){
			yytext.id='YEAR';
			yytext.name='YEAR';
		}else{
			yytext.id='year';
			yytext.name='year';
		}
		yytext.value= Today.getFullYear()-1911;
		document.getElementById("spYear").appendChild(yytext);
	}

	if( index == 13 ){
		document.getElementById("spMonth").innerHTML='<b>月份 : </b>';
		var mmSelect = document.createElement("select");
		mmSelect.id = "month";
		mmSelect.name = "month";
		document.getElementById("spMonth").appendChild(mmSelect);
		for(var i=0;i<armonth[0].length;i++){
			document.myForm.month.options[i]=new Option(armonth[0][i], armonth[1][i]);	// 設定新選項
			document.myForm.month.length=armonth[0].length;
		}
	}

	if( index == 2 || (index == 9 && (subIndex == 3 || subIndex == 4)) ){
		document.getElementById("spMonth").innerHTML='<b>月份 : </b>';
		var mmSelect = document.createElement("select");
		if(index == 9 && (subIndex == 3 || subIndex == 4)){
			mmSelect.id = "MONTH";
			mmSelect.name = "MONTH";
			document.getElementById("spMonth").appendChild(mmSelect);
			for(var i=0;i<arallmonth[0].length;i++){
				document.myForm.MONTH.options[i]=new Option(arallmonth[0][i], arallmonth[1][i]);	// 設定新選項
				document.myForm.MONTH.length=arallmonth[0].length;
			}
		}else{
			mmSelect.id = "month";
			mmSelect.name = "month";
			document.getElementById("spMonth").appendChild(mmSelect);
			for(var i=0;i<arallmonth[0].length;i++){
				document.myForm.month.options[i]=new Option(arallmonth[0][i], arallmonth[1][i]);	// 設定新選項
				document.myForm.month.length=arallmonth[0].length;
			}
		}
	}

	if( (index == 2 && (subIndex == 0 || subIndex == 1) ) || (index == 9 && (subIndex == 3 || subIndex == 4)) ){
		document.getElementById("spbDate").innerHTML='<b>日期 : </b>';
		var bdSelect = document.createElement("select");
		if(index == 9 && (subIndex == 3 || subIndex == 4)){
			bdSelect.id = "SDAY";
			bdSelect.name = "SDAY";
			document.getElementById("spbDate").appendChild(bdSelect);
			for(var i=0;i<ardate[0].length;i++){
				document.myForm.SDAY.options[i]=new Option(ardate[0][i], ardate[1][i]);	// 設定新選項
				document.myForm.SDAY.length=ardate[0].length;
			}
		}else{
			bdSelect.id = "bdate";
			bdSelect.name = "bdate";
			document.getElementById("spbDate").appendChild(bdSelect);
			for(var i=0;i<ardate[0].length;i++){
				document.myForm.bdate.options[i]=new Option(ardate[0][i], ardate[1][i]);	// 設定新選項
				document.myForm.bdate.length=ardate[0].length;
			}
		}
		

		document.getElementById("speDate").innerHTML='<b> ~ </b>';
		var edSelect = document.createElement("select");
		if(index == 9 && (subIndex == 3 || subIndex == 4)){
			edSelect.id = "EDAY";
			edSelect.name = "EDAY";
			document.getElementById("speDate").appendChild(edSelect);
			for(var i=0;i<ardate[0].length;i++){
				document.myForm.EDAY.options[i]=new Option(ardate[0][i], ardate[1][i]);	// 設定新選項
				document.myForm.EDAY.length=ardate[0].length;
			}
		}else{
			edSelect.id = "edate";
			edSelect.name = "edate";
			document.getElementById("speDate").appendChild(edSelect);
			for(var i=0;i<ardate[0].length;i++){
				document.myForm.edate.options[i]=new Option(ardate[0][i], ardate[1][i]);	// 設定新選項
				document.myForm.edate.length=ardate[0].length;
			}
		}
		
	}

	if( index == 6 ){
		document.getElementById("spSeason").innerHTML='<b>季別 : </b>';
		var ssSelect = document.createElement("select");
		ssSelect.id = "season";
		ssSelect.name = "season";
		document.getElementById("spSeason").appendChild(ssSelect);
		for(var i=0;i<arseason[0].length;i++){
			document.myForm.season.options[i]=new Option(arseason[0][i], arseason[1][i]);	// 設定新選項
			document.myForm.season.length=arseason[0].length;
		}
	}

	if( index == 7 ){
		var spradio1 = document.getElementById("spradio1");
		var spradio11 = document.getElementById("spradio11");
		var e = document.createElement("input");
		e.type = "radio";
		e.id = "ifrs";
		e.name = "ifrs";
		e.value = "1";
		spradio1.appendChild(e);
		spradio11.innerHTML += 'IFRS前 ';
		
		var spradio2 = document.getElementById("spradio2");
		var spradio21 = document.getElementById("spradio21");
		var ee = document.createElement("input");
		ee.type = "radio";
		ee.id = "ifrs";
		ee.name = "ifrs";
		ee.value = "2";
		ee.checked = "checked";
		spradio2.appendChild(ee);
		spradio21.innerHTML += 'IFRS後 ';
	}

	(b = document.createElement("button")).innerHTML = "查詢";
	b.id = "qbtn";
	if(index == 1 || index == 4 || index == 10 || index == 12){
		b.setAttribute("onclick", "showtable01();ajax1(this.form,\"table01\");");
		document.getElementById("spbtn0").appendChild(b);
	}else{
		b.setAttribute("onclick", "showtable01();ajax1(this.form,\"table01\");");
		document.getElementById("spbtn").appendChild(b);
	}
}
//20191005 THOMAS EASON end:簡化查詢

//2010/08/12:edward:begin:為了連動下拉選單,再增加一個function讓它可以直接把form送出
	function isAction() {
		var tmpfileName = ["t123sb09_q1","t123sb10_q1","t123sb09_q2","t123sb10_q2","p_t112sb02_q1",
									"p_t112sb02_q2","t127sb00_q1","t127sb00_q2","p_t56sb25","t129sb01","t127sb00",
									"t198sb04_q1","t198sb04","t123sb00_q1","t123sb00"
									];
		var file_name = document.fh.funcName.value;
		var nedAction = true;
		for (var i=0;i<tmpfileName.length;i++){
			if (tmpfileName[i] == file_name){
				nedAction = false;
			}
		}
		var typek = document.form1.TYPEK;
//		alert(typek.value);
		if (nedAction){
			if (typek != null || typek != undefined){
				ajax1(document.form1,'search');
			}
		}
	}
//2010/08/12:edward:end:為了連動下拉選單,再增加一個function讓它可以直接把form送出

/*
function changeZoom() {
	var nAgt = navigator.userAgent;
	var fullVersion  = 0;
	var fileName = document.fh.funcName.value;
	if ((verOffset=nAgt.indexOf("MSIE"))!=-1) {
		fullVersion  = parseFloat(nAgt.substring(verOffset+5));
	}
	//if (fullVersion=="7" && fileName == "t51sb01"){
	if (fullVersion=="7"){
		doZoom("fontSize2");
	}
}
*/

/*20160204 EDWARD ALICE 自動完成*/
var keyIndex = 0;
var oldAction = "";
function chkKeyDown(form , obj , targetDiv , evnt){
	if ( obj.value.length == 0 ){
		return false;
	}

	document.getElementById('oldKeyWord').value = obj.value;

	var thisAction = form.action;
	if ( thisAction.indexOf("auto") == -1 ){
		oldAction = thisAction;
	}

	var keyCode;
	if ( window.event ){
		keyCode = event.keyCode;
	}else{
		keyCode = evnt.which;
	}

	if ( keyCode == 13 ){//ENTER
		if ( obj == document.getElementById("keyword") ){
			form.action="/mops/web/autoAction";
			hideDiv2(obj);
			form.submit();
		}else{
			form.action = oldAction;
			hideDiv2(obj);
			ajax1(form , "table01");
		}
	}else{
		form.action="/mops/web/ajax_autoComplete";
	}

	if ( keyCode == 37 || keyCode == 39 ){//Left || Right
		if ( keyIndex > 0 ){
			obj.value = document.getElementById("autoDiv-"+(keyIndex)).value;

			//為了讓左右鍵按下時，自動完成會重新顯示，所以這邊要先把舊的值清空
			document.getElementById('oldKeyWord').value = "";
		}
	}else{
		var dataLength = 0;
		var divBody;

		if ( keyCode == 38 || keyCode == 40 ){
			if ( document.getElementById("dataLength") ){
				dataLength = parseInt(document.getElementById("dataLength").value);
			}
		}

		if ( keyCode == 38 ){//UP
			//復原
			try{
				chgClass(1 , keyIndex , obj)
			}catch (e){}

			keyIndex--;

			chgPosition(keyCode , dataLength , targetDiv);

			if ( keyIndex < 1 ){
				keyIndex = dataLength;
			}

			//轉變
			try{
				chgClass(0 , keyIndex , obj)
			}catch (e){
				
			}
			obj.value = document.getElementById("autoDiv-"+(keyIndex)).value;
		}
		if ( keyCode == 40 ){//Down
			//復原
			try{
				chgClass(1 , keyIndex , obj)
			}catch (e){}

			keyIndex++;

			chgPosition(keyCode , dataLength , targetDiv);

			if ( keyIndex > dataLength ){
				keyIndex = 1;
			}

			//轉變
			try{
				chgClass(0 , keyIndex , obj)
			}catch (e){
				
			}

			obj.value = document.getElementById("autoDiv-"+(keyIndex)).value;
		}
	}
}

var minScrollTop = 0;
var maxScrollTop = 300;
function chgPosition(keyCode , dataLength , targetDiv){
	var addIndex = 0;//需額外加上的
	var tmpIndex = keyIndex;
	var tmpScrollTop = maxScrollTop;

	if ( keyIndex > dataLength ){
		minScrollTop = -26;
		maxScrollTop = 274;

		tmpIndex = 1;
	}else if ( keyIndex < 1 ){
		var titleLength = parseInt(document.getElementById("titleLength").value);

		maxScrollTop = (dataLength*26+(titleLength*26));
		minScrollTop = (maxScrollTop-274);

		tmpIndex = dataLength;
	}

	if ( keyCode == 38 ){//UP
		minScrollTop -= 26;
		maxScrollTop -= 26;

		if ( document.getElementById("autoSub-"+(tmpIndex)) != null ){
			addIndex -= 26;
		}
	}else if ( keyCode == 40 ){//Down
		minScrollTop += 26;
		maxScrollTop += 26;

		if ( document.getElementById("autAdd-"+(tmpIndex)) != null || document.getElementById("autoSub-"+(tmpIndex)) != null ){
			addIndex += 26;
		}
	}

	var divObj = document.getElementById(""+targetDiv+"");
	divBody = document.getElementById("autoCompilete-dbody"+(tmpIndex));
	var divTop = divBody.offsetTop;//選擇到的 div 的位置

	if ( divTop < 274 ){

		if ( keyIndex <= dataLength ){
			if ( keyCode == 38 ){//UP
				if ( maxScrollTop < 300 ){
					return false;
				}
			}else if ( keyCode == 40 ){//Down
				if ( minScrollTop < 274 ){
					return false;
				}
			}
		}
	}

	if ( keyIndex > dataLength ){
		divObj.scrollTop = minScrollTop;
	}else if ( keyIndex < 1 ){
		divObj.scrollTop = maxScrollTop;
	}else{
		divObj.scrollTop = divTop-274;
	}
}

function autoComplete(form1 , objDiv , targetDiv , evnt){
	var keyCode;
	if ( window.event ){
		keyCode = event.keyCode;
	}else{
		keyCode = evnt.which;
	}
	if ( keyCode != 38 && keyCode != 40 ){
		keyIndex = 0;//將index歸0

		minScrollTop = 0;//將minScrollTop初始化
		maxScrollTop = 300;//將maxScrollTop初始化
		var timeOut = 300;//300毫秒
		setTimeout(function(){ callAjax(form1 , objDiv , targetDiv); } , timeOut);
	}
}

function callAjax(form1 , objDiv , targetDiv){
	var keyWord = "";
	try{
		keyWord = document.getElementById(""+objDiv+"").value.trim();//本次輸入值
	}catch (e){
		keyWord = document.getElementById(""+objDiv+"").value;//本次輸入值
	}
	var oldKeyWord = document.getElementById('oldKeyWord').value;//上次輸入值

	document.getElementById('oldKeyWord').value = keyWord;

	if ( keyWord != oldKeyWord && keyWord.length > 0 ){//若值不為空白，或不和上次一樣，就要重新搜尋一次
		runAjax(form1 , objDiv , targetDiv);
	}else if ( keyWord.length == 0 ){
		hideDiv(targetDiv);
	}
}

function runAjax(form1 , objDiv , targetDiv){
	//抓取server的IP
	var href = document.location.href;
	var serverDNS = "";
	var url = "";
	if ( href.indexOf("http://") == 0 ){
		var x = href.indexOf("/", 7);
		if (x == -1) x = href.length;
		serverDNS = href.substring(7,x);
		url = "http://"+serverDNS+"/mops/web/ajax_autoComplete";
	}

	//20190603 twse1097 begin：判斷處理來源是https的request
	if ( href.indexOf("https://") == 0 ){
		var x = href.indexOf("/", 8);
		if (x == -1) x = href.length;
		serverDNS = href.substring(8,x);
		url = "https://"+serverDNS+"/mops/web/ajax_autoComplete";
	}
	//20190603 twse1097 end：判斷處理來源是https的request

	var xhttp;

	if ( window.XMLHttpRequest ){
		xhttp = new XMLHttpRequest();
	}else if ( window.ActiveXObject ){
		try{
			xhttp = new ActiveXObject ("Msxml2.XMLHTTP");
		} catch (e) {
			xhttp = new ActiveXObject ("Microsoft.XMLHTTP");
		}
	}

	try{
		xhttp.async = false;
	}catch (e){}

	var str='encodeURIComponent=1';
	var es = form1.elements;
	for (var i=0;i<es.length;i++){
		var value1 = encodeURIComponent(es[i].value);
		if(es[i].name=='') continue;
		if(es[i].type=="checkbox") { if(!es[i].checked) continue;}
		if(es[i].type=="radio") { if(!es[i].checked) continue;}
		str=str+'&'+es[i].name+'='+value1;
	}

	var sstep = "1";
	if ( objDiv.indexOf("2") != -1 || objDiv.indexOf("end_") != -1 ){
		sstep = "2";
	}

	str=str+'&sstep'+'='+sstep;

	xhttp.onreadystatechange = function() {
		if (xhttp.readyState == 4 && xhttp.status == 200) {
			runKeyWord(form1 , objDiv , targetDiv , xhttp);
		}
	};

	xhttp.open("POST", url , true);
	xhttp.send(str);
}

function runKeyWord(form1 , objDiv , targetDiv , xhttp){
	var tmpText = xhttp.responseText;
	if ( tmpText.indexOf("<div id=\"zoom\"></div>") != -1 ){
		hideDiv(targetDiv);
	}else{
		document.getElementById(""+targetDiv+"").innerHTML = tmpText;
		if ( checkHTML(objDiv , targetDiv , form1) ){
			showDiv(objDiv , targetDiv , 250);
		}else{
			hideDiv(targetDiv);
			runAjax(form1 , objDiv , targetDiv);
		}
	}

	return;
}

//改變 div 的 className
function chgClass(type , divIndex , objDiv){
	var obj = document.getElementById(""+objDiv+"");
	var className = "";

	var divHead = document.getElementById("autoCompilete-dhead"+divIndex+"");

	if ( divHead != null ){
		if ( type == 0 ){
			className = "auto-mousover";
			divHead.className = className+"-box";
		}else{
			className = "auto-mousout";
			divHead.className = className+"-box";
		}
	}
}
//檢查產出的 HTML 是不是跟此次的吻合
function checkHTML(objDiv , targetDiv , form1){
	var oValue = document.getElementById(""+objDiv+"").value;//本次輸入值

	var chkStr = "source_str=\""+oValue+"\"";
	var target = document.getElementById(""+targetDiv+"").innerHTML;

//	if ( target.indexOf(chkStr) == -1 ){
//		return false;
//	}else{
		return true;
//	}

}
//顯示自動完成
function showDiv(objDiv , targetDiv , maxLength){
	var dv = document.getElementById(""+targetDiv+"");
	dv.style.display = "block";

	var obj = document.getElementById(""+objDiv+"");
	var x = realPosX(obj);
	var y = realPosY(obj)+22;

	//IE6
	if ( navigator.userAgent.indexOf("MSIE 6.0") > 0 ){
		if ( objDiv == "keyword" ){
			dv.style.width = (maxLength*12)/1.8 + "px";
		}
	}

	dv.style.left = x+"px";
	dv.style.top = y+"px";
	dv.scrollTop = 0+"px";
}
//計算 X 軸
function realPosX(oTarget) {
	var realX = oTarget.offsetLeft
	if (oTarget.offsetParent.tagName.toUpperCase() != "BODY") {
		realX += realPosX(oTarget.offsetParent); 
	}
	return realX;
}
//計算 Y 軸
function realPosY(oTarget) {
		var realY = oTarget.offsetTop;
		if (oTarget.offsetParent.tagName.toUpperCase() != "BODY") {
			realY += realPosY(oTarget.offsetParent);
		}
		return realY;
}
//關閉自動完成
function hideDiv(targetDiv){
	var dv = document.getElementById(""+targetDiv+"");

		try{
			var minTop = dv.offsetLeft;
			var minLeft = dv.offsetTop;

			var width = dv.offsetWidth;
			var heigh = dv.offsetHeight;

			var maxTop = minTop + width;
			var maxLeft = minTop + heigh;

			if ( (mouseX < minTop) || (mouseX > maxTop) || (mouseY < minLeft) || (mouseY > maxLeft) ){
				dv.innerHTML = "";
				dv.style.display = "none";
			}
		}catch (e){}
}
//關閉自動完成 for 公司代號
function hideDiv2(objDiv){
	var dv = null;

	var dv1 = document.getElementById("auto-complete-data");
	var dv2 = document.getElementById("auto-complete-data2");
	var dvName = "";

	if ( dv1.style.display != "none" ){
		dv = dv1;
		dvName = "auto-complete-data";
	}else if ( dv2.style.display != "none" ){
		dv = dv2;
		dvName = "auto-complete-data2";
	}

	if ( dv != null ){
		dv.innerHTML = "";
		dv.style.display = "none";
	}

	//將old
	document.getElementById('oldKeyWord').value = objDiv.value;

	//將原本的 form action 還原，不然會跑到自動完成的程式
	if ( document.form1 ){
		document.form1.action = '/mops/web/ajax_' + document.fh.funcName.value;
	}
}

//檢查是否要關閉自動完成
function checkautoComplete(){
	var dv = null;

	var dv1 = document.getElementById("auto-complete-data");
	var dv2 = document.getElementById("auto-complete-data2");
	var dvName = "";

	if ( dv1.style.display != "none" ){
		dv = dv1;
		dvName = "auto-complete-data";
	}else if ( dv2.style.display != "none" ){
		dv = dv2;
		dvName = "auto-complete-data2";
	}

	if ( dv != null ){
		try{
			var minTop = dv.offsetLeft;
			var minLeft = dv.offsetTop;

			var width = dv.offsetWidth;
			var heigh = dv.offsetHeight;

			var maxTop = minTop + width;
			var maxLeft = minTop + heigh;

			if ( (mouseX < minTop) || (mouseX > maxTop) || (mouseY < minLeft) || (mouseY > maxLeft) ){
				dv.innerHTML = "";
				dv.style.display = "none";
			}

		}catch (e){}
	}

	return true;
}

//取得滑鼠座標
var IE = document.all?true:false;
if (!IE) document.captureEvents(Event.MOUSEMOVE);
document.onmousemove = getMouseXY;
document.onclick = checkautoComplete;
var mouseX = 0;
var mouseY = 0;
function getMouseXY(e) {
	if (IE){
		mouseX = event.clientX + document.body.scrollLeft;
		mouseY = event.clientY + document.body.scrollTop;
	}else{
		mouseX = e.pageX;
		mouseY = e.pageY;
	}

	if (mouseX < 0){mouseX = 0;}
	if (mouseY < 0){mouseY = 0;}
	return true;
}

/*
function getMsg() {
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			document.querySelector("form").className="bw";
			var resp = this.responseText;
			console.log(resp);
			//setTimeout(function() {
				eval(resp);
			//},5000);
		}
	}
	
	setTimeout(function() {
		xhttp.open("POST", "/server-java/AjaxCheck", true);
			xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			xhttp.send("step=0");
	},0);

	setInterval(
		function(){
			xhttp.open("POST", "/server-java/AjaxCheck", true);
			xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			xhttp.send("step=0");
		}
	,300000);
}
*/
function getMsg() {
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {

			/*首頁搜尋列跑馬*/
			if(!document.querySelector("#marquee")){
			document.querySelector("#nav").style.paddingLeft="32px";
			document.querySelector("#nav").innerHTML = "<style>#marquee{width:430px;}</style><marquee id='marquee' onMouseOver='this.stop()' onMouseOut='this.start()' behavior='side' direction='left' scrollamount='4''></marquee>";
			}else{
			document.querySelector("#marquee").innerHTML = "";
			};

			document.querySelector("form").className="bw";
			var resp = this.responseText;
			//console.log(resp);
			eval(resp);

			//20191114 twse1097:跑馬燈更新頻率調整為1hr
			setTimeout(function(){getMsg()},3600000);
		};
	};
	xhttp.open("POST", "/server-java/AjaxCheck", true);
	xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	xhttp.send("step=0");
};

getMsg();

function menuAction(fileName , usually){
	document.fh2.usually.value=usually;
	document.fh2.action="/mops/web/"+fileName;
	document.fh2.submit();
}

/*20211224 MAX FRANCIS begin:增加財務重點專區連結方式*/
function image_function () {
	if( document.querySelector(".image_function_a.block")){
		document.querySelector(".image_function_a.block").setAttribute("class","image_function_a blocknone");
	}else{
		document.querySelector(".image_function_a.blocknone").setAttribute("class","image_function_a block");
	}
};
/*20211224 MAX FRANCIS end: 增加財務重點專區連結方式*/

/**
(1) href='t146sb03'  要改為 href=\'t146sb03\'
(2) 有子項目的，在<ul>前面要加上<img src="arrow_r.gif" class="liArrow" />
**/
var menu0 = '\
	常用報表\
	<ul>\
			<li class = "L01_Item" onclick="menuAction(\'t05st03\' , \'Y\');">公司基本資料</li>\
			<li class = "L01_Item" onclick="menuAction(\'t05st10_ifrs\' , \'Y\');">採用IFRSs後之月營業收入資訊</li>\
			<li class = "L01_Pop">重大訊息<img src="arrow_r.gif" class="liArrow" /><ul>\
				<li class = "L02_Item" onclick="menuAction(\'t05sr01_1\' , \'Y\');">即時重大訊息</li>\
				<li class = "L02_Item" onclick="menuAction(\'t05st02\' , \'Y\');">當日重大訊息</li>\
				<li class = "L02_Item" onclick="menuAction(\'t05st01\' , \'Y\');">歷史重大訊息</li>\
			</ul>\
			</li>\
			<li class = "L01_Pop">財務報表<img src="arrow_r.gif" class="liArrow" /><ul>\
				<li class = "L02_Item" onclick="menuAction(\'t163sb01\' , \'Y\');">財務報告公告</li>\
				<li class = "L02_Item" onclick="menuAction(\'t164sb03\' , \'Y\');">資產負債表</li>\
				<li class = "L02_Item" onclick="menuAction(\'t164sb04\' , \'Y\');">綜合損益表</li>\
				<li class = "L02_Item" onclick="menuAction(\'t164sb05\' , \'Y\');">現金流量表</li>\
				<li class = "L02_Item" onclick="menuAction(\'t164sb06\' , \'Y\');">權益變動表</li>\
				<li class = "L02_Item" onclick="menuAction(\'t163sb15\' , \'Y\');">簡明綜合損益表(四季)</li>\
				<li class = "L02_Item" onclick="menuAction(\'t163sb16\' , \'Y\');">簡明資產負債表(四季)</li>\
				<li class = "L02_Item" onclick="menuAction(\'t163sb17\' , \'Y\');">簡明綜合損益表(三年)</li>\
				<li class = "L02_Item" onclick="menuAction(\'t163sb18\' , \'Y\');">簡明資產負債表(三年)</li>\
			</ul>\
			</li>\
			<li class = "L01_Pop">股東會及股利<img src="arrow_r.gif" class="liArrow" /><ul>\
				<li class = "L02_Item" onclick="menuAction(\'t05st09_2\' , \'Y\');">股利分派情形</li>\
				<li class = "L02_Item" onclick="menuAction(\'t108sb19_q1\' , \'Y\');">決定分配股息及紅利或其他利益</li>\
			</ul>\
			</li>\
			<li class = "L01_Pop">電子書<img src="arrow_r.gif" class="liArrow" /><ul>\
				<li class = "L02_Item" onclick="menuAction(\'t57sb01_q1\' , \'Y\');">財務報告書</li>\
				<li class = "L02_Item" onclick="menuAction(\'t57sb01_q3\' , \'Y\');">公開說明書</li>\
			</ul>\
			</li>\
			<li class = "L01_Item" onclick="menuAction(\'stapap1\' , \'Y\');">董監事持股餘額明細資料</li>\
			<li class = "L01_Item" onclick="menuAction(\'query6_1\' , \'Y\');">內部人持股異動事後申報表</li>\
			<li class = "L01_Item" onclick="menuAction(\'t100sb02_1\' , \'Y\');">法人說明會一覽表</li>\
			<li class = "L01_Pop">投資資訊<img src="arrow_r.gif" class="liArrow" /><ul>\
				<li class = "L02_Item" onclick="menuAction(\'t05st15\' , \'Y\');">赴大陸投資資訊(實際數)</li>\
				<li class = "L02_Item" onclick="menuAction(\'t05st16\' , \'Y\');">投資海外子公司資訊(實際數)</li>\
			</ul>\
			</li>\
			</ul>\ ';

			var menu1='\
				基本資料\
				<ul>\
							<li class = "L01_Item" onclick="window.location.href=\'t146sb05\';">精華版3.0</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t05st03\';">公司基本資料</li>\
							<li class = "L01_Pop">重要子公司基本資料<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t79sb02\';">重要子公司基本資料</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t79sb03\';">重要子公司異動說明</li>\
								</ul>\
							</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t102sb01\';">被投資控股公司基本資料</li>\
							<li class = "L01_Pop">電子書<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t57sb01_q1\';">財務報告書</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t57sb01_q2\';">財務預測書</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t57sb01_q3\';">公開說明書</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t57sb01_q4\';">公開收購說明書(101年7月後請至「公開收購專區」查詢)</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t57sb01_2\';">公開招募說明書</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t57sb01_q5\';">年報及股東會相關資料(含存託憑證資料)</li>\
									<li class = "L02_Item" onclick="window.location.href=\'WDLReader\';">WDL Reader(請至威鋒數位軟體下載區下載)</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t57sb01_q8\';">年度自結財務資訊專區</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">董監大股東持股、質押、轉讓<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'stapap1_all\';">董事、監察人、經理人及大股東持股餘額彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'stapap1\';">董監事持股餘額明細資料</li>\
									<li class = "L02_Pop">內部人持股轉讓事前申報表(個別公司)<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L03_Item" onclick="window.location.href=\'t56sb21_q1\';">持股轉讓日報表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t56sb21_q2\';">持股未轉讓日報表</li>\
										</ul>\
									</li>\
									<li class = "L02_Pop">內部人持股轉讓事前申報彙總表<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L03_Item" onclick="window.location.href=\'t56sb21_q3\';">持股轉讓日報表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t56sb21_q4\';">持股未轉讓日報表</li>\
										</ul>\
									</li>\
									<li class = "L02_Pop">董監事股權異動統計彙總表<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L03_Item" onclick="window.location.href=\'IRB160\';">公司增減資表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'IRB170\';">新公司彙總表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'IRB140\';">轉讓持股達100萬股以上者彙總表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'IRB150\';">取得股份達100萬股以上者彙總表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'IRB190\';">董事、監察人質權設定在100萬股以上彙總表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'IRB200\';">董事、監察人質權解除在100萬股以上彙總表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'IRB110\';">董事、監察人、經理人及百分之十以上大股東股權異動彙總表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'IRB130\';">董事、監察人、經理人及百分之十以上大股東質權設定彙總表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'IRB180\';">董事、監察人質權設定佔董事及監察人實際持有股數彙總表</li>\
										</ul>\
									</li>\
									<li class = "L02_Item" onclick="window.location.href=\'query6_1\';">內部人持股異動事後申報表</li>\
									<li class = "L02_Pop">內部人設質解質彙總公告<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L03_Item" onclick="window.location.href=\'STAMAK03_q1\';">依照日期排序</li>\
											<li class = "L03_Item" onclick="window.location.href=\'STAMAK03_q2\';">依照公司代號排序</li>\
										</ul>\
									</li>\
									<li class = "L02_Item" onclick="window.location.href=\'STAMAK03_1\';">內部人設質解質公告(個別公司)</li>\
									<li class = "L02_Pop">股權轉讓資料查詢<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L03_Item" onclick="window.location.href=\'t56sb12_q1\';">上市公司持股轉讓日報表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t56sb12_q2\';">上櫃公司持股轉讓日報表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t56sb12_q3\';">興櫃公司持股轉讓日報表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t56sb12_q4\';">公開發行公司持股轉讓日報表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t56sb12_q5\';">上市公司持股未轉讓日報表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t56sb12_q6\';">上櫃公司持股未轉讓日報表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t56sb12_q7\';">興櫃公司持股未轉讓日報表</li>\
										</ul>\
									</li>\
									<li class = "L02_Item" onclick="window.location.href=\'IRB100\';">董事、監察人持股不足法定成數彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'IRB210\';">董事、監察人持股不足法定成數連續達3個月以上彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t93sb06\';">公司董事、監察人及持股10%以上大股東為法人之彙總查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t93sb06_1\';"> 持股10%以上大股東最近異動情形</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">庫藏股(已移至「投資專區」項下「庫藏股資訊專區」)<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t35sb00_1\';">申請庫藏股買回基本資料(已移至「投資專區」項下「庫藏股資訊專區」)</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t35sb00_2\';">申請庫藏買回達一定標準(已移至「投資專區」項下「庫藏股資訊專區」)</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t35sb00_3\';">查詢期間屆滿(執行完畢)及已辦理消除及轉讓股份(已移至「投資專區」項下「庫藏股資訊專區」)</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t35sb00_4\';">庫藏股轉讓予員工基本資料查詢(已移至「投資專區」項下「庫藏股資訊專區」)</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t35sb00_5\';">董事會決議變更買回股份轉讓員工辦法之公告事項查詢(已移至「投資專區」項下「庫藏股資訊專區」)</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">買回臺灣存託憑證<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t35sb08\';">買回臺灣存託憑證基本資料</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t35sb10\';">買回臺灣存託憑證達一定標準</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t35sb09\';">查詢買回臺灣存託憑證期間屆滿或執行完畢</li>\
								</ul>\
							</li>\
							<li class = "L01_Item" onclick="window.location.href=\'bfhtm_q1\';">募資計畫</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t05st05\';">歷年變更登記</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t16sn02\';">股權分散表</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t98sb04\';">國內海外有價證券轉換情形</li>\
							<li class = "L01_Pop">國內有價證券<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t47sb12\';">特別股權利基本資料查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t47sb05\';">附認股權特別股基本資料查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t47sb06\';">一般公司債基本資料查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t47sb17\';">一般公司債月報表查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t47sb07\';">轉換公司債基本資料查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t47sb18\';">轉換公司債月報表查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t47sb08\';">附認股權公司債基本資料查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t47sb19\';">附認股權公司債月報表查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t47sb11\';">附認股權公司債利率查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t47sb20\';">金融債券月報表查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t144sb01\';">分離後認股權憑證發行基本資料查詢</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">海外有價證券<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t47sb03_q1\';">海外股票基本資料查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t47sb24\';">海外股票流通餘額報表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t47sb03_q2\';">海外存託憑證基本資料查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t47sb25\';">海外存託憑證暨其所表彰有價證券流通餘額查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t47sb03_q3\';">海外一般公司債基本資料查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t47sb21\';">海外一般公司債異動情形報表查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t47sb03_q4\';">海外轉換公司債基本資料查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t47sb22\';">海外轉換公司債異動情形報表查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t47sb03_q5\';">海外附認股權公司債基本資料查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t47sb23\';">海外附認股權公司債異動情形報表查詢</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">員工認股權憑證<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t47sb09\';">員工認股權憑證基本資料查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t158sb02\';">員工認股權憑證發行次日暨經理人、部門及分支機構主管取得認股權憑證情形之申報資訊</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t158sb05_new\';">員工認股權憑證發行期間屆滿次日暨經理人、部門及分支機構主管取得認股權憑證情形之申報資訊</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t158sb03\';">員工認股權憑證經理人、部門及分支機構主管認購情形之資訊－認購次日內申報（自102年7月4日起免申報）</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t158sb04_new\';">員工認股權憑證經理人、部門及分支機構主管當季認購認股權情形資料－按季結束十日內申報</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t47sc18\';">員工認股權憑證年度已執行及未執行資訊</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t158sb01\';">員工認股權憑證年度經理人、部門及分支機構主管與前十大員工之取得及認購情形資訊</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">限制員工權利新股<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t160sb01\';">限制員工權利新股發行辦法及對股東權益可能稀釋情形(包含變更發行辦法、實際發行資料)</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t160sb02\';">員工達成既得條件之解除限制資訊</li>\
								</ul>\
							</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t111sb02\';">關係企業組織圖(自102年度起免申報)</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t98sb02\';">僑外投資持股情形統計表</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t132sb20\';">外國發行人之股票、臺灣存託憑證、債券流通情形</li>\
					</ul>\
					';
					var menu2='\
						彙總報表\
						<ul>\
							<li class = "L01_Pop">基本資料<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t51sb01\';">基本資料查詢彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t79sb04\';">重要子公司基本資料彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t47hsc01\';">國內公司發行海外存託憑證彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t35sb00_6\';">庫藏股統計表(已移至「投資專區」項下「庫藏股資訊專區」)</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t51sb04_q1\';">員工認股權憑證基本資料彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t158sb06\';">員工認股權憑證實際發行資料及已(未)執行認股情形彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t160sb04\';">限制員工權利新股基本資料彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t51sb04_q2\';">國內其他有價證券資料彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t51sb03\';">海外有價證券基本資料彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t98sb05\';">轉換公司債轉換變動情形一覽表</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">股東會及股利<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Pop">股東會公告<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L03_Item" onclick="window.location.href=\'t108sb31_q1\';">召開股東常(臨時)會日期、地點及採用電子投票情形等資料彙總表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t108sb26\';">召集股東常(臨時)會公告資料彙總表(95年度起適用)</li>\
										</ul>\
									</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t144sb11\';">採候選人提名制、累積投票制、全額連記法選任董監事及當選資料彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t144sb09\';">股東行使提案權情形彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t150sb04\';">股東會議案決議情形</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t05sb12\';">普通股股利分派頻率暨普通股年度(含第4季或後半年度)現金股息及紅利決議層級彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t05st09_new\';">股利分派情形</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t66sb06\';">僅分派員工紅利及董監酬勞而未分派股利之公司查詢(自104年度起不適用)</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t66sb23\';">TDR股利分派情形(101年起適用)</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t108sb27\';">除權息公告</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t108sb31new\';">股東會及除權息日曆</li>\
								</ul>\
							</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t100sb02_1\';">法人說明會一覽表</li>\
							<li class = "L01_Pop">財務報表<img src="arrow_r.gif" class="liArrow" /><ul>\
								<li class = "L02_Pop">採用IFRSs後<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L03_Item" onclick="window.location.href=\'t163sb04\';">綜合損益表</li>\
									<li class = "L03_Item" onclick="window.location.href=\'t163sb05\';">資產負債表</li>\
									<li class = "L03_Item" onclick="window.location.href=\'t163sb20\';">現金流量表</li>\
									<li class = "L03_Item" onclick="window.location.href=\'t56sb29_q3\';">財務報告經監察人承認情形</li>\
									<li class = "L03_Item" onclick="window.location.href=\'t163sb14\';">會計師查核(核閱)報告</li>\
									<li class = "L03_Item" onclick="window.location.href=\'t163sb19\';">各產業EPS統計資訊</li>\
									</ul>\
								</li>\
								<li class = "L02_Pop">採用IFRSs前<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L03_Item" onclick="window.location.href=\'t51sb08\';">損益表</li>\
									<li class = "L03_Item" onclick="window.location.href=\'t51sb07\';">資產負債表</li>\
									<li class = "L03_Item" onclick="window.location.href=\'t51sb13\';">合併損益表</li>\
									<li class = "L03_Item" onclick="window.location.href=\'t51sb12\';">合併資產負債表</li>\
									<li class = "L03_Item" onclick="window.location.href=\'t132sb21\';">第一上市櫃損益季節查詢彙總表</li>\
									<li class = "L03_Item" onclick="window.location.href=\'t132sb22\';">第一上市櫃資產負債季節查詢彙總表</li>\
									<li class = "L03_Item" onclick="window.location.href=\'t56sb29_q2\';">財務報告經監察人承認情形</li>\
									<li class = "L03_Item" onclick="window.location.href=\'t06se09_1\';">會計師查核(核閱)報告</li>\
									</ul>\
								</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">財務預測<img src="arrow_r.gif" class="liArrow" /><ul>\
								<li class = "L02_Pop">採IFRSs後<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L03_Pop">財測達成情形<img src="arrow_r.gif" class="liArrow" /><ul>\
										<li class = "L04_Item" onclick="window.location.href=\'t06sf09_1_q1\';">截至各季綜合損益財測達成情形(完整式)</li>\
										<li class = "L04_Item" onclick="window.location.href=\'t163sb11\';">年度自結綜合損益達成情形及差異原因(完整式)</li>\
										<li class = "L04_Item" onclick="window.location.href=\'t163sb12\';">年度實際綜合損益(經會計師查核)達成情形(完整式)</li>\
										<li class = "L04_Item" onclick="window.location.href=\'t06sf09_2_q1\';">各季綜合損益財測達成情形(簡式)</li>\
										<li class = "L04_Item" onclick="window.location.href=\'t06sf09_3_q1\';">當季綜合損益經會計師查核(核閱)數與當季預測數差異達百分之十以上者，<br>或截至當季累計差異達百分之二十以上者(簡式)</li>\
										</ul>\
									</li>\
									</ul>\
								</li>\
								<li class = "L02_Pop">採IFRSs前<img src="arrow_r.gif" class="liArrow" /><ul>\
										<li class = "L03_Pop">財測達成情形<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L04_Item" onclick="window.location.href=\'t06sf09_1\';">截至各季稅前損益財測達成情形</li>\
											<li class = "L04_Item" onclick="window.location.href=\'t52sb01\';">年度自結稅前損益(未經會計師查核)達成情形</li>\
											<li class = "L04_Item" onclick="window.location.href=\'t52sb02\';">年度實際稅前損益(經會計師查核)達成情形</li>\
											<li class = "L04_Item" onclick="window.location.href=\'t06sf09_2\';">各季稅前損益財測達成情形(簡式)</li>\
											<li class = "L04_Item" onclick="window.location.href=\'t06sf09_3\';">當季稅前損益經會計師查核(核閱)數與當季預測數差異達百分之十以上者，<br>或截至當季累計差異達百分之二十以上者(簡式)</li>\
											</ul>\
										</li>\
										<li class = "L03_Pop">處記缺失情形<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L04_Item" onclick="window.location.href=\'t52sc03_4\';">完整式處記缺失情形</li>\
											<li class = "L04_Item" onclick="window.location.href=\'t52sc03_3\';">簡式處記缺失情形</li>\
											</ul>\
										</li>\
										<li class = "L03_Pop">財務預測相關資訊<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L04_Item" onclick="window.location.href=\'t21sc02\';">完整式</li>\
											<li class = "L04_Item" onclick="window.location.href=\'t21sc02_1\';">簡式</li>\
											</ul>\
										</li>\
										<li class = "L03_Item" onclick="window.location.href=\'finance_w1\';">上市公司各季財測彙總資料</li>\
										<li class = "L03_Pop">期間別財測公告情形<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L04_Item" onclick="window.location.href=\'t56sb02n_1_all\';">完整式財務預測彙總查詢</li>\
											<li class = "L04_Item" onclick="window.location.href=\'t56sball_1\';">簡式財務預測彙總查詢</li>\
											</ul>\
										</li>\
										<li class = "L03_Pop">合併財務預測相關資訊<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L04_Item" onclick="window.location.href=\'t21sc02c\';">完整式</li>\
											<li class = "L04_Item" onclick="window.location.href=\'t21sc02_1c\';">簡式</li>\
											</ul>\
										</li>\
									</ul>\
								</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">公告<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t67sb07\';">取得或處分資產公告</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t116sb02\';">取得或處分私募有價證券公告</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t100hsb05\';">董事會議決事項未經審計委員會通過，或獨立董事有反對或保留意見公告</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t138sb01\';">自結損益公告</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t132sb16\';">公開發行股票全面轉換無實體發行公告</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t06hsb07\';">財務報告無虛偽或隱匿聲明書公告</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t06hsb09\';">會計主管不符資格條件調整職務公告</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t108sb08_1_q1\';">轉換(附認股權)公司債公告</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t127sb00_q1\';">各項公告查詢作業</li>\
									<li class = "L02_Pop">海外公司債公告<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L03_Item" onclick="window.location.href=\'t47sb03_q6\';">海外一般公司債</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t47sb03_q7\';">海外轉換公司債</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t47sb03_q8\';">海外附認股權公司債</li>\
										</ul>\
									</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t144sb02\';">分離後認股權憑證之公告</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t57sb01_q9\';">年度自結財務資訊專區</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">營運概況<img src="arrow_r.gif" class="liArrow" /><ul>\
								<li class = "L02_Pop">每月營收<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L03_Item" onclick="window.location.href=\'t21sc04_ifrs\';">採用IFRSs後每月營業收入彙總表</li>\
									<li class = "L03_Pop">採用IFRSs前營業收入彙總表<img src="arrow_r.gif" class="liArrow" /><ul>\
										<li class = "L04_Item" onclick="window.location.href=\'t21sc04\';">每月營業收入統計彙總表</li>\
										<li class = "L04_Item" onclick="window.location.href=\'t21sb06\';">每月合併營業收入統計彙總表</li>\
									</ul>\</li>\
									<li class = "L03_Item" onclick="window.location.href=\'t05st08_all\';">各項產品業務營收統計彙總表</li>\
									</ul>\
								</li>\
								<li class = "L02_Pop">財務比率分析<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L03_Item">採IFRSs後<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "Level04_ItemStyle" onclick="window.location.href=\'t51sb02_q1\';">財務分析資料查詢彙總表</li>\
											<li class = "Level04_ItemStyle" onclick="window.location.href=\'t163sb06\';">營益分析查詢彙總表</li>\
											<li class = "Level04_ItemStyle" onclick="window.location.href=\'t163sb07\';">毛利率彙總表</li>\
										</ul>\
									</li>\
									<li class = "L03_Item">採IFRSs前<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "Level04_ItemStyle" onclick="window.location.href=\'t51sb02\';">財務分析資料查詢彙總表</li>\
											<li class = "Level04_ItemStyle" onclick="window.location.href=\'t51sb06\';">營益分析查詢彙總表</li>\
											<li class = "Level04_ItemStyle" onclick="window.location.href=\'t51sb05\';">毛利率彙總表</li>\
										</ul>\
									</li>\
									</ul>\
								</li>\
								<li class = "L02_Item" onclick="window.location.href=\'t05st16_all\';">投資海外子公司資訊彙總表</li>\
								<li class = "L02_Item" onclick="window.location.href=\'t92sb05\';">赴大陸投資資料查詢彙總表</li>\
								</ul>\
							</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t51sb01_1\';">下市/下櫃/撤銷登錄興櫃/不繼續公開發行彙總表</li>\
							<li class = "L01_Item" onclick="window.location.href=\'edco_w\';">初次上市(櫃)公司(IPO)穩定價格措施</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t116sb01_new\';">辦理私募之應募人為內部人或關係人</li>\
					</ul>\ ';
					var menu3='\
						股東會及股利\
						<ul>\
							<li class = "L01_Pop">股東常會(臨時會)公告<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t108sb16_q1\';">召開股東常(臨時)會及受益人大會(94.5.5後之上市櫃/興櫃公司)</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t59sb06\';">召開股東常(臨時)會(公開發行及94.5.5前之全體公司)</li>\
								</ul>\
							</li>\
							<li class = "L01_Item" onclick="window.open(\'http://cgc.twse.com.tw/electronicVoting/chPage\');">股東常會採行電子投票之上市櫃公司名單彙總表</li>\
							<li class = "L01_Pop">除權息公告<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t108sb19_q1\';">決定分配股息及紅利或其他利益(94.5.5後之上市櫃/興櫃公司)</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t59sb07\';">決定分派股息及紅利或其他利益(公開發行及94.5.5前之全體公司)</li>\
								</ul>\
							</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t05st09_2\';">股利分派情形</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t66sb20\';">TDR股利分派情形-經董事會通過擬議者適用</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t66sb21\';">TDR股利分派情形-經股東會通過者適用</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t66sb22\';">TDR股利分派情形-僅需董事會通過無需經股東會通過者適用</li>\
					</ul>\ ';
					//20181218 調整順序
					/*20220530 bristol ringcle begin 選單文字修改及新增 */
					/*
					var menu4='\公司治理\
					\<ul>\
							<li class = "L01_Pop">公司治理結構<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t100sb01_1\';">公司治理組織架構部分(含董事會組成之基本資訊)</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t100sb03_1\';">設立功能性委員會及組織成員</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t144sb10_w\';">年報前十大股東相互間關係彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t144sb08\';">董事長兼任總經理情形彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t100sb04_1\';">訂定公司治理之相關規程規則</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">董事及監察人相關資訊<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t100sb07\';">董事及監察人出(列)席董事會及進修情形暨獨立董事現職、經歷及兼任情形(個別)</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t93sc01_1\';">獨立董事現職、經歷及兼任情形彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t93sc03_1\';">董事及監察人出(列)席董事會及進修情形彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t93sb05\';">獨立董事設置情形</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t119sb07\';">員工酬勞及董事、監察人酬勞資訊彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t119sb04\';">董監事酬金相關資訊</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t119sb05\';">公司年度稅後虧損惟董監事酬金總金額或平均每位董監事酬金卻增加</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t119sb06\';">稅後損益與董監酬金變動之關聯性與合理性</li>\
									<li class = "L02_Item" onclick="window.location.href=\'IRB100_q1\';">董事、監察人持股不足法定成數彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t135sb03\';">董事及監察人投保責任險情形</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t167sb01\';">依據「上市上櫃公司治理實務守則」第24條規定公告彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t167sb02\';">自願揭露退休董事長或總經理回任公司顧問</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">股東常會相關之公司治理統計資訊<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'http://cgc.twse.com.tw/nomination/chPage\';">採候選人提名制之上市櫃公司</li>\
									<li class = "L02_Item" onclick="window.location.href=\'http://cgc.twse.com.tw/enProcedureManual/chPage\';">股東常會提供英文議事手冊之上市櫃公司</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">企業社會責任相關資訊<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t152sb01\';">溫室氣體排放及減量資訊</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t100sb11\';">企業社會責任報告書</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t114sb07\';">分派員工酬勞之經理人姓名及分派情形</li>\
									<li class = "L02_Pop">員工福利及薪酬統計相關資訊<img src="arrow_r.gif" class="liArrow" /><ul>\
										<li class = "L03_Item" onclick="window.location.href=\'t100sb14\';">財務報告附註揭露之員工福利(薪資)資訊</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t100sb15\';">非擔任主管職務之全時員工薪資資訊</li>\
										<li class = "L03_Pop">員工福利政策及權益維護措施揭露<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L04_Item" onclick="window.location.href=\'t100sb12\';">個別公司查詢</li>\
											<li class = "L04_Item" onclick="window.location.href=\'t100sb13\';">彙總資料查詢</li>\
										</ul>\
									</ul>\
								</ul>\
							</li>\
					*/
					var menu4='\公司治理\
					\<ul>\
							<li class = "L01_Pop">公司治理結構<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t100sb01_1\';">公司治理組織架構部分(含董事會組成之基本資訊)</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t100sb03_1\';">設立功能性委員會及組織成員</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t144sb10_w\';">年報前十大股東相互間關係彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t144sb08\';">董事長兼任總經理情形彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t100sb04_1\';">訂定公司治理之相關規程規則</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">董事及監察人相關資訊<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t100sb07\';">董事及監察人出(列)席董事會及進修情形暨獨立董事現職、經歷及兼任情形(個別)</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t93sc01_1\';">獨立董事現職、經歷及兼任情形彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t93sc03_1\';">董事及監察人出(列)席董事會及進修情形彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t93sb05\';">獨立董事設置情形</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t119sb07\';">員工酬勞及董事、監察人酬勞資訊彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t119sb04\';">董監事酬金相關資訊</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t119sb05\';">公司年度稅後虧損惟董監事酬金總金額或平均每位董監事酬金卻增加</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t119sb06\';">稅後損益與董監酬金變動之關聯性與合理性</li>\
									<li class = "L02_Item" onclick="window.location.href=\'IRB100_q1\';">董事、監察人持股不足法定成數彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t135sb03\';">董事及監察人投保責任險情形</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t167sb01\';">依據「上市上櫃公司治理實務守則」第24條規定公告彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t167sb02\';">自願揭露退休董事長或總經理回任公司顧問</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">股東常會相關之公司治理統計資訊<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'http://cgc.twse.com.tw/nomination/chPage\';">採候選人提名制之上市櫃公司</li>\
									<li class = "L02_Item" onclick="window.location.href=\'http://cgc.twse.com.tw/enProcedureManual/chPage\';">股東常會提供英文議事手冊之上市櫃公司</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">企業ESG相關資訊<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Pop">企業ESG資訊揭露<img src="arrow_r.gif" class="liArrow" /><ul>\
										<li class = "L03_Item" onclick="window.location.href=\'t214sb01\';">個別公司查詢</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t214sb02\';">彙總資料查詢</li>\
										</ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t152sb01\';">溫室氣體排放及減量資訊</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t100sb11\';">永續報告書</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t114sb07\';">分派員工酬勞之經理人姓名及分派情形</li>\
									<li class = "L02_Pop">員工福利及薪酬統計相關資訊<img src="arrow_r.gif" class="liArrow" /><ul>\
										<li class = "L03_Item" onclick="window.location.href=\'t100sb14\';">財務報告附註揭露之員工福利(薪資)資訊</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t100sb15\';">非擔任主管職務之全時員工薪資資訊</li>\
										<li class = "L03_Pop">員工福利政策及權益維護措施揭露<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L04_Item" onclick="window.location.href=\'t100sb12\';">個別公司查詢</li>\
											<li class = "L04_Item" onclick="window.location.href=\'t100sb13\';">彙總資料查詢</li>\
										</ul>\
									</ul>\
								</ul>\
							</li>\
							<li class = "L01_Pop">內部控制專區<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t06sg20\';">內控聲明書公告</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t06hsg20\';">內部控制專案審查報告</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">投保中心對公司起訴或公司已依規發布重大訊息之訴訟案件<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'sfipc_w\';">投保中心現正進行中之團體求償相關訴訟案件</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t144sb07\';">公司已依規發布重大訊息之訴訟案件</li>\
								</ul>\
							</li>\
						\</ul>\ ';
						/*20220530 bristol ringcle end 選單文字修改及新增 */
						/*
							var menu4='\
                            <li class = "L01_Item" onclick="window.open(\'http://cgc.twse.com.tw/electronicVoting/chPage\');">於股東常會採行電子投票之上市櫃公司</li>\
							<li class = "L01_Item" onclick="window.open(\'http://cgc.twse.com.tw/nomination/chPage\');">採候選人提名制之上市櫃公司</li>\
                            <li class = "L01_Item" onclick="window.open(\'http://cgc.twse.com.tw/ballotCase/chPage\');">於股東常會採行逐案票決之上市櫃公司彙總表</li>\
                            <li class = "L01_Item" onclick="window.open(\'http://cgc.twse.com.tw/enProcedureManual/chPage\');">股東常會提供英文議事手冊之上市櫃公司</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t100sb01_1\';">公司治理組織架構部分</li>\
							<li class = "L01_Pop">內部控制專區<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t06sg20\';">內控聲明書公告</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t06hsg20\';">內部控制專案審查報告</li>\
								</ul>\
							</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t100sb07\';">董事及監察人出(列)席董事會及進修情形暨獨立董事現職、經歷及兼任情形(個別)</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t93sc01_1\';">獨立董事現職、經歷及兼任情形彙總表</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t93sc03_1\';">董事及監察人出(列)席董事會及進修情形彙總表</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t93sb05\';">獨立董事設置情形</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t119sb07\';">員工酬勞及董事、監察人酬勞資訊彙總表</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t119sb04\';">董監事酬金相關資訊</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t119sb05\';">公司年度稅後虧損惟董監事酬金總金額或平均每位董監事酬金卻增加</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t119sb06\';">稅後損益與董監酬金變動之關聯性與合理性</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t167sb02\';">自願揭露退休董事長或總經理回任公司顧問</li>\
							<li class = "L01_Item" onclick="window.location.href=\'IRB100_q1\';">董事、監察人持股不足法定成數彙總表</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t135sb03\';">董事及監察人投保責任險情形</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t100sb03_1\';">設立功能性委員會及組織成員</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t100sb04_1\';">訂定公司治理之相關規程規則</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t100sb10\';">公司治理自評報告(自2014/10/13起停止上傳)</li>\
							<li class = "L01_Item" onclick="window.location.href=\'http://mops.twse.com.tw/nas/protect/report.pdf\';">「薪資報酬委員會運作效益之衡量與評估」調查報告</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t135sb02\';">依證交法第14條之4設立審計委員會彙總表(已移至「設立功能性委員會及組織成員」項下查詢)</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t114sb07\';">分派員工酬勞之經理人姓名及分派情形</li>\
							<li class = "L01_Pop">投保中心對公司起訴或公司已依規發布重大訊息之訴訟案件<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'sfipc_w\';">投保中心現正進行中之團體求償相關訴訟案件</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t144sb07\';">公司已依規發布重大訊息之訴訟案件</li>\
								</ul>\
							</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t144sb08\';">董事長兼任總經理情形彙總表</li>\
							<li class = "L01_Item" onclick="window.location.href=\'cga_w\';">通過中華公司治理協會「公司治理制度評量」認證之公司名單</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t144sb10_w\';">年報前十大股東相互間關係彙總表</li>\
							<li class = "L01_Item" onclick="window.location.href=\'best_company_w\';">公司治理網站揭露較佳參考範例</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t152sb01\';">溫室氣體排放及減量資訊</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t167sb01\';">依據「上市上櫃公司治理實務守則」第24條規定公告彙總表</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t100sb11\';">企業社會責任報告書</li>\
							<li class = "L01_Pop">員工福利政策及權益維護措施揭露<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t100sb12\';">個別公司查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t100sb13\';">彙總資料查詢</li>\
								</ul>\
							</li>\
';	
*/			
/*
							<li class = "L01_Pop">內部控制專區<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t06sg20\';">內控聲明書公告</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t06hsg20\';">內部控制專案審查報告</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t06sb06\';">內部稽核單位基本資料</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t06sb04\';">內部控制主要缺失與改善情形</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t06sb05\';">內部控制及內部稽核運作情形查詢作業</li>\
								</ul>\
							</li>\

*/
var menu5='\
	財務報表\
	<ul>\
							<li class = "L01_Pop">採IFRSs後<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t163sb01\';">財務報告公告</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t56sb31_q1\';">財務報告更(補)正查詢作業</li>\
									<li class = "L02_Pop">財務預測公告<img src="arrow_r.gif" class="liArrow" /><ul>\
										<li class = "L03_Item" onclick="window.location.href=\'t163sb10\';">財務預測公告</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t66sb02_q1\';">實際數與自結數差異公告</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t66sb03_q1\';">實際數與預測數差異公告</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t66sb04_q1\';">年度終了預計損益表達成情形公告</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t163sb11_q1\';">年度自結綜合損益達成情形及差異原因(完整式)</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t163sb12_q1\';">年度實際綜合損益(經會計師查核)達成情形(完整式)</li>\
									</ul>\
									</li>\
									<li class = "L02_Pop">合併/個別報表<img src="arrow_r.gif" class="liArrow" /><ul>\
										<li class = "L03_Item" onclick="window.location.href=\'t164sb03\';">資產負債表</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t164sb04\';">綜合損益表</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t164sb05\';">現金流量表</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t164sb06\';">權益變動表</li>\
									</ul>\
									</li>\
									<li class = "L02_Pop">簡明報表<img src="arrow_r.gif" class="liArrow" /><ul>\
										<li class = "L03_Item" onclick="window.location.href=\'t163sb15\';">簡明綜合損益表(四季)</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t163sb16\';">簡明資產負債表(四季)</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t163sb17\';">簡明綜合損益表(三年)</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t163sb18\';">簡明資產負債表(三年)</li>\
									</ul>\
									</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t163sb03\';">會計師查核(核閱)報告</li>\
									<li class = "L02_Pop">財務預測報表(完整式)<img src="arrow_r.gif" class="liArrow" /><ul>\
										<li class = "L03_Item" onclick="window.location.href=\'t163sb13a\';">財務預測-資產負債表</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t163sb13b\';">財務預測—綜合損益表</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t163sb13c\';">財務預測—簡明財測資料</li>\
									</ul>\
									</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">採IFRSs前<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t56sb01n_1\';">財務報告公告</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t56sb31\';">財務報告更(補)正查詢作業</li>\
									<li class = "L02_Pop">財務預測公告<img src="arrow_r.gif" class="liArrow" /><ul>\
										<li class = "L03_Item" onclick="window.location.href=\'t56sb02n_1_q1\';">原編公告</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t56sb02n_1_q2\';">更新/更正/重編公告</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t56sb02n_1_q3\';">仍屬有效公告</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t56sb02n_1_q4\';">不適用之公告</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t56sb02n_1_q5\';">簡式財務預測公告</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t56sb02n_1c_q1\';">合併財務預測公告原編</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t56sb02n_1c_q2\';">合併財務預測公告更新/更正/重編)</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t66sb04\';">年度終了預計損益表達成情形及差異原因公告</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t66sb02\';">實際數與自結數差異公告</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t66sb03\';">實際數與預測數差異公告</li>\
									</ul>\
									</li>\
									<li class = "L02_Pop">個別報表<img src="arrow_r.gif" class="liArrow" /><ul>\
										<li class = "L03_Item" onclick="window.location.href=\'t05st32\';">損益表</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t05st31\';">資產負債表</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t05st35\';">股東權益變動表</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t05st36\';">現金流量表</li>\
									</ul>\
									</li>\
									<li class = "L02_Pop">合併報表<img src="arrow_r.gif" class="liArrow" /><ul>\
										<li class = "L03_Item" onclick="window.location.href=\'t05st34\';">損益表</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t05st33\';">資產負債表</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t05st38\';">股東權益變動表</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t05st39\';">現金流量表</li>\
									</ul>\
									</li>\
									<li class = "L02_Pop">簡明報表<img src="arrow_r.gif" class="liArrow" /><ul>\
										<li class = "L03_Item" onclick="window.location.href=\'t05st21\';">簡明損益表(三年)</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t05st20\';">簡明資產負債表(三年)</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t05st30\';">簡明損益表(四季)</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t05st29\';">簡明資產負債表(四季)</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t05st30_c\';">簡明合併損益表(四季)</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t05st29_c\';">簡明合併資產負債表(四季)</li>\
									</ul>\
									</li>\
									<li class = "L02_Pop">會計師查核(核閱)報告<img src="arrow_r.gif" class="liArrow" /><ul>\
										<li class = "L03_Item" onclick="window.location.href=\'t05st37\';">個別</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t05st40\';">合併</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t20sb01c\';">合併關係企業</li>\
									</ul>\
									</li>\
									<li class = "L02_Pop">財務預測報表(完整式)<img src="arrow_r.gif" class="liArrow" /><ul>\
										<li class = "L03_Item" onclick="window.location.href=\'t05st23e_q2\';">財務預測-損益表</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t05st23e_q3\';">財務預測-簡明財測資料</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t05st23e_q1\';">財務預測-資產負債表</li>\
									</ul>\
									</li>\
									<li class = "L02_Pop">合併關係企業財務報表<img src="arrow_r.gif" class="liArrow" /><ul>\
										<li class = "L03_Item" onclick="window.location.href=\'t20sb01a\';">資產負債表</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t20sb01b\';">損益表</li>\
									</ul>\
									</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">XBRL資訊平台<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t203sb01\';">單一公司案例文件查詢及下載</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t203sb02\';">案例文件整批下載</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t203sb03\';">分類標準下載</li>\
								</ul>\
							</li>\
							<li class = "L01_Item" onclick="window.open(\'http://mopsfin.twse.com.tw/\');">財務比較e點通</li>\
							<li class = "L01_Item" onclick="window.open(\'http://mopsfin.twse.com.tw/brkfin/\');">證券商財務資料動態查詢系統</li>\
					</ul>\ ';
					var menu6='\
						重大訊息與公告\
						<ul>\
							<li class = "L01_Item" onclick="window.location.href=\'t05sr01_1\';">即時重大訊息</li>\
							<li class = "L01_Pop">重大訊息綜合查詢<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t05st02\';">當日重大訊息</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t05st01\';">歷史重大訊息</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t51sb10_q1\';">重大訊息主旨全文檢索</li>\
								</ul>\
							</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t132sb12\';">臺灣存託憑證收盤價彙總表</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t100sb07_1\';">法說會</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t146sb10\';">公告查詢</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t178sb01\';">券商對媒體轉載之澄清或說明</li>\
						</ul>\ ';
						var menu7='\
							營運概況\
								<ul>\
							<li class = "L01_Pop">每月營收<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t05st10_ifrs\';">採用IFRSs後之月營業收入資訊</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t05st10\';">採用IFRSs前之開立發票及營業收入資訊(含合併營收)</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t05st08\';">各項產品業務營收統計表</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">資金貸與及背書保證<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t05st11\';">背書保證與資金貸放餘額資訊</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t65sb04\';">資金貸與及背書保證明細表資訊</li>\
								</ul>\
							</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t79sb01\';">重要子公司月營收、背書保證及資金貸放餘額</li>\
							<li class = "L01_Pop">關係人交易專區<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t05st11_q2\';">背書保證與資金貸放餘額明細</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t65sb04_q2\';">資金貸與及背書保證明細表資訊</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t79sb01_q2\';">重要子公司月營收、背書保證與資金貸放餘額</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t12sc01_q2\';">月取得或處分資產資訊</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t141sb02\';">與關係人取得處分資產、進貨銷貨、應收及應付款項相關資訊</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t141sb03\';">關係人交易申報數與查核（核閱）數差異說明</li>\
								</ul>\
							</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t12sc01_q3\';">財務資料表</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t12sc01_q1\';">母子公司交互資訊(自102年6月起免申報)</li>\
							<li class = "L01_Pop">赴大陸投資資訊<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t05st15\';">赴大陸投資資訊（實際數）</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t92sb01\';">赴大陸投資資訊（自結數）</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">投資海外子公司資訊<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t05st16\';">投資海外子公司資訊（實際數）</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t92sb03\';">投資海外子公司資訊（自結數）</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">財務比率分析<img src="arrow_r.gif" class="liArrow" /><ul>\
								<li class = "L02_Pop">採IFRSs後<img src="arrow_r.gif" class="liArrow" /><ul>\
										<li class = "L03_Item" onclick="window.location.href=\'t163sb08\';">營益分析表</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t05st22_q1\';">財務分析資料</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t163sb09\';">毛利率</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t05st25_q1\';">存貨週轉率</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t05st26_q1\';">應收帳款周轉率</li>\
									</ul>\
								</li>\
								<li class = "L02_Pop">採IFRSs前<img src="arrow_r.gif" class="liArrow" /><ul>\
										<li class = "L03_Item" onclick="window.location.href=\'t05st24\';">營益分析表</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t05st22\';">財務分析資料</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t05st27\';">毛利率</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t05st25\';">存貨周轉率</li>\
										<li class = "L03_Item" onclick="window.location.href=\'t05st26\';">應收帳款周轉率表</li>\
									</ul>\
								</li>\
								</ul>\
							</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t15sf\';">衍生性商品交易資訊</li>\
							<li class = "L01_Pop">自結損益公告<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t138sb02_q1\';">自結損益公告-月申報</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t138sb02_q2\';">自結損益公告-季申報</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">創業投資公司投資資訊<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t193sb01\';">創業投資公司查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t193sb02\';">創業投資公司每月投資資訊</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t193sb03\';">創業投資公司每季投資資訊-被投資公司資訊</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t193sb04\';">未投資資金投資上市（櫃）有價證券資訊</li>\
								</ul>\
							</li>\
					</ul>\ ';
/*20211224 MAX FRANCIS begin 新增 t211sb01_q1 t211sb05 */
/*
					var menu8='\
						投資專區\
						<ul>\
							<li class = "L01_Pop">銀行(金融控股公司)大股東持股變動及設質情形專區<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t142sb01\';">銀行(金融控股公司)大股東持股變動情形申報表查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t142sb02\';">銀行(金融控股公司)大股東設質情形申報表查詢</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">財務重點專區<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Pop">上市公司<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L03_Item" onclick="window.location.href=\'t123sb04_q1\';">股票停止買賣者</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t123sb05_q1\';">變更交易方法者</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t123sb06_q1\';">全體上市公司</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t123sb14\';">全體第一上市公司</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t123sb32\';">全體創新板上市公司</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t123sb09_q1\';">按產業類別查詢</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t123sb10_q1\';">按個別公司查詢</li>\
										</ul>\
									</li>\
									<li class = "L02_Pop">上櫃公司<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L03_Item" onclick="window.location.href=\'t123sb04_q2\';">股票停止買賣者</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t123sb05_q2\';">變更交易方法者</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t123sb11_q1\';">管理股票</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t123sb06_q2\';">全體上櫃公司</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t123sb14_q1\';">全體第一上櫃公司</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t123sb09_q2\';">按產業類別查詢</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t123sb10_q2\';">按個別公司查詢</li>\
										</ul>\
									</li>\
									<li class = "L02_Pop">興櫃公司<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L03_Item" onclick="window.location.href=\'t123sb04_q3\';">股票停止買賣者</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t123sb06_q3\';">全體興櫃公司</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t123sb22\';">全體外國興櫃公司</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t123sb09_q3\';">按產業類別查詢</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t123sb10_q3\';">按個別公司查詢</li>\
										</ul>\
									</li>\
									<li class = "L02_Pop" onclick="window.location.href=\'t123sb01_TDR\';">TDR發行公司</li>\
									<li class = "L02_Pop" onclick="window.location.href=\'t123sb00_q1\';">財務重點專區</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">受輔導專區<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t68sb01_q1\';">輔導中公司名單</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t68sb01_q2\';">外國企業輔導中公司名單</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t68sb02\';">暫停或終止輔導公司名單</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t68sh02\';">重大事項對公司財務業務影響情形之公告查詢</li>\
								</ul>\
							</li>\
							<li class = "L01_Item" onclick="window.location.href=\'bfhtm_q2\';">募資計劃執行專區</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t51sb09\';">採彈性面額(每股面額非新台幣10元)公司專區</li>\
							<li class = "L01_Pop">外國企業第一上市櫃專區<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t132sb02\';">個別公司資訊</li>\
									<li class = "L02_Pop">彙總報表<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L03_Pop">採用IFRSs後<img src="arrow_r.gif" class="liArrow" /><ul>\
												<li class = "L04_Item" onclick="window.location.href=\'t132sb23\';">外國企業各季損益表</li>\
												<li class = "L04_Item" onclick="window.location.href=\'t132sb24\';">外國企業各季資產負債季節查詢彙總表</li>\
												<li class = "L04_Item" onclick="window.location.href=\'t132sb25\';">外國企業財務分析</li>\
												</ul>\
											</li>\
											<li class = "L03_Pop">採用IFRSs前<img src="arrow_r.gif" class="liArrow" /><ul>\
												<li class = "L04_Item" onclick="window.location.href=\'t132sb21_q1\';">外國企業各季損益表</li>\
												<li class = "L04_Item" onclick="window.location.href=\'t132sb22_q1\';">外國企業各季資產負債季節查詢彙總表</li>\
												<li class = "L04_Item" onclick="window.location.href=\'t51sb02_q2\';">外國企業財務分析</li>\
												</ul>\
											</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t51sb01_q1\';">外國企業基本資料彙總表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t108sb26_q1\';">外國企業召開股東常(臨時)會公告資料</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t05st09_new_q1\';">外國企業股利分派情形</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t108sb27_q1\';">外國企業除權息公告</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t21sc04_ifrs_q1\';">外國企業每月營業收入</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t138sb01_q1\';">外國企業自結損益</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t100sb02_1_q1\';">外國企業法人說明會一覽表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t114sb10\';">外國企業營運地區資訊</li>\
										</ul>\
									</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">臺灣存託憑證專區<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t132sb05_1\';">個別公司資訊</li>\
									<li class = "L02_Pop">彙總報表<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L03_Item" onclick="window.location.href=\'t132sb05_2\';">臺灣存託憑證收盤價彙總表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t132sb05_3\';">臺灣存託憑證發行人簡明財務報表彙總表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t132sb05_4\';">臺灣存託憑證發行人財務報告更(補)正查詢作業</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t132sb05_5\';">臺灣存託憑證發行單位數一覽表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t132sb05_6\';">原股上市地財務報告公告期限彙總表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t132sb05_7\';">臺灣存託憑證財務重點專區 </li>\
										</ul>\
									</li>\
								</ul>\
							</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t132sb08\';">科技事業專區</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t132sb26\';">創新板公司專區</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t132sb29\';">戰略新板專區</li>\
							<li class = "L01_Pop">私募專區<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t116sb01\';">私募資料查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t116sb03\';">投保中心新聞稿查詢</li>\
								</ul>\
							</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t105sb01\';">債信專區</li>\
							<li class = "L01_Pop">基金資訊<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t51sb11_q2\';">基金基本資料彙總表</li>\
									<li class = "L02_Pop">封閉型基金<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L03_Item" onclick="window.location.href=\'t78sb01_q1\';">基金每日淨資產價值彙總表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t78sb02_q1\';">基金每週投資產業類股比例彙總表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t78sb03_q1\';">基金每月持股前五大個股彙總表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t78sb04_q1\';">基金每季投資個股彙總表</li>\
										</ul>\
									</li>\
									<li class = "L02_Pop">國內成分證券指數股票型基金<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L03_Item" onclick="window.location.href=\'t78sb35\';">基金每日淨資產價值</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t78sb02_q2\';">基金每週投資產業類股比例</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t78sb03_q2\';">基金每月持股前五大個股</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t78sb04_q2\';">基金每季持股明細表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t78sb05\';">基金淨值及指數歷史表現比較表</li>\
										</ul>\
									</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t78sb30\';">連結式指數股票型基金</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t78sb31\';">境外指數股票型基金</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t78sb32\';">國外成分/加掛外幣證券指數股票型基金</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t78sb33\';">槓桿/反向/加掛外幣指數股票型基金</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t78sb34\';">指數/加掛外幣股票型期貨信託基金</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t78sb21\';">最近三月歷史重大訊息</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t78sb20\';">歷史重大訊息</li>\
									<li class = "L02_Item" onclick="window.location.href=\'o_t51sb11\';">櫃買基金基本資料彙總表</li>\
									<li class = "L02_Pop">櫃買國內成分股及債券成分之指數股票型基金<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L03_Item" onclick="window.location.href=\'o_t78sb01\';">櫃買基金每日淨資產價值彙總表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'o_t78sb02\';">櫃買基金每週投資產業類股比例彙總表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'o_t78sb03\';">櫃買基金每月持股前五大個股彙總表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'o_t78sb05\';">櫃買基金淨值及指數歷史表現比較表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'o_t78sb04\';">櫃買基金每季投資個股彙總表</li>\
										</ul>\
									</li>\
									<li class = "L02_Item" onclick="window.location.href=\'o_t78sb32\';">櫃買國外成分股及債券成分/加掛外幣之指數股票型基金</li>\
									<li class = "L02_Item" onclick="window.location.href=\'o_t78sb33\';">櫃買槓桿/反向/加掛外幣指數股票型基金</li>\
									<li class = "L02_Item" onclick="window.location.href=\'o_t78sb34\';">櫃買指數/加掛外幣股票型期貨信託基金</li>\
									<li class = "L02_Item" onclick="window.location.href=\'o_t78sb21\';">櫃買基金最近三月歷史重大訊息</li>\
									<li class = "L02_Item" onclick="window.location.href=\'o_t78sb20\';">櫃買基金歷史重大訊息</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t108sb30\';">基金、ETF、REITs公告彙總查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t57sb01_q6\';">基金財務報告書</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t57sb01_q7\';">基金公開說明書</li>\
									<li class = "L02_Pop">ETF發行單位(轉換)數異動查詢<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L03_Item" onclick="window.location.href=\'t78sb36\';">上市ETF</li>\
											<li class = "L03_Item" onclick="window.location.href=\'o_t78sb36\';">上櫃ETF</li>\
										</ul>\
									</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">ETN資訊<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t204sb05\';">ETN個別基本資料查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t204sb06\';">ETN彙總基本資料查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t204sb01\';">每日指標價值</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t204sb03\';">每月資金運用情形</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t204sb04\';">歷史表現表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t204sb07\';">重大訊息</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t204sb08\';">公告查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t204sb09\';">公開說明書</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t204sb02\';">發行單位數異動查詢</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">信用評等專區<img src="arrow_r.gif" class="liArrow" /><ul>\
								<li class = "L02_Pop">證券商信用評等專區<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t109sb03_w1\';">證券商代號查詢</li>\
										<li class = "L02_Item" onclick="window.location.href=\'t109sb02_w1\';">證券商信用評等資料查詢</li>\
									</ul>\
								</li>\
								<li class = "L02_Pop" onclick="window.location.href=\'t191sb01\';">上市公司信用評等專區</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">證券商資本適足比率及風險管理資訊專區<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t122sb01_q1\';">證券商風險管理相關品質化資訊查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t17sc13_1_w1\';">證券商資本適足比率資訊查詢</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">ETF專區<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L01_Item" onclick="window.location.href=\'http://www.twse.com.tw/zh/ETF/news\';">上市ETF專區</li>\
									<li class = "L01_Item" onclick="window.open(\'http://www.tpex.org.tw/web/link/index.php?l=zh-tw&t=545&s=6\');">上櫃ETF專區</li>\
								</ul>\
							</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t168sb01\';">金管會證券期貨局裁罰案件專區</li>\
							<li class = "L01_Pop">違反資訊申報、重大訊息及說明記者會規定專區<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t132sb17\';">上市公司</li>\
									<li class = "L02_Item" onclick="window.location.href=\'o_t132sb17\';">上櫃公司</li>\
									<li class = "L02_Item" onclick="window.location.href=\'o_t132sb17_q1\';">興櫃公司</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">公開收購資訊專區<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t162sb01\';">公開收購資料查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t162sb02\';">公開收購統計彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t162sb03\';">公開收購申報資料彙總表</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">庫藏股資訊專區<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t35sb01_q1\';">庫藏股買回基本資料查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t35sb01_q2\';">庫藏買回達一定標準查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t35sb01_q3\';">期間屆滿(執行完畢)公告事項查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t35sb06\';">庫藏股轉讓予員工基本資料查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t35sb07\';">董事會決議變更買回股份轉讓員工辦法之公告事項查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t35sc09\';">庫藏股統計彙總表</li>\
								</ul>\
							</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t174sb01_q1\';">經營權及營業範圍異(變)動專區</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t189sb01\';">企業併購法資訊專區</li>\
					</ul>\ ';
*/
					var menu8='\
						投資專區\
						<ul>\
							<li class = "L01_Pop">銀行(金融控股公司)大股東持股變動及設質情形專區<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t142sb01\';">銀行(金融控股公司)大股東持股變動情形申報表查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t142sb02\';">銀行(金融控股公司)大股東設質情形申報表查詢</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">財務及交易資訊重點專區<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t211sb01_q1\';">財務資訊</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t211sb05\';">交易資訊</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t123sb01_TDR\';">TDR發行公司</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t213sb01\';">定期公告財務資訊</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">受輔導專區<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t68sb01_q1\';">輔導中公司名單</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t68sb01_q2\';">外國企業輔導中公司名單</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t68sb02\';">暫停或終止輔導公司名單</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t68sh02\';">重大事項對公司財務業務影響情形之公告查詢</li>\
								</ul>\
							</li>\
							<li class = "L01_Item" onclick="window.location.href=\'bfhtm_q2\';">募資計劃執行專區</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t51sb09\';">採彈性面額(每股面額非新台幣10元)公司專區</li>\
							<li class = "L01_Pop">外國企業第一上市櫃專區<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t132sb02\';">個別公司資訊</li>\
									<li class = "L02_Pop">彙總報表<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L03_Pop">採用IFRSs後<img src="arrow_r.gif" class="liArrow" /><ul>\
												<li class = "L04_Item" onclick="window.location.href=\'t132sb23\';">外國企業各季損益表</li>\
												<li class = "L04_Item" onclick="window.location.href=\'t132sb24\';">外國企業各季資產負債季節查詢彙總表</li>\
												<li class = "L04_Item" onclick="window.location.href=\'t132sb25\';">外國企業財務分析</li>\
												</ul>\
											</li>\
											<li class = "L03_Pop">採用IFRSs前<img src="arrow_r.gif" class="liArrow" /><ul>\
												<li class = "L04_Item" onclick="window.location.href=\'t132sb21_q1\';">外國企業各季損益表</li>\
												<li class = "L04_Item" onclick="window.location.href=\'t132sb22_q1\';">外國企業各季資產負債季節查詢彙總表</li>\
												<li class = "L04_Item" onclick="window.location.href=\'t51sb02_q2\';">外國企業財務分析</li>\
												</ul>\
											</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t51sb01_q1\';">外國企業基本資料彙總表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t108sb26_q1\';">外國企業召開股東常(臨時)會公告資料</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t05st09_new_q1\';">外國企業股利分派情形</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t108sb27_q1\';">外國企業除權息公告</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t21sc04_ifrs_q1\';">外國企業每月營業收入</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t138sb01_q1\';">外國企業自結損益</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t100sb02_1_q1\';">外國企業法人說明會一覽表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t114sb10\';">外國企業營運地區資訊</li>\
										</ul>\
									</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">臺灣存託憑證專區<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t132sb05_1\';">個別公司資訊</li>\
									<li class = "L02_Pop">彙總報表<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L03_Item" onclick="window.location.href=\'t132sb05_2\';">臺灣存託憑證收盤價彙總表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t132sb05_3\';">臺灣存託憑證發行人簡明財務報表彙總表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t132sb05_4\';">臺灣存託憑證發行人財務報告更(補)正查詢作業</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t132sb05_5\';">臺灣存託憑證發行單位數一覽表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t132sb05_6\';">原股上市地財務報告公告期限彙總表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t132sb05_7\';">臺灣存託憑證財務重點專區 </li>\
										</ul>\
									</li>\
								</ul>\
							</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t132sb08\';">科技事業專區</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t132sb26\';">創新板公司專區</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t132sb29\';">戰略新板專區</li>\
							<li class = "L01_Pop">私募專區<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t116sb01\';">私募資料查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t116sb03\';">投保中心新聞稿查詢</li>\
								</ul>\
							</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t105sb01\';">債信專區</li>\
							<li class = "L01_Pop">基金資訊<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t51sb11_q2\';">基金基本資料彙總表</li>\
									<li class = "L02_Pop">封閉型基金<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L03_Item" onclick="window.location.href=\'t78sb01_q1\';">基金每日淨資產價值彙總表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t78sb02_q1\';">基金每週投資產業類股比例彙總表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t78sb03_q1\';">基金每月持股前五大個股彙總表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t78sb04_q1\';">基金每季投資個股彙總表</li>\
										</ul>\
									</li>\
									<li class = "L02_Pop">國內成分證券指數股票型基金<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L03_Item" onclick="window.location.href=\'t78sb35\';">基金每日淨資產價值</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t78sb02_q2\';">基金每週投資產業類股比例</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t78sb03_q2\';">基金每月持股前五大個股</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t78sb04_q2\';">基金每季持股明細表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t78sb05\';">基金淨值及指數歷史表現比較表</li>\
										</ul>\
									</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t78sb30\';">連結式指數股票型基金</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t78sb31\';">境外指數股票型基金</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t78sb32\';">國外成分/加掛外幣證券指數股票型基金</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t78sb33\';">槓桿/反向/加掛外幣指數股票型基金</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t78sb34\';">指數/加掛外幣股票型期貨信託基金</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t78sb21\';">最近三月歷史重大訊息</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t78sb20\';">歷史重大訊息</li>\
									<li class = "L02_Item" onclick="window.location.href=\'o_t51sb11\';">櫃買基金基本資料彙總表</li>\
									<li class = "L02_Pop">櫃買國內成分股及債券成分之指數股票型基金<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L03_Item" onclick="window.location.href=\'o_t78sb01\';">櫃買基金每日淨資產價值彙總表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'o_t78sb02\';">櫃買基金每週投資產業類股比例彙總表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'o_t78sb03\';">櫃買基金每月持股前五大個股彙總表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'o_t78sb05\';">櫃買基金淨值及指數歷史表現比較表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'o_t78sb04\';">櫃買基金每季投資個股彙總表</li>\
										</ul>\
									</li>\
									<li class = "L02_Item" onclick="window.location.href=\'o_t78sb32\';">櫃買國外成分股及債券成分/加掛外幣之指數股票型基金</li>\
									<li class = "L02_Item" onclick="window.location.href=\'o_t78sb33\';">櫃買槓桿/反向/加掛外幣指數股票型基金</li>\
									<li class = "L02_Item" onclick="window.location.href=\'o_t78sb34\';">櫃買指數/加掛外幣股票型期貨信託基金</li>\
									<li class = "L02_Item" onclick="window.location.href=\'o_t78sb21\';">櫃買基金最近三月歷史重大訊息</li>\
									<li class = "L02_Item" onclick="window.location.href=\'o_t78sb20\';">櫃買基金歷史重大訊息</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t108sb30\';">基金、ETF、REITs公告彙總查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t57sb01_q6\';">基金財務報告書</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t57sb01_q7\';">基金公開說明書</li>\
									<li class = "L02_Pop">ETF發行單位(轉換)數異動查詢<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L03_Item" onclick="window.location.href=\'t78sb36\';">上市ETF</li>\
											<li class = "L03_Item" onclick="window.location.href=\'o_t78sb36\';">上櫃ETF</li>\
										</ul>\
									</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">ETN資訊<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t204sb05\';">ETN個別基本資料查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t204sb06\';">ETN彙總基本資料查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t204sb01\';">每日指標價值</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t204sb03\';">每月資金運用情形</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t204sb04\';">歷史表現表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t204sb07\';">重大訊息</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t204sb08\';">公告查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t204sb09\';">公開說明書</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t204sb02\';">發行單位數異動查詢</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">信用評等專區<img src="arrow_r.gif" class="liArrow" /><ul>\
								<li class = "L02_Pop">證券商信用評等專區<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t109sb03_w1\';">證券商代號查詢</li>\
										<li class = "L02_Item" onclick="window.location.href=\'t109sb02_w1\';">證券商信用評等資料查詢</li>\
									</ul>\
								</li>\
								<li class = "L02_Pop" onclick="window.location.href=\'t191sb01\';">上市公司信用評等專區</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">證券商資本適足比率及風險管理資訊專區<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t122sb01_q1\';">證券商風險管理相關品質化資訊查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t17sc13_1_w1\';">證券商資本適足比率資訊查詢</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">ETF專區<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L01_Item" onclick="window.location.href=\'http://www.twse.com.tw/zh/ETF/news\';">上市ETF專區</li>\
									<li class = "L01_Item" onclick="window.open(\'http://www.tpex.org.tw/web/link/index.php?l=zh-tw&t=545&s=6\');">上櫃ETF專區</li>\
								</ul>\
							</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t168sb01\';">金管會證券期貨局裁罰案件專區</li>\
							<li class = "L01_Pop">違反資訊申報、重大訊息及說明記者會規定專區<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t132sb17\';">上市公司</li>\
									<li class = "L02_Item" onclick="window.location.href=\'o_t132sb17\';">上櫃公司</li>\
									<li class = "L02_Item" onclick="window.location.href=\'o_t132sb17_q1\';">興櫃公司</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">公開收購資訊專區<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t162sb01\';">公開收購資料查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t162sb02\';">公開收購統計彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t162sb03\';">公開收購申報資料彙總表</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">庫藏股資訊專區<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t35sb01_q1\';">庫藏股買回基本資料查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t35sb01_q2\';">庫藏買回達一定標準查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t35sb01_q3\';">期間屆滿(執行完畢)公告事項查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t35sb06\';">庫藏股轉讓予員工基本資料查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t35sb07\';">董事會決議變更買回股份轉讓員工辦法之公告事項查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t35sc09\';">庫藏股統計彙總表</li>\
								</ul>\
							</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t174sb01_q1\';">經營權及營業範圍異(變)動專區</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t189sb01\';">企業併購法資訊專區</li>\
						</ul>\ ';
/*20211224 MAX FRANCIS end 新增 t211sb01_q1 t211sb05 */
					var menu9='\
						認購（售）權證\
						<ul>\
							<li class = "L01_Item" onclick="window.location.href=\'t90sbfa01\';">認購(售)權證搜尋器</li>\
							<li class = "L01_Pop">權證庫存不足500張彙總表<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t90sb03\';">上市權證造市專戶庫存不足500張彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'o_t90sb03\';">上櫃權證造市專戶庫存不足500張彙總表</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">個別權證基本資料查詢<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t05st48_q1\';">上市認購（售）權證個別權證基本資料查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t05st48_q2\';">上櫃認購（售）權證個別權證基本資料查詢</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">權證基本資料彙總查詢<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t90sb01\';">認購（售）權證基本資料彙總表（含下市權證）</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t110sb02\';">上市、櫃認購（售）權證基本資料彙總查詢</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">認購(售)權證履約價格／履約點數重設公告彙總表查詢<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t95sb03\';">上市認購（售）權證履約價格／履約點數重設公告彙總表查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'o_t95sb03\';">上櫃認購（售）權證履約價格／履約點數重設公告彙總表查詢</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">認購(售)權證履約價格及行使比例調整公告彙總表查詢<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t95sb02\';">上市認購（售）權證履約價格及行使比例調整公告彙總表查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'o_t95sb02\';">上櫃認購（售）權證履約價格及行使比例調整公告彙總表查詢</li>\
								</ul>\
							</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t150sb02\';">國內標的之上市（櫃）認購（售）權證到期日結算價格／結算點數彙總表</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t132sb11\';">上市、櫃認購(售)權證發行人累計註銷權證數量彙總表查詢</li>\
							<li class = "L01_Pop">國內標的查詢<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t51sb11_q1\';">上市認購（售）權證（含牛熊證）標的證券為ETF查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t51sb11_otc\';">上櫃認購（售）權證（含牛熊證）標的證券為ETF查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t111sb01\';">上市認購（售）權證（含牛熊證）標的證券查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'o_t111sb01\';">上櫃認購（售）權證（含牛熊證）標的證券查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t95sb06\';">上市認購（售）權證（含牛熊證）標的指數查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'o_t95sb06\';">上櫃認購（售）權證（含牛熊證）標的指數查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'o_t95sb07\';">上櫃認購(售)權證(含牛熊證)其他標的查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t33sb03\';">上市期貨型權證標的查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'o_t33sb03\';">上櫃期貨型權證標的查詢</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">發行人公告查詢<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t95sb01\';">上市認購（售）權證發行人公告查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'o_t95sb01\';">上櫃認購（售）權證發行人公告查詢</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">認購(售)權證公開銷售說明書查詢<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t33sbAfa03\';">上市認購(售)權證公開銷售說明書查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'o_t33sbAfa03\';">上櫃認購(售)權證公開銷售說明書查詢</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">發行人基本資料查詢<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t49asb05_q1\';">上市認購（售）權證發行人基本資料查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t49asb05_q2\';">上櫃認購（售）權證發行人基本資料查詢</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">重大訊息<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t49asb01_q1\';">上市認購（售）權證發行人即時重大訊息</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t49asb01_q2\';">上櫃認購（售）權證發行人即時重大訊息</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t49asb04_q1\';">上市認購（售）權證當日重大訊息</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t49asb04_q2\';">上櫃認購（售）權證當日重大訊息</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t49asb02_q1\';">上市認購（售）權證歷史重大訊息</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t49asb02_q2\';">上櫃認購（售）權證歷史重大訊息</li>\
								</ul>\
							</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t129sb01\';">發行人發行海外認購(售)權證基本資料查詢</li>\
                                 <li class = "L01_Pop">國外標的查詢<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t150sb01\';">外國標的收盤資訊彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t33sb01\';">上市認購(售)權證之外國標的彙總查詢作業</li>\
									<li class = "L02_Item" onclick="window.location.href=\'o_t33sb02\';">上櫃認購(售)權證之外國標的彙總查詢作業</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t153sb01_q1\';">上市權證外國標的公司公告資訊查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t153sb01_q2\';">上櫃權證外國標的公司公告資訊查詢</li>\
								</ul>\
							</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t95sb07\';">權證發行資料彙總表查詢</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t33sbAfa01\';">認購(售)權證可能達到上（下）限價格或指數彙總表</li>\
							<li class = "L01_Item" onclick="window.location.href=\'t33sbAfa02\';">認購(售)權證當日達到上（下）限價格或指數彙總表</li>\
					</ul>\ ';
					var menu10='\
						債券\
						<ul>\
							<li class = "L01_Pop">綜合資料查詢<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Pop">債券發行資訊查詢<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L03_Item" onclick="window.location.href=\'t120sb02_q1\';">最近三個月現況查詢</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t120sb02_q2\';">歷史資料查詢(未含最近三個月資料)</li>\
										</ul>\
									</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t120sg03\';">債券訊息市場公告</li>\
									<li class = "L02_Pop">債券相關彙總報表查詢<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L03_Item" onclick="window.location.href=\'t120sg08_w1\';">新發行債券狀況一覽表 </li>\
											<li class = "L03_Item" onclick="window.location.href=\'t120sg08_w2\';">債券上櫃掛牌及到期下櫃明細彙總表</li>\
										</ul>\
									</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">政府債券<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t120sb02_q3\';">歷史資料查詢(未含最近三個月資料)</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t120sb02_q4\';">最近三個月現況查詢</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">普通公司債<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t120sb02_q5\';">歷史資料查詢(未含最近三個月資料)</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t120sb02_q6\';">最近三個月現況查詢</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">金融債券<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t120sb02_q7\';">歷史資料查詢(未含最近三個月資料)</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t120sb02_q8\';">最近三個月現況查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t120sb02_w1\';">各銀行金融債未發行餘額狀況</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">轉(交)換公司債與附認股權公司債<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t120sb02_q9\';">歷史資料查詢(未含最近三個月資料)</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t120sb02_q10\';">最近三個月現況查詢</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">結構型債券<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t120sb02_q13\';">歷史資料查詢(未含最近三個月資料)</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t120sb02_q14\';">最近三個月現況查詢</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">外國發行人之債券(股權商品未於我國掛牌交易者)<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t113sb01_1\';">外國發行人代號查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t113sb01\';">發行人基本資料</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t113sb02\';">外幣計價國際債券及新台幣計價外國債券基本資料查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t113sb03\';">年報電子資料查詢作業</li>\
									<li class = "L02_Pop">債券重大訊息查詢<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L02_Item" onclick="window.location.href=\'t113sb04_q1\';">當日重大訊息查詢</li>\
											<li class = "L02_Item" onclick="window.location.href=\'t113sb04_q2\';">歷史重大訊息查詢</li>\
										</ul>\
									</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t198sb04_q1\';">債券到期前變動各項公告查詢作業</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t198sb03\';">永續發展債券之計畫書/評估報告/發行後報告</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t113sb05\';">債息對照表查詢作業</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t113sb06\';">銷售說明書查詢作業</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t113sb07\';">公開說明書查詢作業</li>\
								</ul>\
							</li>\
							<li class = "L01_Item" onclick="window.open(\'http://www.gretai.org.tw/ch/bond/publish/international_bond_search/memo.php\');">國際債券</li>\
							<li class = "L01_Pop">分割債券<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.open(\'https://www.tpex.org.tw/web/bond/publish/division_search/division_search_01.php?l=zh-tw\');">代碼一覽表</li>\
									<li class = "L02_Item" onclick="window.open(\'https://www.tpex.org.tw/web/bond/publish/division_search/division_search_02.php?l=zh-tw\');">資料表</li>\
									<li class = "L02_Item" onclick="window.open(\'https://www.tpex.org.tw/web/bond/publish/division_search/division_search_03.php?l=zh-tw\');">債利息分攤基礎表</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">公告<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t108sb08_1_q2\';">轉換(附認股權)公司債公告彙總表</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t127sb00_q2\';">各項公告查詢作業</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t108sb08\';">轉換(附認股權)公司債公告(94.5.5起適用)</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t59sb09\';">發行新股、公司債暨有價證券交付或發放股利前辦理之公告(公司法第252及273條)</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t56sb24\';">債券訊息市場公告</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t198sb01\';">永續發展債券之計畫書/評估報告/發行後報告</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t59sb08\';">股票或公司債核准上市(櫃)或終止上市(櫃)之公告</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t59sb12\';">因轉換公司債、附認股權公司債轉換而發行新股公告</li>\
									<li class = "L02_Item" onclick="window.open(\'http://web2.twsa.org.tw/bond\');">普通公司債承銷公告(僅限銷售予專業投資人)</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop">債券法規查詢<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'t120sb04_w001\';">全部相關法規</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t120sb04_w002\';">政府債券</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t120sb04_w003\';">普通公司債及金融債券</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t120sb04_w004\';">外國債券/國際債券</li>\
									<li class = "L02_Item" onclick="window.location.href=\'t120sb04_w005\';">證券法令規章</li>\
								</ul>\
							</li>\
							<li class = "L01_Pop" onclick="window.open(\'http://www.tpex.org.tw/web/bond/link/index.php?l=zh-tw&t=2&s=6\');">交易資訊</li>\
					</ul> ';
					var menu11='\
						資產證券化\
						<ul>\
							<li class = "L01_Pop">綜合資料查詢<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'p_t112sb02_q1\';">證券資訊查詢作業</li>\
									<li class = "L02_Item" onclick="window.location.href=\'p_t127sb06\';">資產證券化各項商品流通在外餘額統計表</li>\
									<li class = "L02_Pop">受益證券每日淨資產價值彙整表查詢<img src="arrow_r.gif" class="liArrow" /><ul>\
											<li class = "L03_Item" onclick="window.location.href=\'t112sb01_q5\';">不動產投資信託受益證券每日淨資產價值彙總表</li>\
											<li class = "L03_Item" onclick="window.location.href=\'t112sb01_q6\';">不動產資產信託受益證券每日淨資產價值彙總表</li>\
										</ul>\
									</li>\
									<li class = "L02_Item" onclick="window.location.href=\'p_t112sb07\';">重大訊息查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'p_t112sb08\';">公告事項查詢</li>\
									<li class = "L02_Item" onclick="window.location.href=\'p_t112sb15\';">永續發展債券之計畫書/評估報告/發行後報告</li>\
									<li class = "L02_Item" onclick="window.location.href=\'p_t112sb14_q2\';">債息對照表查詢作業</li>\
									<li class = "L02_Item" onclick="window.location.href=\'p_t112_w01\';">私募證券化受益證券資訊揭露</li>\
									<li class = "L02_Item" onclick="window.location.href=\'p_t56sb25\';">資產池查詢</li>\
								</ul>\
							</li>\
							<li class = "L01_Item" onclick="window.location.href=\'p_t112sb02_q2\';">依證券代號、受託機構或特殊目的公司代號查詢</li>\
							<li class = "L01_Item" onclick="window.location.href=\'p_t112sb03\';">不動產投資信託受益證券</li>\
							<li class = "L01_Item" onclick="window.location.href=\'p_t112sb04\';">不動產資產信託受益證券</li>\
							<li class = "L01_Item" onclick="window.location.href=\'p_t112sb05\';">金融資產受益證券</li>\
							<li class = "L01_Item" onclick="window.location.href=\'p_t112sb06\';">金融資產基礎證券</li>\
							<li class = "L01_Item" onclick="window.location.href=\'p_t57sb01_w\';">電子書查詢</li>\
							<li class = "L01_Item" onclick="window.open(\'http://www.tpex.org.tw/web/bond/link/index.php?l=zh-tw&t=2&s=6\');">交易資訊查詢</li>\
							<li class = "L01_Pop">資產證券化法規查詢<img src="arrow_r.gif" class="liArrow" /><ul>\
									<li class = "L02_Item" onclick="window.location.href=\'p_t112_w001\';">不動產證券化</li>\
									<li class = "L02_Item" onclick="window.location.href=\'p_t112_w002\';">金融資產證券化</li>\
								</ul>\
							</li>\
</ul>\ ';