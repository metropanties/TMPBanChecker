FROM openjdk:17-jdk-alpine
WORKDIR /TMPChecker
ADD /build/libs/TMPChecker-1.0.0-all.jar TMPChecker.jar
CMD ["java", "-jar", "TMPChecker.jar"]