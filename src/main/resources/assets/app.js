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

        var admin = nga.application('json server admin') // application main title
            .baseApiUrl('http://localhost:8080/'); // main API endpoint

        // define all entities at the top to allow references between them
        var blog = nga.entity('blogs')
        	.baseApiUrl('http://localhost:8080/api/') // The base API endpoint can be customized by entity

        // set the application entities
        admin
            .addEntity(blog);


        blog.dashboardView() // customize the dashboard panel for this entity
            .title('Recent blogs')
            .order(1) // display the blog panel first in the dashboard
            .perPage(5) // limit the panel to the 5 latest blog
            .fields([
            	nga.field('published_at', 'date'),
            	nga.field('name').isDetailLink(true)]); // fields() called with arguments add fields to the view

        blog.listView()
            .title('All blogs') // default title is "[Entity_name] list"
            .description('List of blogs with pagination') // description appears under the title
            .infinitePagination(true) // load pages as the user scrolls
            .fields([
                nga.field('id').label('ID'), // The default displayed name is the camelCase field name. label() overrides id
                nga.field('name'), // the default list field type is "string", and displays as a string
                nga.field('published_at', 'date'), // Date field type allows date formatting
                nga.field('views', 'number')
            ])
            .listActions(['show', 'edit', 'delete']);

        blog.creationView()
            .fields([
                nga.field('name') // the default edit field type is "string", and displays as a text input
                    .attributes({ placeholder: 'the blog title' }) // you can add custom attributes, too
                    .validation({ required: true, minlength: 1, maxlength: 100 }), // add validation rules for fields
                nga.field('body', 'wysiwyg'), // overriding the type allows rich text editing for the body
                nga.field('published_at', 'date') // Date field type translates to a datepicker
            ]);

        blog.editionView()
            .title('Edit blog "{{ entry.values.name }}"') // title() accepts a template string, which has access to the entry
            .actions(['list', 'show', 'delete']) // choose which buttons appear in the top action bar. Show is disabled by default
            .fields([
                blog.creationView().fields(), // fields() without arguments returns the list of fields. That way you can reuse fields from another view to avoid repetition
                nga.field('views', 'number').cssClasses('col-sm-4')
            ]);

        blog.showView() // a showView displays one entry in full page - allows to display more data than in a a list
            .fields([
                nga.field('id'),
                blog.editionView().fields()
            ]);

        admin.menu(nga.menu()
            .addChild(nga.menu(blog).title("blogs").icon('<span class="glyphicon glyphicon-file"></span>')) // customize the entity menu icon
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
