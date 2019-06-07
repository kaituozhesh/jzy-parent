app.controller("baseController",function ($scope) {
    //分页控件配置 currentPage:当前页码
    // totalItems:总记录数
    // itemsPerPage : 每页记录数
    // perPageOptions : 分页选项
    // onChange: 当页码变更后自动触发的方法
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 4,
        perPageOptions: [2, 4, 8, 20, 50],
        onChange: function () {

            $scope.reloadList();
        }
    };
    //刷新列表
    $scope.reloadList = function () {
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    };


    // 删除
    $scope.selectIds = [];// 用户勾选的id集合
    // 用户勾选复选框
    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked) {
            $scope.selectIds.push(id); // push向集合添加元素
        } else {
            var index = $scope.selectIds.indexOf(id); // 查找值的位置
            $scope.selectIds.splice(index, 1); // 参数1  移除的位置  参数2  移除的个数
        }
    };

    $scope.jsonToString = function (jsonString, key) {
        var json = JSON.parse(jsonString);
        var value = "";
        for (var i = 0; i < json.length; i++) {
            if (i > 0) {
                value += ",";
            }
            value += json[i][key];
        }
        return value;
    };

    // 在list集合中根据某key的值查询对象
    $scope.searchObjectByKey=function (list, key, keyValue) {
        for (var i = 0; i < list.length; i ++){
            if(list[i][key]==keyValue) { // 选择的选项  规格名已经存在
                return list[i];// 返回该选项对象
            }
        }
        return null; // 选项规格名不存在
    }
});