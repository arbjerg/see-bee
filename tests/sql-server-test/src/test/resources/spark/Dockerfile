#
#
# Copyright (c) 2017-2018, Namely, Inc. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License"); You may not
# use this file except in compliance with the License. You may obtain a copy of
# the License at:
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations under
# the License.
#

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

