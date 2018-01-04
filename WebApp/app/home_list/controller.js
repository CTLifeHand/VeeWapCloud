
(function (angular) {
  'use strict';

  // 创建正在热映模块
  var module = angular.module(
    'veewapManager.home_list', [
      'ngRoute',
      // 'veewapManager.services.http' //注意这个是我们自己创建的 应该字符串类型的
    ]);
  // 配置模块的路由
  module.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/:category/:page', {
      templateUrl: 'home_list/view.html',
      controller: 'HomeListController'
    });
  }]);

  //处理控制器的逻辑
  //注入的时候
  module.controller("HomeListController", [
    "$scope",
    // '$http',
    '$route',
    '$routeParams',
    '$http',
    'AppConfig',
    function ($scope, $route, $routeParams, $http, AppConfig) {
      var _isOnline = Boolean(parseInt($routeParams.isOnline));
      $scope.isOnline = $routeParams.isOnline ? _isOnline : false;
      $scope.title = '家庭列表'
      // $scope.subjects = data.subjects
      //定义每页究竟要显示
      var count = AppConfig.pageSize;
      var page = parseInt($routeParams.page)
      var start = (page - 1) * count
      $scope.subjects = []
      $scope.message = 'loading'
      $scope.loading = true
      $scope.totalPages = 0
      $scope.totalCount = 0
      $scope.currentPage = page

      var type = $routeParams.category.indexOf('home_list') >= 0 ? "getHomeArray" : "search";
      // 暴露一个上一页下一页的行为
      $scope.go = function (page) {
        // 传过来的是第几页我就跳第几页
        // 一定要做一个合法范围校验
        if (page >= 1) {
          $route.updateParams({isOnline: Number($scope.isOnline)});
          if (page <= $scope.totalPages) {
            $route.updateParams({ page: page, isOnline: Number($scope.isOnline) });
          }
        }
      };
      // HttpService是我们写的对象 好似是单利来的
      //注意:$routeParams 的数据来源：1.路由匹配出来的，
      // 2.?参数 而且? 是在/category/page?1 之后取的?后面
      //加载数据(注意这是自执行)
      $scope.loadNewData = function () {
        var param = $scope.isOnline ? { type: type, offset: start, length: count, isOnline: 1 } : { type: type, offset: start, length: count};
        // $scope.go(1); // 这个是不是可以去掉?
        $http({
          method: 'post',
          url: AppConfig.veeWapApiAddress,
          data: $.param(param),
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        }).success(function (data) {
          console.log(data);
          $scope.message = ''
          $scope.subjects = data.VMHomeArray;
          $scope.totalCount = data.totalHomeCount;
          $scope.loading = false
          $scope.totalPages = Math.ceil(data.totalHomeCount / count)
          // //注意 要通知他重新渲染 这个异步请求
          // $scope.$apply();
        }).error(function (error) {
          console.log(error);
        }).then(function (response) {
          console.log("Home_List数据");
          // console.log(response);
        });
      };
      $scope.loadNewData();

    }]);



})(angular);