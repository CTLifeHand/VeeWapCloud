
(function (angular) {
  'use strict';

  // 创建正在热映模块
  var module = angular.module(
    'veewapManager.profile', [
      'ngRoute',
      // 'veewapManager.services.http' //注意这个是我们自己创建的 应该字符串类型的
    ]);
  // 配置模块的路由
  module.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/profile', {
      templateUrl: 'profile/view.html',
      controller: 'ProfileController'
    });
  }]);

  //处理控制器的逻辑
  //注入的时候
  module.controller("ProfileController", [
    "$scope",
    // '$http',
    '$route',
    '$routeParams',
    '$http',
    'AppConfig',
    function ($scope, $route, $routeParams, $http, AppConfig) {
      $scope.title = '正在开发...'
    }])



})(angular);