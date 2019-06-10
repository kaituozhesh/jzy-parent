// 定义模块
var app = angular.module('jzy', []);
// 定义过滤器
app.filter('trustHtml', ['$sce',function ($sce) {
    // 传入参数是被过滤的内容
    return function (data) {
        // 返回过滤后的内容(信任html的转换)
        return $sce.trustAsHtml(data);
    }
}]);