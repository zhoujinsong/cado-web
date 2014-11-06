//ready fun
$(document).ready(function () {
    $("#menu").wijmenu({
        checkable: true
    });
    $("#content").load(function(){ 
        $(this).height(0); //用于每次刷新时控制IFRAME高度初始化 
        var height = $(this).contents().height() + 10; 
        $(this).height( height < 500 ? 500 : height ); 
      }); 
});