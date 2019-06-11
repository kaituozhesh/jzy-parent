 //控制层 
app.controller('userController' ,function($scope,$controller   ,userService){	
	
    $scope.reg = function () {
        if ($scope.password != $scope.entity.password){
            alert("两次密码不一致, 请重新输入");
            $scope.entity.password = "";
            $scope.password = "";
            return ;
        }
        userService.add($scope.entity, $scope.smscode).success(function (response) {
            alert(response.message);
        });
    };

    // 发送验证码
    $scope.sendCode = function () {
        if ($scope.entity.phone == null || $scope.entity.phone == ""){
            alert("请输入正确的手机号");
            return;
        }
        userService.sendCode($scope.entity.phone).success(function (response) {
            alert(response.message);
        });
    };
    
});	
