$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	//关闭弹出框
	$("#sendModal").modal("hide");

	var toName = $("#recipient-name").val();
	var content = $("#message-text").val();
	$.post(
		CONTEXT_PATH + "/letter/send",
		{"toName":toName,"content":content},
		function (data){
			data = $.parseJSON(data);
			if (data.code == 0){
				$("#hintBody").text("发送成功!");
			} else {
				$("#hintBody").text(data.msg);
			}
			//显示提示框
			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				location.reload();//刷新页面
			}, 2000);//2秒后自动关闭

		}
	);

}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}