app.controller('payController', function ($scope, $location, payService) {
   $scope.createNative = function () {
       payService.createNative().success(function (response) {
           // 显示订单号和金额
           // 返回结果单位为分   转换为元   保留两位小数
           $scope.money = (response.total_fee / 100).toFixed(2);
           // 订单号
           $scope.out_trade_no = response.out_trade_no;
           // 生成二维码
           var qr = new QRious({
              element:document.getElementById('qrious'),
               size:250,
               value:response.code_url,
               level:'H'
           });
           // 调用查询支付状态
           queryPayStatus();
       })
   };

   // 查询支付状态   后端 循环查看状态   直到支付成功或者  失败 才返回结果
   queryPayStatus = function () {
       payService.queryPayStatus($scope.out_trade_no).success(function (response) {
           if (response.success){
               location.href = "paysuccess.html#?money=" + $scope.money;
           } else {
               if (response.message == '二维码超时') {
                   // 从新生成二维码
                   $scope.createNative();
               } else {
                   location.href = "payfail.html";
               }
           }
       });
   };

   $scope.getMoney = function () {
       return $location.search()['money'];
   }

});