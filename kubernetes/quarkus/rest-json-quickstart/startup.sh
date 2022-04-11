#/bin/bash
if [ $NATIVE -eq 1 ]; then
	./application -Dquarkus.http.host=0.0.0.0
else
	java -jar rest-json-quickstart-1.0.0-SNAPSHOT.jar -Dquarkus.http.host=0.0.0.0
fi

