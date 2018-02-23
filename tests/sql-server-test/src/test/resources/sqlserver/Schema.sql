--
--
-- Copyright (c) 2017-2018, Namely, Inc. All Rights Reserved.
--
-- Licensed under the Apache License, Version 2.0 (the "License"); You may not
-- use this file except in compliance with the License. You may obtain a copy of
-- the License at:
--
-- http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
-- WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
-- License for the specific language governing permissions and limitations under
-- the License.
--

CREATE DATABASE seebee collate sql_latin1_general_cp1_cs_as;

ALTER DATABASE seebee
SET CHANGE_TRACKING = ON
(CHANGE_RETENTION = 2 DAYS, AUTO_CLEANUP = ON)

go

ALTER DATABASE seebee
SET ALLOW_SNAPSHOT_ISOLATION ON

go

USE seebee;


CREATE TABLE USERS(
    id INTEGER IDENTITY NOT NULL,
    name varchar(32) UNIQUE NOT NULL,
    age INTEGER NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

ALTER TABLE USERS
ENABLE CHANGE_TRACKING
WITH (TRACK_COLUMNS_UPDATED = ON)

CREATE TABLE SWITCHES(
    id INTEGER NOT NULL,
    name varchar(32) UNIQUE NOT NULL,
    state INTEGER NOT NULL,
    CONSTRAINT pk_switches PRIMARY KEY (id)
);

ALTER TABLE SWITCHES
ENABLE CHANGE_TRACKING
WITH (TRACK_COLUMNS_UPDATED = ON)


go
