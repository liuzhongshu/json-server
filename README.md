# json-server
A quick java based back-end REST server for prototyping and mocking

## Build & run
```
mvn compile exec:java
```

then use browser to access http://localhost:8080/api/blogs

## Model mocking

Based on the  `db.json` file:
 
```
"comments":[
   {"id":1,"name":"comment1"},
   {"id":2,"name":"comment2"}
  ],
  "blogs":[
   {"id":1,"name":"blog1","body":"<p>blog1 content<br></p>","published_at":"2015-06-01","views":7},
   {"id":2,"name":"blog2","body":"<p>blog2 content..<br></p>","views":3}
  ]
```  

these are two models: comments and blogs, the code will generate all routes(root path is /api defined in server.yml) for these models: 

```
GET    /api/blogs
GET    /api/blogs/1
GET    /api/blogs/1/comments
POST   /api/blogs
PUT    /api/blogs/1
DELETE /api/blogs/1
```

## Api mocking

In db.json file, every mock api is just a json object, you can write request and response. for example:

```
"auth/login@POST":[
    {
      "request":{"username":"aaa"},
      "response":{"token":"aaaaa"}
    },
    {
      "request":{"username":"bbb"},
      "response":{"!exception":"user not found"}
    }
  ],
  "db/save@POST":[{"response":{}}]  
```

here, we define two api, one is POST request to /api/auth/login, another is POST request to /api/db/save, if the request contain the username 'aaa', the response will be a token, and the response will be a 500 error if username is 'bbb'. 

## hot mocking
if your modify the model mocking or api mocking, no need to restart the server, just refresh browser, it's amazing simple.
 
## client
if your want use your client code, just put the code in webapp, and refresh browser.

## ng-admin client
if your just want a CRUD client based on angular ng-admin, run bower install, then access http://localhost:8080/ 

