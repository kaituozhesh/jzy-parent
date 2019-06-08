 //控制层 
app.controller('goodsController' ,function($scope,$controller ,$location  ,goodsService,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	};
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	};
    $scope.entity={ goodsDesc:{itemImages:[],specificationItems:[]}  };
	//查询实体 
    $scope.findOne=function(){
        var id = $location.search()['id'];
        if (id == null) {
            return ;
        }
        goodsService.findOne(id).success(
            function(response){
                $scope.entity= response;
                // 商品图片
                $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
                // 扩展属性
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                // 规格选择
                $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);
                // 转换SKU列表中的规格对象
                for (var i = 0 ; i < $scope.entity.itemList.length; i++){
                    $scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
                }
            }
        );
    };
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    $scope.status=['未审核','已审核','审核未通过','已关闭'];

    $scope.itemCatList=[]; // 商品分类列表
    $scope.findItemCatList=function () {
        itemCatService.findAll().success(
            function (response) {
                for (var i = 0; i < response.length; i++){
                    $scope.itemCatList[response[i].id]=response[i].name;
                }
            }
        )
    };

    $scope.checkAttributeValue=function (specName,optionName) {
        var items = $scope.entity.goodsDesc.specificationItems;
        var object = $scope.searchObjectByKey(items,'attributeName',specName);
        if (object != null){
            if(object.attributeValue.indexOf(optionName) >= 0){ // 能够查询到规格选项
                return true;
            }
        }
        return false;
    };

    // 更新状态
    $scope.updateStatus=function (status) {
        goodsService.updateStatus($scope.selectIds,status).success(
            function (response) {
                if (response.success){
                    $scope.reloadList();// 刷新页面
                    $scope.selectIds=[];
                }else {
                    alert(response.message);
                }
            }
        );
    }
    
});	
