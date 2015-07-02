/*global angular*/
(function () {
    "use strict";

    var app = angular.module('myApp', ['ng-admin']);

    app.controller('main', function ($scope, $rootScope, $location) {
        $rootScope.$on('$stateChangeSuccess', function () {
            $scope.displayBanner = $location.$$path === '/dashboard';
        });
    });

    app.config(function (NgAdminConfigurationProvider, RestangularProvider) {
        var nga = NgAdminConfigurationProvider;

        // use the custom query parameters function to format the API request correctly
        RestangularProvider.addFullRequestInterceptor(function(element, operation, what, url, headers, params) {
            if (operation == "getList") {
                // custom pagination params
                if (params._page) {
                    params._start = (params._page - 1) * params._perPage;
                    params._end = params._page * params._perPage;
                }
                delete params._page;
                delete params._perPage;
                // custom sort params
                if (params._sortField) {
                    params._sort = params._sortField;
                    delete params._sortField;
                }
                // custom filters
                if (params._filters) {
                    for (var filter in params._filters) {
                        params[filter] = params._filters[filter];
                    }
                    delete params._filters;
                }
            }
            return { params: params };
        });

        var admin = nga.application('json server admin') 
            .baseApiUrl('http://localhost:8080/api/'); 

        var blog = nga.entity('blogs');
        	

        // set the application entities
        admin.addEntity(blog);


        blog.dashboardView() 
            .title('Recent blogs')
            .order(1) 
            .perPage(5) 
            .fields([
            	nga.field('published_at', 'date'),
            	nga.field('name').isDetailLink(true)]); 

        blog.listView()
            .title('All blogs') 
            .description('List of blogs with pagination') 
            .infinitePagination(true) // load pages as the user scrolls
            .fields([
                nga.field('id').label('ID'), 
                nga.field('name'), 
                nga.field('published_at', 'date'), 
                nga.field('views', 'number')
            ])
            .listActions(['show', 'edit', 'delete']);

        blog.creationView()
            .fields([
                nga.field('name') 
                    .attributes({ placeholder: 'the blog title' }) 
                    .validation({ required: true, minlength: 1, maxlength: 100 }), 
                nga.field('body', 'wysiwyg'), 
                nga.field('published_at', 'date') 
            ]);

        blog.editionView()
            .title('Edit blog "{{ entry.values.name }}"') 
            .actions(['list', 'show', 'delete']) 
            .fields([
                blog.creationView().fields(), 
                nga.field('views', 'number')
            ]);

        blog.showView() 
            .fields([
                nga.field('id'),
                blog.editionView().fields()
            ]);

        admin.menu(nga.menu()
            .addChild(nga.menu(blog).title("blogs").icon('<span class="glyphicon glyphicon-file"></span>')) 
            .addChild(nga.menu().title('Other')
                .addChild(nga.menu().title('Stats').icon('').link('/stats'))
            )
        );

        nga.configure(admin);
    });


    // custom page with menu item
    var customPageTemplate = '<div class="row"><div class="col-lg-12">' +
            '<ma-view-actions><ma-back-button></ma-back-button></ma-view-actions>' +
            '<div class="page-header">' +
                '<h1>Stats</h1>' +
                '<p class="lead">A custom page</p>' +
            '</div>' +
        '</div></div>';
    app.config(function ($stateProvider) {
        $stateProvider.state('stats', {
            parent: 'main',
            url: '/stats',
            template: customPageTemplate
        });
    });

}());
