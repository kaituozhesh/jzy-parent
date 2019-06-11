 //控制层 
app.controller('userController' ,function($scope,$controller   ,userService){	
	
    $scope.reg = function () {
        if ($scope.password != $scope.entity.password){
            alert("两次密码不一致, 请重新输入");
            $scope.entity.password = "";
            $scope.password = "";
            return ;
        }
        userService.add($scope.entity).success(function (response) {
            alert(response.message);
        });
    };
    
});	
