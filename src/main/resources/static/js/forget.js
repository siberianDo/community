$(function(){
    $("#verifyCodeBtn").click(getVerifyCode);
});

function getVerifyCode(){
//    通过ID获取字段值
    var email =$("#your-email").val();
//    判断email是否为空
    if(!email){
//    通过弹出框方式提示错误信息
        alert("请输入邮箱！");
    }

    $.get(
        CONTEXT_PATH + "/forget/code",
        {"email":email},
        function(data){
            data=$.parseJson(data);
            if(data.code==0){
                alert("验证码已发送到您的邮箱，请注意查看！")
            }else{
                alert(data.msg);
            }
        }
    );
}