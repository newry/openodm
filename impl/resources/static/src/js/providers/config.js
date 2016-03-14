(function(angular) {
    'use strict';
    angular.module('FileManagerApp').provider('fileManagerConfig', function() {

        var values = {
            appName: 'angular-filemanager',
            defaultLang: 'en',
            enablePermissions: false,

            loginUrl: 'login',
            listUrl: 'folder/v1/listFolder',
            uploadUrl: 'download',
            renameUrl: 'folder/v1/rename',
            copyUrl: 'bridges/jsp/handler.jsp',
            removeUrl: 'bridges/jsp/handler.jsp',
            editUrl: 'bridges/jsp/handler.jsp',
            getContentUrl: 'bridges/jsp/handler.jsp',
            getHistoryUrl: 'bridges/jsp/handler.jsp',
            createFolderUrl: 'bridges/jsp/handler.jsp',
            downloadFileUrl: 'download',
            compressUrl: 'bridges/jsp/handler.jsp',
            extractUrl: 'bridges/jsp/handler.jsp',
            permissionsUrl: 'bridges/jsp/handler.jsp',
            checkoutUrl: 'bridges/jsp/handler.jsp',
            checkinUrl: 'download',

            sidebar: true,
            breadcrumb: true,
            allowedActions: {
                upload: true,
                rename: true,
                copy: true,
                edit: false,
                changePermissions: false,
                compress: false,
                compressChooseName: false,
                extract: false,
                download: true,
                preview: true,
                remove: false,
                checkin: true,
                checkout: true,
				showHistory: true
            },

            enablePermissionsRecursive: true,
            compressAsync: true,
            extractAsync: true,

            isEditableFilePattern: /\.(txt|html?|aspx?|ini|pl|py|md|css|js|log|htaccess|htpasswd|json|sql|xml|xslt?|sh|rb|as|bat|cmd|coffee|jsp[3-6]?|java|c|cbl|go|h|scala|vb|properties)$/i,
            isImageFilePattern: /\.(jpe?g|gif|bmp|png|svg|tiff?)$/i,
            isExtractableFilePattern: /\.(gz|tar|rar|g?zip)$/i,
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
