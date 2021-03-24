#!/bin/bash
mvn clean package
cp ./target/wordcount-1.0.0.jar ./upload/wordcount/wordcount-1.0.0.jar
cd upload/
zip -r -X wordcount.zip wordcount/
aws s3 cp wordcount.zip s3://west-bucket-csye6225/wordcount.zip --acl public-read