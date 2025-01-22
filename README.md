"# drumre-lab1" 

## Building an image (Java 21 version expected on local machine)
In application.properties add/modifiy the line so that it has this correct uri
spring.data.mongodb.uri=mongodb://mongodb:27017/Lab1Db

./mvnw package
docker compose build

## Running an image
docker compose up -d

## Project Overview
Login Page
![login](https://github.com/MarioHosnjak/drumre-lab1/blob/main/docs/img/login.PNG)
Profile Page
![profile](https://github.com/MarioHosnjak/drumre-lab1/blob/main/docs/img/profile.PNG)
Community
![community](https://github.com/MarioHosnjak/drumre-lab1/blob/main/docs/img/community.PNG)
Browse Movies
![browse](https://github.com/MarioHosnjak/drumre-lab1/blob/main/docs/img/browse.PNG)
Movie Feed
![feed1](https://github.com/MarioHosnjak/drumre-lab1/blob/main/docs/img/feed1.PNG)
Movie Feed (scrolled down)
![feed2](https://github.com/MarioHosnjak/drumre-lab1/blob/main/docs/img/feed2.PNG)
