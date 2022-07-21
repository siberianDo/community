$(function(){
	$("#publishBtn").click(publish);
});

function publish() {

    $("#publishModal").modal("hide");

    //请求服务器之前获取服务器所需数据
    var title=$("#recipient-name").val();
    var content=$("#message-text").val();

	$.post(
        CONTEXT_PATH+"/discuss/add",
        {"title":title,"content":content},
        function(data){
            data=$.parseJSON(data);
            //显示提示信息到提示框
            $("#hintBody").text(data.msg);
            //显示提示框
            $("#hintModal").modal("show");
            //两秒后隐藏提示框
            setTimeout(function(){
                $("#hintModal").modal("hide");
                //刷新页面，显示发布的帖子
                if(data.code==200){
                    window.location.reload();
                }
            }, 2000);
        }
    );
}