(function(angular) {
    'use strict';

    // 创建正在热映模块
    var module = angular.module(
        'veewapManager.feedback', [
            'ngRoute',
            // 'veewapManager.services.http' //注意这个是我们自己创建的 应该字符串类型的
        ]);
    // 配置模块的路由
    module.config(['$routeProvider', function($routeProvider) {
        $routeProvider.when('/feedback/:page', {
            templateUrl: 'feedback/view.html',
            controller: 'feedbackController'
        });
    }]);

    //处理控制器的逻辑
    //注入的时候
    module.controller("feedbackController", [
        "$scope",
        '$http',
        '$route',
        '$routeParams',
        // 'HttpService',
        'AppConfig',
        function($scope, $http, $route, $routeParams, AppConfig) {
            $scope.title = '反馈列表'
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

            // 暴露一个上一页下一页的行为
            $scope.go = function(page) {
                // 传过来的是第几页我就跳第几页
                // 一定要做一个合法范围校验
                if (page >= 1 && page <= $scope.totalPages) {
                    $route.updateParams({ page: page });
                }
            };
            // HttpService是我们写的对象 好似是单利来的
            //注意:$routeParams 的数据来源：1.路由匹配出来的，
            // 2.?参数 而且? 是在/category/page?1 之后取的?后面
            //加载数据(注意这是自执行)
            $scope.loadNewData = function() {
                $scope.go(1);
                $http({
                    method: 'post',
                    url: AppConfig.veeWapApiAddress,
                    data: $.param({ type: "getFeedback", offset: start, length: count, timeLimit: 300 }),
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
                }).success(function(data) {
                    console.log(data);
                    $scope.message = ''
                    $scope.subjects = data.VMFeedbackArray;
                    $scope.totalCount = data.totalFeedbackCount;
                    $scope.loading = false
                    $scope.totalPages = Math.ceil(data.totalFeedbackCount / count)
                        // //注意 要通知他重新渲染 这个异步请求
                        // $scope.$apply();
                }).error(function(error) {
                    console.log(error);
                }).then(function(response) {
                    console.log("feedback数据");
                    // console.log(response);
                });
            };
            $scope.loadNewData();
        }
    ]);

})(angular);