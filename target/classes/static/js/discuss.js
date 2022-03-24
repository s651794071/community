function like(btn, entityType, entityId, entityUserID) {
    // 向服务器发送异步请求
    $.post(
        CONTEXT_PATH +"/like",
        {"entityType":entityType, "entityId": entityId, "entityUserId": entityUserID},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus==1?'已赞':'赞');
            } else {
                alert(data.msg);
            }
        }
    );
}