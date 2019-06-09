app.controller('searchController',function ($scope, searchService) {

    // 搜索
    $scope.search = function () {
        alert("xx");
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap = response;
        });
    };
});