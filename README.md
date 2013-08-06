spray-file-upload
=================

This is an example of file upload using Spray 1.2-M8, Akka 2.10.2. To start, `sbt run`. 

You can then upload multiple files using curl, e.g.,

````
curl -F f1=@example.json -F f2=@json160.gif http://localhost:8080/file
````

You can find the example files in the resource folder. Spray can default maximum content size is 8MB, you can change that in `application.conf`.