FROM alpine
WORKDIR /cliente
RUN apk update
RUN apk fetch openjdk8
RUN apk add openjdk8
ENV JAVA_HOME=/usr/lib/jvm/java-1.8-openjdk
ENV PATH="$JAVA_HOME/bin:${PATH}"
COPY policy.all .
COPY Servidor.java .
COPY ClientMonitor.java .
COPY Client.java .
COPY run.sh .
RUN javac *.java
ENTRYPOINT chmod 777 /clientdata
ENTRYPOINT chmod 777 -R /cliente
ENTRYPOINT ./run.sh

