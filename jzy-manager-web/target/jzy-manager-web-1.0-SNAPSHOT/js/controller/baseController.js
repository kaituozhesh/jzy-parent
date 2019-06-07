app.controller('baseController', function ($scope) {
    $scope.paginationConf = {
        currentPage: 1,  // 当前页码
        totalItems: 10,  // 总记录数
        itemsPerPage: 10, // 每页记录数
        perPageOptions: [10, 20, 30, 40, 50], // 每页选项
        onChange: function(){ // 当页码变更后 自动触发的方法
            $scope.reloadList(); // 页面加载的时候  分页插件就会自动加载
        }
    };

    // 刷新列表
    $scope.reloadList =function(){
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage); // 重新加载
    };

    // 用户勾选的ID集合
    $scope.selectIds = [];
    // 用户勾选复选框
    $scope.updateSelection=function ($event, id) {
        // push向集合添加元素
        if($event.target.checked){
            $scope.selectIds.push(id);
        } else {
            // 查找值的位置
            var index = $scope.selectIds.indexOf(id);
            // 参数1  移除的位置, 参数2 : 移除的个数
            $scope.selectIds.splice(index, 1);
        }
    };
    //
    $scope.jsonToString = function (jsonString, key) {
        var json = JSON.parse(jsonString);
        var value = "";
        for (var i = 0; i < json.length; i ++){
            value += ","json[i][key];
        }
    }
});