FROM openjdk:11-jre-slim

COPY maven/springbatch-sample*.jar /springbatch-sample.jar

ENTRYPOINT ["java","-Dbatch.properties.path=file:/properties/batch.properties","-Djob.name=alimentationJob","-jar","/springbatch-sample.jar","input.file.path=/input/alimentation.csv"]
