app.controller('searchController',function($scope, $location, searchService){

    // 定义搜索对象的结构, category : 商品分类
    $scope.searchMap={'keywords':'', 'category':'', 'brand':'', 'spec':{}, 'price':'', 'pageNo':1, 'pageSize':40, 'sort':'', 'sortField':''};


    //搜索
    $scope.search=function(){
        // 转换为数字
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);
        searchService.search($scope.searchMap).success(
            function(response){
                $scope.resultMap=response;
                $scope.resultMap.totalPages = response.totalPages;
                // $scope.searchMap.pageNo = 1; // 显示第一页
                buildPageLabel(); // 构建分页
            }
        );
    };

    buildPageLabel = function(){
        // 构建分页标签
        $scope.pageLabel = [];
        var firstPage = 1; //开始页码
        var lastPage = $scope.resultMap.totalPages; // 截止页码
        $scope.firstDot = true; // 前面有点
        $scope.lastDot = true; // 后面有点

        // 如果页码大于5
        if ($scope.resultMap.totalPages > 5){
            if ($scope.searchMap.pageNo <= 3){ // 如果当前页码小于等于3   显示前 5 页
                lastPage = 5;
                $scope.firstDot = false; // 前面没点
            } else if ($scope.searchMap.pageNo >= $scope.resultMap.totalPages - 2){ // 显示后 5 页
                firstPage = $scope.resultMap.totalPages - 4;
                $scope.lastDot = false; // 前面没点
            } else { // 显示以当前页为中心的5页
                firstPage = $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;
            }
        } else {
            $scope.firstDot = false; // 前面无点
            $scope.lastDot = false; // 后面无点
        }
        // 构建页码
        for (var i = firstPage; i <= lastPage; i++){
            $scope.pageLabel.push(i);
        }
    };

    // 添加搜索项, 改变searchMap的值
    $scope.addSearchItem = function (key, value) {
        // 用户点击的是分类或品牌
        if (key == 'category' || key == 'brand' || key == 'price'){
            $scope.searchMap[key] = value;
        } else { // 用户点击的是规格
            $scope.searchMap.spec[key] = value;
        }
        $scope.search(); // 查询
    };

    // 撤销搜索项
    $scope.removeSearchItem = function (key) {
        if (key == 'category' || key == 'brand' || key == 'price'){
            $scope.searchMap[key] = '';
        } else { // 用户点击的是规格
            delete $scope.searchMap.spec[key];
        }
        $scope.search(); // 查询
    };

    // 分页查询
    $scope.queryByPage = function (pageNo) {
        if (pageNo < 1 || pageNo > $scope.resultMap.totalPages){
            return;
        }
        $scope.searchMap.pageNo = pageNo;
        $scope.search();

    };

    //判断当前页是否为第一页
    $scope.isTopPage=function(){
        if($scope.searchMap.pageNo==1){
            return true;
        }else{
            return false;
        }
    };

    //判断当前页是否为最后一页
    $scope.isEndPage=function(){
        if($scope.searchMap.pageNo==$scope.resultMap.totalPages){
            return true;
        }else{
            return false;
        }
    };

    // 排序查询
    $scope.sortSearch=function (sortField, sort) {
        $scope.searchMap.sortField = sortField;
        $scope.searchMap.sort = sort;
        $scope.search();//查询
    };

    // 如果选择的是  品牌 就 把 品牌列表隐藏
    $scope.keywordsIsBrand = function () {
        for (var i = 0; i < $scope.resultMap.brandList.length; i ++){
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text) >= 0) {
                return true;
            }
        }
        return false;
    };

    $scope.loadkeywords= function () {
        $scope.searchMap.keywords = $location.search()['keywords'];
        $scope.search();
    }

});