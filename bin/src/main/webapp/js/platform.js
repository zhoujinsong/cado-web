//ready
$(document).ready(function () {
	$("#baseInfo").wijexpander();
	$("#dataFetchArg").wijexpander();
	$("#dataHandleArg").wijexpander();
	$(".inputtext").wijtextbox();
	$(".inputnumber").wijinputnumber({
		decimalPlaces: 0,
	    increment: 5,
	    showSpinner: true
	});
	$("select").wijdropdown();
	$("button").button();
	initial();
	ko.applyBindings(viewModel);
	$("#formUpdate").click(function(){
		$.ajax({
			cache: true,
			type: "POST",
			url: "platformManagement.do?method=updatePlatform",
			data: $("#platformForm").serialize(),
			error: function(request){
				alert("修改平台失败！");
			},
			success:function(data){
				alert("修改平台成功！");
			}
		});
	});
});

//ko vm
var viewModel = {
	_id: ko.observable(0),
	platformClass: ko.observable(""),
	platformVer: ko.observable(""),
	platformName: ko.observable(""),
	platformAddr: ko.observable(""),
	platformPort: ko.observable(""),
	hostDataFetchCycle: ko.observable(0),
	appDataFetchCycle: ko.observable(0),
	hostMonitorCycle: ko.observable(0),
	appMonitorCycle: ko.observable(0),
	platformUsageMax: ko.observable(0),
	platformUsageMin: ko.observable(0),
	cpuWeight: ko.observable(0),
	memoryWeight: ko.observable(0),
	ioWeight: ko.observable(0),
	networkWeight: ko.observable(0)
};

//functions
function initial(){
	var platformId = getQueryStr(window.document.location.href,"platformId");
	$.get("platformManagement.do?method=getPlatform&platformId=" + platformId,function(data,status){
		viewModel._id(data._id);
		viewModel.platformClass(data.platformClass);
		viewModel.platformVer(data.platformVer);
		viewModel.platformName(data.platformName);
		viewModel.platformAddr(data.platformAddr);
		viewModel.platformPort(data.platformPort);
		viewModel.hostDataFetchCycle(data.hostDataFetchCycle);
		viewModel.appDataFetchCycle(data.appDataFetchCycle);
		viewModel.hostMonitorCycle(data.hostMonitorCycle);
		viewModel.appMonitorCycle(data.appMonitorCycle);
		viewModel.platformUsageMax(data.platformUsageMax);
		viewModel.platformUsageMin(data.platformUsageMin);
		viewModel.cpuWeight(data.cpuWeight);
		viewModel.memoryWeight(data.memoryWeight);
		viewModel.ioWeight(data.ioWeight);
		viewModel.networkWeight(data.networkWeight);
	});
}
function getQueryStr(url,str){  
    var rs = new RegExp("(^|)"+str+"=([^/&]*)(/&|$)","gi").exec(url), tmp;  
  
    if(tmp=rs){  
        return tmp[2];  
    }  
  
    // parameter cannot be found  
    return "";  
} 