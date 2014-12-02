//ready
$(document).ready(function () {
   $("#vsplitter").wijsplitter({
   		splitterDistance: 300,
		fullSplit: true	
   });
   $('#dialog').wijdialog({
        captionButtons: {
            refresh: { visible: false}
        }
    });
   $("button").button();
   ko.applyBindings(viewModel);
   getPlatformList();
   $("#newButton").click(function(){
	   $("#platformInfoFrame").attr("src","newPlatform.html");
   });
   $("#deleteButton").click(function(){
	   var array = new Array();
	   var nodes = $("#tree").wijtree("getNodes");
	   for (var i = 0; i < nodes.length; i++) { 
		   var node = nodes[i];
		   var cn = node.element.wijtreenode("getNodes");
		   for(var m=0;m<cn.length;m++){
               if(cn[m].options.checked == true ){
                   array.push(cn[m].options.id);
               }
		   }
	   }
	   if(array.length > 0){
		   $.ajax({
			  url: "../platformManagement.do?method=deletePlatforms",
			  data: {"platformIds": array},
			  dataType: "json",
			  type: "POST",
			  traditional: true,
			  success:function(data,status){
				  alert("删除平台成功！");
				  getPlatformList();
			  },
			  error:function(){
				  alert("删除平台失败！");
			  }
		   });
	   }else{
		   alert("请选择要删除的平台！");
	   }
	   
   });
});
//ko vm
var viewModel={
	showCheckBoxes: true, 
	nodes: ko.observableArray([]),
	treeClickFunction: function(e,data){
		if(data.options.isPlatform){
			var id = data.options.id;
			$("#platformInfoFrame").attr("src","platform.html?platformId="+id);
		}
	}
};

function getPlatformList(){
	$.get("../platformManagement.do?method=listPlatforms",function(data){
		var nodes = new Array();
		nodes[0] = {
			text: "cloudify",
			isPlatform: false
		};
		var secondNodes = new Array();
		nodes[0].nodes = secondNodes;
		for(var i=0;i<data.length;i++){
			secondNodes[i] = {
				text: data[i].platformName,
				id : data[i]._id,
				isPlatform: true
			};
		}
		viewModel.nodes(nodes);
	});
}