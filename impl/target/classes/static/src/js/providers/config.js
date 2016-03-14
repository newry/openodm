(function(angular) {
    'use strict';
    angular.module('CTApp').provider('ctConfig', function() {

        var values = {
            appName: 'Control Terminology',
            tplPath: 'src/templates'
        };

        return { 
            $get: function() {
                return values;
            }, 
            set: function (constants) {
                angular.extend(values, constants);
            }
        };
    
    });
})(angular);
