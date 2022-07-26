$(function () {
    $("#sendBtn").click(send_letter);
    $(".delMessage").click(delete_msg);
});

function send_letter() {
    $("#sendModal").modal("hide");

    //获取接收人以及私信内容数据
    var toName = $("#recipient-name").val();
    var content = $("#message-text").val();

    $.post(
        CONTEXT_PATH + "/letter/send",
        {"toName": toName, "content": content},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                $("#hintBody").text("私信发送成功！");
            } else {
                $("#hintBody").text(data.msg);
            }
            $("#hintModal").modal("show");
            setTimeout(function () {
                $("#hintModal").modal("hide");
                window.location.reload();
            }, 2000);
        }
    );
}

function delete_msg() {

    //获取id
    var id = $(this).prev().val();
    console.log(id);
    $.get(
        CONTEXT_PATH + "/letter/del",
        {"id": id},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                alert("删除私信成功！")
            } else {
                alert(data.msg);
            }
        }
    )
    $(this).parents(".media").remove();
}