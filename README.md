# recording-importer

The recording importer is a backend component that import files of recorded audio/video calls into a processing system.

## Build
The component can be built from the commandline through the following command.
```
mvn clean install
```

## Run
The component can be started through the following command. By default it runs on port 8080.
Before starting make sure the environment variable **processor.endpoint** is pointing to the correct instance of the processor service.
```
mvn spring-boot:run   
```

## Example cURL request

```
curl --location --request POST 'http://localhost:8080/imports' \
--header 'Content-Type: application/json' \
--data-raw '{
    "filename": "/Users/btodorov/example.ogg"
}'
```
