$(function(){
	$("#publishBtn").click(publish);    // $("#publishBtn")这是JQuery的id选择器
	// <button type="button" class="btn btn-primary" id="publishBtn">发布</button>
});

function publish() {
	$("#publishModal").modal("hide"); // 点击发布后隐藏掉这个发布帖子的框

	// 获取标题和内容
	const title = $("#recipient-name").val(); // <label for="recipient-name" class="col-form-label">标题：</label>
	const content = $("#message-text").val();
	// 发送异步请求
	$.post(
		CONTEXT_PATH + "/discuss/add",
		{"title": title, "content": content},
		function(data) {
			console.log(data);

			data = $.parseJSON(data);
			console.log(data.code);
			console.log(data.msg);

			// 在提示框中显示返回消息
			$("#hintBody").text(data.msg);
			// 显示提示框
			$("#hintModal").modal("show");
			// 2秒后隐藏提示框
			setTimeout(function() {
				$("#hintModal").modal("hide");
				// 刷新页面
				if(data.code == 0) {
					window.location.reload();
				}
			}, 2000);
		}
	);

}