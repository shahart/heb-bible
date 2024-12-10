[![CircleCI](https://dl.circleci.com/status-badge/img/gh/shahart/heb-bible/tree/master.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/shahart/heb-bible/tree/master)

# The hebrew bible #

Antique! 1998 --> Now 2024 ported to java/script, Spring Boot, and LambdaS.


![](read-psalm-119.png)

The **deprecated** Pascal code requires some time to re-understand. Plus the fact it was done on DOS with hebrew characters (Ascii 128 stands for Aleph).

The commit is with

- bible.txt 1.42 MB, now also its gzip (0.7 MB)
- bible.gim 46 KB (for gematria)
- errata.inf (קרי וכתיב)
- bible.txt.gz Real compression, vs Pascal's

A nice fact: as we need only 27 letters plus period and space (maybe more, I can't remember), 5 bits are enough, so silly compression done to put it on old 1.44 floppy.

--


חבילה זו מכילה מספר עזרים שונים לתנ"ך (קריאה, הפטרות, קרי/כתיב, חיפוש, פרשות, גימטריה, פסוק לפי שם, דילוגים). רובם פשוטים ביותר לשימוש
ועל-כן התיעוד הנ"ל די קצר.

בקליטת מחרוזת הקש Esc למחיקת כל הביטוי, ו-BackSpace למחיקת
התו הקודם.

Find - 1. אם המחרוזת לא נמצאה יש לנסות לחפש חלקים יותר קטנים
ממנה 2. לחיפוש מלה יחידה יש להקיש רווח לפניה ואחריה. לחיפוש
מלה בסוף או בהתחלה הקש שני רווחים.

Read - כללים ומקשים:
 
Esc ³ יציאה

End/Home ³ קפיצה להתחלת/סוף הספר

Down/Up Arrow ³ גלילת השטח הנצפה מעלה או מטה

PgDn/PgUp ³ גלילת השטח הנצפה 22 שורות

I,Info ³ הצגת חלון מידע על הספר

N,Next ³ חיפוש נוסף של המחרוזת המבוקשת

P,Prev ³ חיפוש אחורה של המחרוזת המבוקשת

7-1 >Shift< ³ קפיצה לסימניה x Shift לסימון סימניה.

9 ³ קפיצה לתוצאת החיפוש האחרון

8/0 ³ קפיצה לסוף/תחילת הפרשה או המפטיר בהתאמה

F,Find ³ חיפוש מחרוזת מבוקשת. לחיפוש מלה הקש רווח

³ לפניה ולאחריה. לקפיצה לפרק מסוים הקש את

³ מספרו ורווח אחרי המספר. לקפיצה לפסוק מסוים

³ הקש את מספרו בלבד. הקש 3 רווחים לקפיצה לפרק

³ הבא.

### Azure Function App

myPsukimViaFunctionApp

### Google Cloud Platform Cloud Function

https://us-central1-pivotal-racer-435706-c6.cloudfunctions.net/myPsukimViaLambda

CLI: gcloud functions list

`curl http://localhost:8081?name=שחר`

### AWS Lambda

Api GW /v1/pasuk with POST method.
Allow _CORS_

- Java

`mvn clean package shade:shade`

At https://eu-north-1.console.aws.amazon.com/lambda/home?region=eu-north-1#/functions/myPsukimViaLambda?tab=configure
Allow _CORS_, put `access-control-allow-origin` at both Expose/ Allowed headers

handler name is `edu.hebbible.Handler::handleRequest`

- JavaScript

`node Pasuk.mjs שחר` >> TODO lambda/index.mjs

### GoLang

TODO Pasuk.go

go build Pasuk.go >> ./Pasuk.exe שחר

go run Pasuk.go שחר

### EC2

Bare minimum Spring Boot 3 App.

Init'ed with Spring Initializ: https://start.spring.io/

- `docker build -t hebbible-app .`
- `docker run -p 8080:8080 hebbible-app`

- `curl -H "Content-Type: application/json" -d 'שחר' http://localhost:8080/[hebBible/]psukim`

$ java -jar hebbible-0.0.1-SNAPSHOT.war

`mvn --file sb3/pom.xml spring-boot:run`

http://localhost:8080/ >> Swagger/OpenApi

#### https://ollama.com/library/tinyllama

- `docker pull ollama/ollama`
- `docker run -d -v ollama:/root/.ollama -p 11434:11434 --name ollama ollama/ollama`
- `docker exec -it ollama ollama run tinyllama`
- `curl -v --location 'http://localhost:11434/api/generate' --header 'Content-Type: application/json' --data '{"model": "tinyllama","prompt": "why is the sky blue?", "stream": false}'`
- `http://localhost:8080/v1/chat/list-models`

#### Getting Started with Create React App

- `node grab-git-info.js`

- `chrome.exe --user-data-dir="C://Chrome dev session" --disable-web-security`


#### Dynamo DB

- `aws dynamodb create-table --table-name psukim`

- `docker pull amazon/dynamodb-local`


### IBM discontinued https://cloud.ibm.com/functions/

### Tencent

### Heroku https://devcenter.heroku.com/articles/getting-started-with-java

### Alibaba Cloud Function Engine

Log on to the Function Compute console https://fcnext.console.aliyun.com/ and upload the code package.

Handler name in the UI = edu.hebbible.Handler::handleRequest

### Oracle Cloud Fn

Via https://fnproject.io/

- `fn start [--log-level DEBUG]`
- `fn init --runtime java psukim`
- `fn create app java-app`


- `fn --verbose deploy --app java-app --local`


- `fn invoke java-app psukim`


- `fn inspect function java-app psukim`
- `curl -X "POST" -H "Content-Type: application/json" http://localhost:8080/invoke/01J8YZ0S20NG8G00GZJ0000002` --data '{"name":"ajr="}'

### Spring Cloud Function (AWS/ Gcp)

`curl localhost:8080/ -H "Content-Type: text/plain" -d "{name}"`

like ajr=

in the aws console, the handler name is `org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest`

### Newman

A runner for Postman

`npm install -g newman`

`newman run src/test/resources/hebbible.postman_collection.json -g src/test/resources/postman_globals.json`

### More...

- https://projectriff.io
- Apache's Open Stack/ Whisk, ..
