
(function (angular) {
  'use strict';

  // 创建正在热映模块
  var module = angular.module(
    'veewapManager.version_list', [
      'ngRoute',
      'ui.bootstrap',
      // 'veewapManager.services.http' //注意这个是我们自己创建的 应该字符串类型的
    ]);
  // 配置模块的路由
  module.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/version/:page', {
      templateUrl: 'version_list/view.html',
      controller: 'VersionListController'
    });
  }]);

  module.controller('ModalInstanceCtrl', [
    '$http',
    '$uibModalInstance',
    'item',
    'AppConfig',
    function ($http, $uibModalInstance, item, AppConfig) {
      var $ctrl = this;
      $ctrl.message = item.message;
      $ctrl.devicetypeText = "操作系统";
      $ctrl.modelText = "世代";

      $ctrl.devicetypeClick = function (devicetype, text) {
        $ctrl.devicetype = devicetype;
        $ctrl.devicetypeText = text;

        //还需要发请求
        $http({
          method: 'post',
          url: AppConfig.veeWapApiAddress,
          data: $.param({ 'type':'getLastVersion', 'devicetype': devicetype}),
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        }).success(function (data) {
          if (data.message.devicetype == 0) {
            data.message.devicetypeText = "Android"
          } else if (data.message.devicetype == 1) {
            data.message.devicetypeText = "iOS"
          } else if (data.message.devicetype == 2) {
            data.message.devicetypeText = "智能开关"
          }
          $ctrl.lastVersion = data.message;
          console.log($ctrl.lastVersion);
          // $scope.$apply(); // 报错了 暂时不需要
        }).error(function (error) {
          console.log(error);
        }).then(function (response) {
          console.log("getLastVersion");
        });
      };

      $ctrl.modelClick = function (model, text) {
        $ctrl.model = model;
        $ctrl.modelText = text;
      };



      $ctrl.ok = function () {
        // $uibModalInstance.close(item.callback);
        //还需要发请求
        var param = { 'type': 'addLastVersion' };

        if ($ctrl.devicetype >= 0) {
          param.devicetype = $ctrl.devicetype;
        }else{
          alert('请选择操作系统');
          return;
        }

        param.model = $ctrl.model ? $ctrl.model : null;
        param.name = $ctrl.name ? $ctrl.name : null;
        param.path = $ctrl.path ? $ctrl.path : null;
        param.version = $ctrl.version ? $ctrl.version : null;
        // param.username = $ctrl.username ? $ctrl.username : null;
        // param.updatetime = new Date().getTime();
        // param.updatetime = "new Date().getTime()";
        console.log(param);
        $http({
          method: 'post',
          url: AppConfig.veeWapApiAddress,
          data: $.param(param),
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        }).success(function (data) {
          console.log(data);
          if (data.success) {
            alert('提交成功');
            $uibModalInstance.close();
          }else{
            if (data.error && data.error == 100) {
              alert('添加失败:软件版本值重复');
            } else if (data.error == 101) {
              alert('添加失败:软件版本号重复' + data);
            } else if (data.error == 102) {
              alert('添加失败:字段为空');
            } else{
              alert('未知错误' + data);
              console.error(data);
            }
          }
          // $scope.$apply(); // 报错了 暂时不需要
        }).error(function (error) {
          console.log(error);
          alert(error);
          $uibModalInstance.close();
        }).then(function (response) {
          console.log("addLastVersion");
        });
      };
      $ctrl.cancel = function () {
        $uibModalInstance.dismiss('cancel');
      };
  }]);

  //处理控制器的逻辑
  //注入的时候
  module.controller("VersionListController", [
    "$scope",
    "$uibModal",
    '$route',
    '$routeParams',
    '$http',
    'AppConfig',
    function ($scope, $uibModal, $route, $routeParams, $http, AppConfig) {
      $scope.title = '版本信息'
      var count = AppConfig.pageSize;
      var page = parseInt($routeParams.page)
      var start = (page - 1) * count
      $scope.subjects = []
      $scope.message = 'loading'
      $scope.loading = true
      $scope.totalPages = 0
      $scope.totalCount = 0
      $scope.currentPage = page
      $scope.devicetypeText = "操作系统"
      $scope.modelText = "世代"
      // $scope.devicetype = -1
      // $scope.model = '';

      // var type = $routeParams.category.indexOf('home_list') >= 0 ? "getHomeArray" : "search";
      // 暴露一个上一页下一页的行为
      $scope.go = function (page) {
        // 传过来的是第几页我就跳第几页
        // 一定要做一个合法范围校验
        if (page >= 1 && page <= $scope.totalPages) {
          $route.updateParams({ page: page});
        }
      };
      // HttpService是我们写的对象 好似是单利来的
      //注意:$routeParams 的数据来源：1.路由匹配出来的，
      // 2.?参数 而且? 是在/category/page?1 之后取的?后面
      //加载数据(注意这是自执行)
      $scope.loadNewData = function () {
        var param = { type: "getVersionArray", offset: start, length: count };
      
        if ($routeParams.devicetype >= 0) {
          param.devicetype = $routeParams.devicetype;
          if ($routeParams.devicetype == 0) {
            $scope.devicetypeText = "Android"
          } else if ($routeParams.devicetype == 1) {
            $scope.devicetypeText = "iOS"
          } else if ($routeParams.devicetype == 2) {
            $scope.devicetypeText = "智能开关"
          } else {
            $scope.devicetypeText = "操作系统"
          }
        }

        if ($routeParams.model&&$routeParams.model.length > 0) {
          param.model = $routeParams.model;
          if ($routeParams.model === 'KG30') {
            $scope.modelText = "第三代"
          } else if ($routeParams.model == 'KG40') {
            $scope.modelText = "第四代"
          } else if ($routeParams.model == 'KG50') {
            $scope.modelText = "第五代"
          } else {
            $scope.modelText = "世代"

          }
        }
        console.log(param)
        
        $http({
          method: 'post',
          url: AppConfig.veeWapApiAddress,
          data: $.param(param),
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        }).success(function (data) {
          console.log(data);
          $scope.message = ''
          $scope.subjects = data.versionArray;
          $scope.totalCount = data.totalCount;
          $scope.totalPages = Math.ceil($scope.totalCount / count)
          // //注意 要通知他重新渲染 这个异步请求
          $scope.loading = false
          // $scope.$apply(); // 报错了 暂时不需要
        }).error(function (error) {
          console.log(error);
        }).then(function (response) {
          console.log("Version_List");
          // console.log(response);
        });
      };
      $scope.loadNewData();

      // 第几代
      $scope.modelClick = function (model,text) {
        // 传过来的是第几页我就跳第几页
        // 一定要做一个合法范围校验
        $scope.model = model;
        $scope.modelText = text;
        $route.updateParams({ page: 1, model: model, devicetype: 2});
      };

      // iOS
      $scope.devicetypeClick = function (devicetype, text) {
        // 传过来的是第几页我就跳第几页
        // 一定要做一个合法范围校验
        $scope.devicetype = devicetype;
        $scope.devicetypeText = text;
        if (devicetype == 2) {
          $route.updateParams({ page: 1, devicetype: devicetype, model:$routeParams.model });
        } else {
          $route.updateParams({ page: 1, devicetype: devicetype, model:null });
        }
      };


      $("#devicetypeButton").on("hide.bs.dropdown", function (a) {
        console.log("devicetypeButton Hiding dropdown..");
      });


      $("#modelButton").on("hide.bs.dropdown", function () {
        console.log("modelButton Hiding dropdown..");
      });

      $scope.addVersion = function ($event) {
        _modalFrom('添加版本信息');
      }

      function _modalFrom(message) {// message用于练习传值
        var modalInstance = $uibModal.open({
          ariaLabelledBy: 'modal-title',
          ariaDescribedBy: 'modal-body',
          templateUrl: 'myModalContent.html',
          controller: 'ModalInstanceCtrl',
          controllerAs: '$modalController',
          resolve: {
            item: function () {
              return {
                message: message,
                callback: function () {
                  //OK的逻辑


                }
              };
            }
          }
        });
        modalInstance.result.then(function (callback) {
          // callback();
        }, function () {
          console.log('Modal dismissed at: ' + new Date());
        });
      }


    }]);
})(angular);

