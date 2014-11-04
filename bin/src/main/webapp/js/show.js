//global attr
var systemUsageChart,systemUsageData,systemUsageDataInterval;

//ready
$(document).ready(function () {
 	$("#vsplitter").wijsplitter({
 		splitterDistance: 400,
 		orientation: "horizontal",
		fullSplit: true
 	});
    $("#platformInfoTab").wijtabs();
    initial();
    $("#platformSelect").change(function(){
    	showPlatformInfos($("#platformSelect").val(),$("#platformSelect").text());
    });
    $("#vsplitter").css("overflow","hidden");
});
//functions
function initial(){
	$.get("platformManagement.do?method=listPlatforms",function(data,status){
		for(var i=0;i<data.length;i++){
			$("#platformSelect").append("<option value='"+ data[i]._id +"'>"+ data[i].platformName +"</option>");
		}
		$("#platformSelect").wijdropdown();
		if(data.length > 0){
			showPlatformInfos(data[0]._id,data[0].platformName);
		}
	});
}

function showPlatformInfos(platformInfoId,platformName){
	loadPlatformHostsInfo(platformInfoId);
	initialSystemUsageLineChart(platformName);
	loadSystemUsage(platformInfoId);
}

function loadPlatformHostsInfo(platformInfoId){
	$("#carousel").wijcarousel({
        display: 1
    });
	$.get("platformShow.do?method=listHosts&platformInfoId=" + platformInfoId, function(data,status){
		for(var i=0;i<data.length;i++){
			var divStr = "<div class='flipcard'> " +
					"<div id='front_" + data[i].hostId + "'>" +
					"<p id=pic_'" + data[i].hostId + "'><img src='images/server_ok.png'></p>";
			for(var j=0;j<data[i].instanceInfo.length;j++){
				divStr += "<p>" + data[i].instanceInfo[j].applicationName + "." + data[i].instanceInfo[j].serviceName + "(" + data[i].instanceInfo[j].instanceId + ")</p>";
			}
			divStr += "<p>" + data[i].address + "</p>";
			divStr +="</div>" +
					"<div id='back_" + data[i].hostId + "'></div>" +
					"</div>";
			$("#hostPicDiv").append(divStr);
			loadHostMonitorDataPeriodically(data[i].hostId);
		}
		$(".flipcard").wijflipcard({ 
	        height: 250, 
	        width: 200
	    });
	});
}

function loadHostMonitorDataPeriodically(hostId){
	getHostMonitorData(hostId);
	window.intervalMonitorData = setInterval(function(){
		getHostMonitorData(hostId);
	},30000);
}

/*function initialSystemUsageLineChart(platformName){
	$("#systemUsageLineChart").wijlinechart({
        showChartLabels: false,
        width: 756,
        height: 300,
        shadow: false,
        animation: { enabled: false },
        seriesTransition: { enabled: false },
        legend: { visible: false },
        hint: { enable: false },
        header: { text: platformName+"系统使用率" },
        hint: {
            content: function () {
                return this.y + '%';
            }
        },
        axis:
        {
            y: {},
            x: {}
        },
        seriesList: [{
            data: {
                x: [new Date(),new Date(),new Date(),new Date(),new Date(),new Date(),new Date(),new Date(),new Date(),new Date(),],
                y: [0,0,0,0,0,0,0,0,0,0]
            },
            markers: {
                visible: true,
                type: "circle"
            }
        }],
        seriesStyles: [{ "stroke-width": 3, stroke: "#00a6dd" }],
        seriesHoverStyles: [{ "stroke-width": 4 }]
    });
}

function loadSystemUsage(platformInfoId){
	window.intervalSystemUsageData = setInterval(function () {
		$.get("platformShow.do?method=loadSystemUsage&platformInfoId=" + platformInfoId,function(data,status){
			$("#systemUsageLineChart").wijlinechart("addSeriesPoint", 0, { x: new Date(), y: data.usage }, true);
	        //animateChart();
		});   
    }, 5000);
}

function animateChart() {
    var path = $("#systemUsageLineChart").wijlinechart("getLinePath", 0),
        markers = $("#systemUsageLineChart").wijlinechart("getLineMarkers", 0),
        box = path.getBBox(),
        width = $("#systemUsageLineChart").wijlinechart("option", "width") / 10.0,
        anim = Raphael.animation({ transform: Raphael.format("...t{0},0", -width) }, 5000);
    path.animate(anim);
    if (path.shadow) {
        var pathShadow = path.shadow;
        pathShadow.animate(anim);
    }
    markers.animate(anim);
    var rect = box.x + " " + (box.y - 5) + " " + box.width + " " + (box.height + 10);
    path.wijAttr("clip-rect", rect);
    markers.attr("clip-rect", rect);
}
*/
function initialSystemUsageLineChart(platformName){
	systemUsageData = new wijmo.collections.ObservableArray();
	systemUsageChart = new wijmo.chart.FlexChart('#systemUsageLineChart');
	systemUsageChart.initialize({
	    chartType: wijmo.chart.ChartType.LineSymbols,
	    itemsSource: systemUsageData,
	    bindingX: 'time',
	    axisX: { format: 'mm:ss' },
	    series: [
	      { name: '系统使用率', binding: 'usage' }
	    ]
	});
}
function loadSystemUsage(platformInfoId){
	getSystemUsage(platformInfoId);
	systemUsageDataInterval = setInterval(function(){
		getSystemUsage(platformInfoId);
	}, 5000);
}

window.dispose = function () {
    if (systemUsageDataInterval) {
        clearInterval(systemUsageDataInterval);
        systemUsageDataInterval = null;
    }
    if (intervalMonitorData){
    	clearInterval(intervalMonitorData);
    	intervalMonitorData = null;
    }
};

function getHostMonitorData(hostId){
	$.get("platformShow.do?method=getHostMonitorData&hostId=" + hostId,function(data,status){
		var divStr = "<p>CPU使用率：" + data.cpuUsagePercent + "%</p><p>内存使用：</p>" +
		"<p>" + data.usedMemorySize + "/" + data.memorySize + "</p>";
		var num = data.usedIOSpeed / data.ioReadSpeed * 100.0;
		divStr += "<p>IO使用率：" + num.toFixed(4) + "%</p>";
		$("#back_"+hostId).empty();
		$("#back_"+hostId).append(divStr);
	});
}
function getSystemUsage(platformInfoId){
	$.get("platformShow.do?method=loadSystemUsage&platformInfoId=" + platformInfoId,function(data,status){
		systemUsageData.push({time:new Date(),usage:data.usage});
		if (systemUsageData.length > 10) {
			systemUsageData.splice(0, 1);
		}
	}); 
}
