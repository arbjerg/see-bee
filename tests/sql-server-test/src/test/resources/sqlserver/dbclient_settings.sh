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

# Settings specific for this DB

# Binary of the SQL client in the docker container
CLIENT=sqsh

# DB password
PASSWORD=Password1

# DB user
USER=sa

# Options used for running the client in the docker container
OPTS="-S localhost -U ${USER} -P ${PASSWORD}"

# Command line switch used by the client to take input from the following file
SWITCH_FOR_SCRIPT=-i

# A simple SQL query that can be run to check DB connectivity
PING_SQL="SELECT 1"