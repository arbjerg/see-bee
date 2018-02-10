FROM p7hb/docker-spark:2.2.0

RUN apt-get -yqq update && \
    apt-get -yqq upgrade && \
    apt-get install -yqq openssh-server && \
    yes "" | ssh-keygen -N "" && \
    cp /root/.ssh/id_rsa.pub /root/.ssh/authorized_keys && \
    /etc/init.d/ssh start && \
    ssh-keyscan remote_server >> /root/.ssh/known_hosts

EXPOSE 7077

LABEL dockerdb.port=7077
# and 4040 and 8081 and 8080

VOLUME /spool

CMD ["bash", "-c", "/etc/init.d/ssh start; start-all.sh; pyspark"]

COPY stream_table.py /
COPY show_table.py /

