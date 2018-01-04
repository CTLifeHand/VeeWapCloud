(function (angular) {
    angular.module('veewapManager.directives.auto_focus', [])
        //这里一定是驼峰命名
        .directive('autoFocus', ['$location', function ($location) {
            // Runs during compile
            var path = $location.path(); // /coming_soon/1
            return {
                restrict: 'A', // E = Element, A = Attribute, C = Class, M = Comment
                //这里可以获得标签 
                link: function ($scope, iElm, iAttrs, controller) {
                    $scope.$location = $location;
                    $scope.$watch('$location.path()', function (now) {
                        // 当path发生变化时执行，now是变化后的值
                        var aLink = iElm.children().attr('href');
                        var type = aLink.replace(/#(\/.+?)\/\d+/, '$1'); // /coming_soon
                        if (now.startsWith(type)) {
                            // 访问的是当前链接
                            iElm.parent().children().removeClass('active');
                            iElm.addClass('active');
                        }
                    })
                }
            };
        }]);
})(angular);
