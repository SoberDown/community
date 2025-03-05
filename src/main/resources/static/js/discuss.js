//写入点赞的异步请求
function like(btn,entityType,entityId,entityUserId,postId){
    //因为后端改动多传一个参数postId,所以异步方法一起改动
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType":entityType,"entityId":entityId,"entityUserId":entityUserId,"postId":postId},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0){
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus==1?"已赞":"赞");
            } else {
                alert(data.msg);
            }
        }
    )
}
