FROM microsoft/mssql-server-linux:2017-GA

LABEL dockerdb.port=1433

RUN apt-get update && apt-get install -y sqsh

ENV ACCEPT_EULA Y

# The password must be at least 8 characters long and contain characters from three of the following five sets:
# Uppercase letters, Lowercase letters, Base 10 digits, Non-Alphanumeric characters, or unicode characters that
# are categorized as alphabetic characters, but are not uppercase or lowercase.
ENV SA_PASSWORD Password1

COPY Schema.sql /tmp/
COPY Data.sql /tmp/
COPY dbclient.sh /
COPY dbclient_settings.sh /
COPY dbserver.sh /

RUN chmod +x dbclient.sh
RUN chmod +x dbserver.sh

RUN /bin/bash -c "(/dbserver.sh &) && ./dbclient.sh -w && echo 'Executing Schema SQL:'; ./dbclient.sh -s /tmp/Schema.sql && echo 'Executing Data SQL:'; ./dbclient.sh -s /tmp/Data.sql"

CMD ["bash", "-c", "echo 'Starting DB, please wait...' && (/dbserver.sh &> /dev/null &) && /dbclient.sh --wait && /dbclient.sh && tail -f /dev/null"]

HEALTHCHECK CMD /dbclient.sh -c