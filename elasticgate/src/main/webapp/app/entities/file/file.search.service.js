(function() {
    'use strict';

    angular
        .module('elasticgateApp')
        .factory('FileSearch', FileSearch);

    FileSearch.$inject = ['$resource'];

    function FileSearch($resource) {
        var resourceUrl =  'elasticmicromysql/' + 'api/_search/files/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
