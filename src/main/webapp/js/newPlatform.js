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
	$("#formReset").click(function(){
		$("#newPlatformForm").reset();
	});
	$("#formSubmit").click(function(){
		$.ajax({
			cache: true,
			type: "POST",
			url: "../platformManagement.do?method=createPlatform",
			data: $("#newPlatformForm").serialize(),
			error: function(request){
				alert("新增平台失败！");
			},
			success:function(data){
				alert("新增平台成功！");
				parent.getPlatformList();
			}
		});
	});
});