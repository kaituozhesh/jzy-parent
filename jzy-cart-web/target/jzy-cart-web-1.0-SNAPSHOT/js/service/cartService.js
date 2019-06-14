app.service('cartService', function ($http) {

    // 购物车列表
    this.findCartList = function () {
        return $http.get('cart/findCartList.do');
    };

    // 添加商品到购物车
    this.addGoodsToCartList = function (itemId, num) {
        return $http.get('/cart/addGoodsToCartList.do?itemId=' + itemId + "&num=" + num);
    };

    // 求和
    this.sum = function (cartList) {
        var totalValue = {totalNum : 0, totalMoney : 0};

        for (var i = 0; i < cartList.length; i ++){
            var cart = cartList[i];
            for (var j = 0; j < cart.orderItemList.length; j ++){
                var orderItem = cart.orderItemList[j];
                totalValue.totalNum += orderItem.num; // 累加数量
                totalValue.totalMoney += orderItem.totalFee; // 累加金额
            }
        }
        return totalValue;
    };

    // 获取当前登陆账号的收货地址
    this.findAddressList=function () {
        return $http.get("/address/findListByLoginUser.do")
    };

    // 新增收货地址
    this.add=function (address) {
        return $http.post("/address/add.do",address);
    };

    // 提交订单
    this.submitOrder = function (order) {
        return $http.post('/order/add.do',order);
    }
});