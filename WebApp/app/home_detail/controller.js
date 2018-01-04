
(function (angular) {
  'use strict';

  // 创建正在热映模块
  var module = angular.module(
    'veewapManager.home_detail', [
      'ngRoute',
      'ui.bootstrap',
    ]);
  // 配置模块的路由
  module.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/detail/:id', {
      templateUrl: 'home_detail/view.html',
      controller: 'HomeDetailController'
    });
  }]);

  module.controller('ModalInstanceCtrl', function ($uibModalInstance, item) {
    var $ctrl = this;
    $ctrl.message = item.message;
    $ctrl.ok = function () {
      $uibModalInstance.close(item.callback);
    };

    $ctrl.cancel = function () {
      $uibModalInstance.dismiss('cancel');
    };
  });

  module.controller("HomeDetailController", [
    "$scope",
    "$uibModal",
    '$route',
    '$routeParams',
    '$http',
    'AppConfig',
    function ($scope, $uibModal, $route, $routeParams, $http, AppConfig) {
      $scope.title = '家庭列表'
      $scope.loading = true
      var id = parseInt($routeParams.id)

      //请求数据
      $http({
        method: 'post',
        url: AppConfig.veeWapApiAddress,
        data: $.param({ type: "getHomeMessage", VMHomeId: id }),
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
      }).success(function (data) {
        console.log(data);
        // header 全部搬过来
        $scope.item = data.VMHomeMessage
        if (data.VMHomeMessage.VMHomeMessage && data.VMHomeMessage.VMHomeMessage.VMUserArray) {
          $scope.users = data.VMHomeMessage.VMHomeMessage.VMUserArray
        }
        if (data.VMHomeMessage.VMHomeMessage && data.VMHomeMessage.VMHomeMessage.VMPanelArray) {
          $scope.panels = data.VMHomeMessage.VMHomeMessage.VMPanelArray
        }
        // $scope.panels.push({"id": 1, "name": "?asdkfjhk", "roomId": 1, "roomName": "门厅", "VMType": "??", "isOnline": 0})


        // $scope.$apply();
      }).error(function (error) {
        console.log(error);
      }).then(function (response) {
        $scope.loading = false;
        console.log("Detail数据");
        // console.log(response);
      });


      // 事件 
      // private
      function _setServerPanel(ServerPanelId) {
        $http({
          method: 'post',
          url: AppConfig.veeWapApiAddress,
          data: $.param({ type: "setServerPanel", VMHomeId: id, ServerPanelId: ServerPanelId }),
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        }).success(function (data) {
          console.log(data);

          // $scope.$apply();
        }).error(function (error) {
          console.log(error);
        }).then(function (response) {
          console.log("我是得到返回数据之后才会执行的操作");
          console.log(response);
        });
      }


      function _setHomeManager(UserId) {
        $http({
          method: 'post',
          url: AppConfig.veeWapApiAddress,
          data: $.param({ type: "setHomeManager", VMHomeId: id, UserId: UserId }),
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        }).success(function (data) {
          console.log(data);
          // $scope.$apply();
        }).error(function (error) {
          console.log(error);
        }).then(function (response) {
          console.log("Home_Detail数据");
          // console.log(response);
        });
      }

      function _modalServerIDAndMessage(ServerPanelId,Message) {
        var modalInstance = $uibModal.open({
          ariaLabelledBy: 'modal-title',
          ariaDescribedBy: 'modal-body',
          templateUrl: 'myModalContent.html',
          controller: 'ModalInstanceCtrl',
          controllerAs: '$modalController',
          resolve: {
            item: function () {
              return {
                message: Message,
                callback: function () {
                  _setServerPanel(ServerPanelId);
                }
              };
            }
          }
        });
        modalInstance.result.then(function (callback) {
          callback();
        }, function () {
          console.log('Modal dismissed at: ' + new Date());
        });
      }

      function _modalUserIdAndMessage(UserId,Message) {
        var modalInstance = $uibModal.open({
          ariaLabelledBy: 'modal-title',
          ariaDescribedBy: 'modal-body',
          templateUrl: 'myModalContent.html',
          controller: 'ModalInstanceCtrl',
          controllerAs: '$modalController',
          resolve: {
            item: function () {
              return {
                message: Message,
                callback: function () {
                  _setHomeManager(UserId);
                }
              };
            }
          }
        });
        modalInstance.result.then(function (callback) {
          callback();
        }, function () {
          console.log('Modal dismissed at: ' + new Date());
        });
      }

      $scope.panelSubtitleClickEvent = function ($event, ServerPanelId) {
        console.log($event)
        if ($event.stopPropagation) {
          $event.stopPropagation();
        }
        _modalServerIDAndMessage(ServerPanelId,'真的切换主面板吗?');
      }
      $scope.panelHeaderCenterClickEvent = function ($event, ServerPanelId) {
        if ($event.button != 1) return;
        _modalServerIDAndMessage(ServerPanelId,'真的切换主面板吗?');
      }

      $scope.userSubtitleClickEvent = function ($event, UserId) {
        console.log($event)
        if ($event.stopPropagation) {
          $event.stopPropagation();
        }
        _modalUserIdAndMessage(UserId,'真的设置家庭管理员吗?');
      }
      $scope.userHeaderCenterClickEvent = function ($event, UserId) {
        if ($event.button != 1) return;
        _modalUserIdAndMessage(UserId,'真的设置家庭管理员吗?');
      }

    }])
})(angular);