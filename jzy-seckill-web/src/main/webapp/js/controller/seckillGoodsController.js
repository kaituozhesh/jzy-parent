app.controller('seckillGoodsController', function ($scope, $location, $interval, seckillGoodsService) {
   // 读取列表数据绑定到表单中
   $scope.findList = function () {
       seckillGoodsService.findList().success(function (response) {
           $scope.list = response;
       });
   };

   // 查询商品
   $scope.findOne = function () {
       // 接收参数
       var id = $location.search()['id'];
       seckillGoodsService.findOne(id).success(function (response) {
           $scope.entity = response;

            // 获取从结束时间到当前日期的秒数
            allSecond = Math.floor((new Date($scope.entity.endTime).getTime() - new Date().getTime()) / 1000);

           // 倒计时开始
            time = $interval(function () {
                allSecond = allSecond - 1;
                $scope.timeString = converTimeString(allSecond);
                if (allSecond <= 0){
                    $interval.cancel(time);
                }
           }, 1000);
       })
   };

   // 转换秒为  天  小时  分钟  秒
   converTimeString = function (allSecond) {
       // var d = new Date(allSecond * 1000);
       // $scope.orderTime =  d.getDate() + "天 " + d.getHours() + "：" + d.getMinutes() + "：" + d.getSeconds();
       var days= Math.floor( allSecond / (60 * 60 * 24));//天数
       var hours= Math.floor( (allSecond - days * 60 * 60 * 24) / (60 * 60) );//小数数
       var minutes= Math.floor(  (allSecond - days * 60 * 60 * 24 - hours * 60 * 60) / 60    );//分钟数
       var seconds= allSecond - days * 60 * 60 * 24 - hours * 60 * 60 - minutes * 60; //秒数
       var timeString="";
       if(days>0){
           timeString=days+"天 ";
       }
       return timeString+hours+":"+minutes+":"+seconds;
   };

   $scope.submitOrder = function () {
       seckillGoodsService.submitOrder($scope.entity.id).success(function (response) {
           // 下单成功
           if (response.success){
               alert("抢购成功， 请在5分钟之内完成支付");
               location.href = "pay.html";
           } else {
               alert(response.message);
           }
       });
   }

});