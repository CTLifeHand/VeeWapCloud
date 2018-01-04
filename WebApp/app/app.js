'use strict';

var button = $('#react-navList');

// Declare app level module which depends on views, and components
// 第一个参数表示模块的名称，第二个参数表示此模块依赖的其他模块。
angular.module('veewapManager', [
  'ngRoute',
  'veewapManager.feedback',
  'veewapManager.home_detail',
  'veewapManager.profile',
  'veewapManager.version_list',
  'veewapManager.home_list',//通用的要在后面
  'ui.bootstrap',
])// 为模块定义一些常量
  .constant('AppConfig', {
    pageSize: 20,
    // veeWapApiAddress: 'http://test.veewap.com/VWHomeManagerServlet'
    // veeWapApiAddress: 'http://soft.veewap.com/VWHomeManagerServlet'
    veeWapApiAddress: '/VWHomeManagerServlet'
  })
  .config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
    //home_list //feedback
    $routeProvider.otherwise({ redirectTo: '/home_list/1' });
    // 暂时还不清楚什么原因
    // $locationProvider.html5Mode(true);//启用html5模式
  }])
  .controller('NavController', [
    '$scope',
    '$route',
    'AppConfig',
    function ($scope, $route, AppConfig) {
      if (window.location.href.indexOf('home') >= 0 || window.location.href.indexOf('detail') >= 0) {
        $scope.selected = '家庭列表';
      } else if (window.location.href.indexOf('feedback') >= 0) {
        $scope.selected = '反馈信息';
      } else if (window.location.href.indexOf('profile') >= 0) {
        $scope.selected = '个人中心';
      } else if (window.location.href.indexOf('version') >=0) {
        $scope.selected = '版本信息';
      } else {
        $scope.selected = '家庭列表';
      }

      // '家庭列表' "反馈信息" "个人中心" '版本信息'
      $scope.navbarClick = function ($event) {
        button.collapse('hide');
        // console.log($event.target.text)
        // console.log($scope.selected === '家庭列表')
        $scope.selected = $event.target.text
      };
    }
  ])
  .controller('TopController', [
    '$scope',
    '$route',
    'AppConfig',
    function ($scope, $route, AppConfig) {
      $scope.backToSignin = function ($event) {
        $scope.selected = $event.target.text;
      };
      $scope.feedback = function ($event) {
        alert('正在开发...');
      };
    }
  ])
  .controller('SearchController', [
    '$scope',
    '$route',
    'AppConfig',
    function ($scope, $route,AppConfig) {
      $scope.input = ''; // 取文本框中的输入
      $scope.search = function () {
        button.collapse('hide');
        window.location.href="#/search/1";
        $route.updateParams({ category: 'search', page: '1', q: $scope.input });
      };
    }
  ]);
