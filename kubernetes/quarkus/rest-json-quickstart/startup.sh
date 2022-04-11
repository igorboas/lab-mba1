#/bin/bash
if [ -z "$NATIVE" ]; then
	java -jar application.jar -Dquarkus.http.host=0.0.0.0
else
	./application -Dquarkus.http.host=0.0.0.0
fi

