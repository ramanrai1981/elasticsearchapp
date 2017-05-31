(function() {
    'use strict';
    angular
        .module('elasticgateApp')
        .factory('File', File);

    File.$inject = ['$resource', 'DateUtils'];

    function File ($resource, DateUtils) {
        var resourceUrl =  'elasticmicromysql/' + 'api/files/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.uploadDate = DateUtils.convertDateTimeFromServer(data.uploadDate);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
