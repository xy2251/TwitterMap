<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	import="tmpkg.DbHelper,java.util.*,tmpkg.Twitter,org.apache.commons.lang3.StringEscapeUtils"%>
<!DOCTYPE html>
<html>
<head>
<title>Twitter Map</title>
<script type="text/javascript"
	src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAb1e5Csn_V8JIYp5no6A5lznf9jLs15o0&sensor=true&libraries=visualization"></script>
<script>
	<%DbHelper dh = new DbHelper();
		ArrayList<Twitter> list = dh.gettwits();
		int datanum = list.size();%>
	var mapdata = [
	<% for (int i=0; i<datanum;i++) {
		Twitter t = list.get(i);
		String username = StringEscapeUtils.escapeEcmaScript(t.getUsername());
		String text = StringEscapeUtils.escapeEcmaScript(t.getText());
		out.println("['"+username+"', '"+text+"', "+t.getLatitude()+", "+t.getLongtitude()+", '"+t.getKeyword()+"', '"+t.getTimestamp()+"'],");
	}
	%>
	];
	
    var map, heatmap, rtheatmap;
    var markers = [];
    var heatlocs = [];
    var rtmarkers = [];
    var rtheatlocs = [];
    function initialize() {
      var mapOptions = {
        zoom: 2,
        center: new google.maps.LatLng(40.7127, -74.0059)
      };
      map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
      var infowindow = new google.maps.InfoWindow();
 
      var i, marker;
      for (i = 0;i<mapdata.length;i++) {
    	var pinImage;
      
      	pinImage = new google.maps.MarkerImage("http://chart.googleapis.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|C6EF8C",
      		        new google.maps.Size(21, 34),
      		        new google.maps.Point(0,0),
      		        new google.maps.Point(10, 34));
      	
      	marker = new google.maps.Marker({
      		position: new google.maps.LatLng(mapdata[i][2], mapdata[i][3]),
      		map: null,
      		icon: pinImage
      	});
      	markers[i] = marker;
      	heatlocs[i] = new google.maps.LatLng(mapdata[i][2], mapdata[i][3]);
      	google.maps.event.addListener(marker, 'click', (function(marker, i) {
      		return function() {
      			var contentstr = '<div>'
      			+'<h2> @'+unescape(mapdata[i][0])+' :</h2>'
      			+'<p>"'+unescape(mapdata[i][1])+'"</p>'
      			+'<p>At '+mapdata[i][5]+'</p>'
      			+'</div>';
      			infowindow.setContent(contentstr);
      			infowindow.open(map,marker);
      		};
      	})(marker, i));
      }
      var pointArray = new google.maps.MVCArray(heatlocs);
      heatmap = new google.maps.visualization.HeatmapLayer({
    	    data: pointArray
    	  });
      heatmap.set('radius', 18);
      var pointArray2 = new google.maps.MVCArray(rtheatlocs);
      rtheatmap = new google.maps.visualization.HeatmapLayer({
  	    data: pointArray2
	  });
      rtheatmap.set('radius', 18);
    }
    
    function setAllMap(map) {
    	for (var i = 0; i < markers.length; i++) {
    		markers[i].setMap(map);
    	}
    }
    function setAllrtMap(map) {
    	for (var i = 0; i < rtmarkers.length; i++) {
    		rtmarkers[i].setMap(map);
    	}
    }
    
    function changeMap(sel) {
    	heatmap.setMap(null);
    	rtheatmap.setMap(null);
    	setAllMap(null);
    	setAllrtMap(null);
    	if(sel.value=="0"){
    		setAllMap(map);
    	}
    	else if(sel.value=="1"){
			heatmap.setMap(map);
    	}
    	else if (sel.value=="2"){
    		setAllrtMap(map);
    	}
    	else if (sel.value=="3"){
    		var pointArray = new google.maps.MVCArray(rtheatlocs);
    		rtheatmap.setData(pointArray);
    		rtheatmap.setMap(map);
    	} 
    }
    
    function changeKeyword(sel) {
    	setAllMap(null);
    	if(sel.value=="0")
    		setAllMap(map);
    	else{
        	for (var i = 0; i < markers.length; i++) {
        		if (sel.value == mapdata[i][4])
        			markers[i].setMap(map);
        	}
    	}
    }

    google.maps.event.addDomListener(window, 'load', initialize);
   
    var websocket;
	function init() {
		var infowindow2 = new google.maps.InfoWindow();
		//websocket = new WebSocket("ws://ec2-54-186-52-134.us-west-2.compute.amazonaws.com:8080//markerupdate");
		websocket = new WebSocket("ws://localhost:8080//markerupdate");

		websocket.onopen = function() { document.getElementById("output").innerHTML += "<p>> CONNECTED</p>"; };
        
        websocket.onmessage = function(evt) { 
        	var data = JSON.parse(evt.data);
        	var pinImage;
        		pinImage = new google.maps.MarkerImage("http://chart.googleapis.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|C6EF8C",
        		        new google.maps.Size(21, 34),
        		        new google.maps.Point(0,0),
        		        new google.maps.Point(10, 34));
        	
        	marker = new google.maps.Marker({
          		position: new google.maps.LatLng(data.latitude, data.longtitude),
          		map: map,
          		icon: pinImage
          	});
          	google.maps.event.addListener(marker, 'click', (function(marker) {
          		return function() {
          			var contentstr = '<div>'
          			+'<h2> @'+unescape(data.text)+' :</h2>'
          			+'<p>"'+unescape(data.username)+'"</p>'
          			+'<p>At '+data.timestamp+'</p>'
          			+'</div>';
          			infowindow2.setContent(contentstr);
          			infowindow2.open(map,marker);
          		};
          	})(marker));
          	rtmarkers.push(marker);
          	var temploc = new google.maps.LatLng(data.latitude, data.longtitude);
          	rtheatlocs.push(temploc);
		};
        
        websocket.onerror = function(evt) { document.getElementById("output").innerHTML += "<p style='color: red;'>> ERROR: " + evt.data + "</p>"; };
	}
	window.resizeTo(width,height);
	window.addEventListener("load", init, false);

    </script>
    
<style>
#header {
	background-color: black;
	color: white;
	text-align: center;
	padding: 5px;
}

#nav {
	line-height: 30px;
	background-color: #eeeeee;
	height: 500px;
	width: 150px;
	float: left;
	padding: 5px;
}

#section {
	width: 450px;
	float: left;
	padding: 10px;
}

#footer {
	background-color: black;
	color: white;
	clear: both;
	text-align: center;
	padding: 5px;
}
</style>
</head>
<body>
	<div id="header">
		<h1>Twitter Map</h1>
	</div>
	
	<div id="nav">
		<br> <h2><b>Menu</b></h2><br>
		Map Mode:<br>
		<select id="heatmapSelect" onchange="changeMap(this)">
			<option value="99" selected="selected">choose mode</option>
			<option value="0">history data</option>
			<option value="1">history heat map</option>
			<option value="2">real time data</option>
			<option value="3">real time heat map</option>
		</select>
		<br>
		Key Word:<br>
		<select id="keywordSelect" onchange="changeKeyword(this)">
			<option value="0">All</option>
			<option value="is">is</option>
			<option value="am">am</option>
			<option value="are">are</option>
		</select>
		<br>
		<br>
		<p>Click marker to see what people tweet:) </p>
		<p><font color="blue"> still working on<br> real time..</font></p>

		
	</div>
	<div id="section">
		<div id="map-canvas" style="width:800px;height:500px;"></div>
	</div>
	<div id="footer">
		Copyright @ Xiaofan Yang, xy2251, March 4th, 2015
	</div>
	
</body>
</html>