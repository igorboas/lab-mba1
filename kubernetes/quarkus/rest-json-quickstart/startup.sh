#/bin/bash
if [ -z "$NATIVE" ]; then
	./application -Dquarkus.http.host=0.0.0.0
else
	java -jar application.jar -Dquarkus.http.host=0.0.0.0
fi

