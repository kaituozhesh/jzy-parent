app.controller('brandController', function ($scope, $http, brandService) {
    $scope.findAll = function () {
        brandService.findAll().success(function (response) {
            $scope.list = response;
        })
    };

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
    // 去后端查询数据
    $scope.findPage=function (page, size) {
        brandService.findPage(page, size).success(function (response) {
            $scope.list = response.rows; // 当前页数据
            $scope.paginationConf.totalItems = response.total; // 更新总记录数
        });
    };

    // 新增
    $scope.save = function () {
        var object = null;
        if ($scope.entity.id != null){
            object = brandService.update($scope.entity);
        } else {
            object = brandService.add($scope.entity);
        }
        object.success(function (response) {
            if (response.success){
                $scope.reloadList();  // 刷新页面
            } else {
                alert(response.message); // 提示添加错误
            }
        })
    };
    // 根据id查询实体
    $scope.findOne = function (id) {
        brandService.findOne(id).success(function (response) {
            $scope.entity = response;
        })
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
    // 删除
    $scope.dele = function () {
        brandService.dele($scope.selectIds).success(function (response) {
            if (response.success){
                $scope.reloadList();
            } else {
                alert(response.message);
            }
        })
    };
    $scope.searchEntity = {};
    // 条件查询
    $scope.search = function (page, size) {
        brandService.search(page, size, $scope.searchEntity).success(function (response) {
            $scope.list = response.rows; // 当前页数据
            $scope.paginationConf.totalItems = response.total; // 更新总记录数
        });
    }
});