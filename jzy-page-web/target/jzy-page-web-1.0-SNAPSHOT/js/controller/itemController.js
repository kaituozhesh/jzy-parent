app.controller('itemController',function($scope){

	$scope.specificationItems={};
	// 数量加减
	$scope.addNum = function(x){
		$scope.num += x;
		if($scope.num < 1){
			$scope.num = 1;
		}
	};
	
	// 用户选择规格
	$scope.selectSpecification = function(key, value){
		$scope.specificationItems[key]=value;
		// 查询sku
		searchSku();
	};

	// 判断某规格是否呗选择
	$scope.isSelected = function(key, value){
		if($scope.specificationItems[key] == value){
			return true;
		}
		return false;
	};
	
	$scope.sku={}; // 当前现在的sku
	
	$scope.loadSku = function(){
		$scope.sku = skuList[0];
		$scope.specificationItems=JSON.parse(JSON.stringify($scope.sku.spec));
	};
	// 匹配两个对象是否相等
	matchObject = function(map1, map2){
		for(var k in map1){
			if(map1[k] != map2[k]){
				return false;
			}
		}
		for(var k in map2){
			if(map2[k] != map1[k]){
				return false;
			}
		}
		return true;
	};
	
	// 根据规格查询sku
	searchSku = function(){
		for(var i = 0; i < skuList.length; i++){
			if(matchObject(skuList[i].spec, $scope.specificationItems)){
				$scope.sku = skuList[i];
				return;
			}
		}
		$scope.sku={id:0,title:'--------------'}
	};
	
	$scope.addToCart = function(){
		alert("SKU ID" + $scope.sku.id);
	}
	
});


















