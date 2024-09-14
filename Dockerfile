FROM gradle:latest
VOLUME /cucumber
WORKDIR /cucumber
COPY . .
CMD ./gradlew test