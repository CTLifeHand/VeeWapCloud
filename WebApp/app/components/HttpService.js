
'use strict';
// 这个方法就是跨域服务的方法 
// 这里没有包装Angular的库 只是利用Angular去实现了一个单利
// apply函数 就是手动的时候
(function(angular) {
  // 由于默认angular提供的异步请求对象不支持自定义回调函数名
  // angular随机分配的回调函数名称不被豆瓣支持(只支持callback=xxx)
  var http = angular.module('veewapManager.services.http', []);
  //service 函数 就是创建了一个服务的对象
  http.service('HttpService', ['$window', '$document', function($window, $document) {
    // url : http://api.douban.com/vsdfsdf -> <script> -> html就可自动执行
    this.jsonp = function(url, data, callback) {
      var fnSuffix = Math.random().toString().replace('.', '');
      var cbFuncName = 'TonyChan' + fnSuffix;
      var querystring = url.indexOf('?') == -1 ? '?' : '&';
      for (var key in data) {
        querystring += key + '=' + data[key] + '&';
      }
      querystring += 'callback=' + cbFuncName;
      //$document的第一个对象就是原来的document
      var scriptElement = $document[0].createElement('script');
      scriptElement.src = url + querystring;

      //$window和window是一样的 最好回调完成之后删除掉
      $window[cbFuncName] = function(data) {
        callback(data);
        $document[0].body.removeChild(scriptElement);
      };
      $document[0].body.appendChild(scriptElement);
    };
  }]);
})(angular);
