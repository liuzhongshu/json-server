# json-server
A quick java based back-end REST server for prototyping and mocking inspired by nodejs [json-server](https://github.com/typicode/json-server)

## Build & run
```
mvn package 
java -jar target/json-server-0.0.1-snapshot.jar server
```

then use browser to access http://localhost:8080/blogs

## Routes

Based on the  `db.json` file, here are all the available routes. 

```
GET    /blogs
GET    /blogs/1
GET    /blogs/1/comments
POST   /blogs
PUT    /blogs/1
DELETE /blogs/1
```
