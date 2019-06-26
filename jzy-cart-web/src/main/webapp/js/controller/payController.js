app.controller('payController' ,function($scope ,$location,payService){


    $scope.createNative=function(){
        payService.createNative().success(
            function(response){
                //显示订单号和金额
                $scope.money= (response.total_fee/100).toFixed(2);
                $scope.out_trade_no=response.out_trade_no;
                //生成二维码
                var qr=new QRious({
                    element:document.getElementById('qrious'),
                    size:250,
                    value:response.code_rul,
                    level:'H'
                });

                queryPayStatus();//调用查询

            }
        );
    }

    //调用查询
    queryPayStatus=function(){
        payService.queryPayStatus($scope.out_trade_no).success(
            function(response){
                alert(response.success);
                if(response.success){
                    location.href="/paysuccess.html#?money=" + $scope.money;
                }else{
                    // 如果用户关闭了页面  js也调用不到了  也不会刷新二维码了   如果是 有事比如接个电话   二维码超时  就刷新
                    if(response.message=='二维码超时'){
                        $scope.createNative();//重新生成二维码
                    }else{
                        location.href="payfail.html";
                    }
                }
            }
        );
    }

    //获取金额
    $scope.getMoney=function(){
        return $location.search()['money'];
    }

});