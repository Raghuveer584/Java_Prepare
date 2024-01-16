FROM @docker.pull.registry@/etcc_zulu_java:jdk8.0.222-fontfix-telnet

ARG INSTALL_USER=ritehorizon
ARG INSTALL_GROUP=ritehorizon
ENV APPLICATION_HOME=/home/${INSTALL_USER}
ENV JAR_FILE=@project.artifactId@-@project.version@-exec.jar

RUN groupadd --gid 500 ${INSTALL_GROUP}
RUN useradd --uid 700 --gid 500 --create-home --shell /bin/bash ${INSTALL_USER}

WORKDIR ${APPLICATION_HOME}

COPY ${JAR_FILE} ./

RUN chown ${INSTALL_USER}:${INSTALL_GROUP} ${JAR_FILE}

USER ${INSTALL_USER}

ENTRYPOINT exec java $JAVA_OPTS -jar ./${JAR_FILE}

VOLUME ["${APPLICATION_HOME}/logs", "${APPLICATION_HOME}/config"]

HEALTHCHECK CMD curl --fail http://localhost:8080/ || exit 1
