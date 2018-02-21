CREATE DATABASE seebee collate sql_latin1_general_cp1_cs_as;

ALTER DATABASE seebee
SET CHANGE_TRACKING = ON
(CHANGE_RETENTION = 2 DAYS, AUTO_CLEANUP = ON)

go

ALTER DATABASE seebee
SET ALLOW_SNAPSHOT_ISOLATION ON

go

USE seebee;


CREATE TABLE ALLTYPES1(
    bigint BIGINT NOT NULL,
    bigintNullable BIGINT NULL,
    bitnn BIT,
    bitNullable BIT NULL,
    charNullable CHAR(10) NULL,
    charnn CHAR(10),
    charSingle CHAR(1),
    dateNullable DATE NULL,
    datenn DATE,
    datetimeNullable DATETIME NULL,
    datetimenn DATETIME,
    datetime2Nullable DATETIME2 NULL,
    datetime2nn DATETIME2,
    decimalNullable DECIMAL(12, 9) NULL,
    decimalnn DECIMAL(12, 9),
    floatNullable FLOAT NULL,
    floatnn FLOAT,
    imageNullable IMAGE NULL,
    imagenn IMAGE,
    intNullable INT NULL,
    intnn INT,
    --mediumMoneyNullable MEDIUMMONEY NULL,
    --mediumMoneynn MEDIUMMONEY,
    moneyNullable MONEY NULL,
    moneynn MONEY,
    numericNullable NUMERIC NULL,
    numericnn NUMERIC,
    nvarShort NVARCHAR(1),
    nvarMedium NVARCHAR(100),
    nvarLong NVARCHAR(4000),
    realNullable REAL NULL,
    realnn REAL,
    sdatetimeNullable SMALLDATETIME NULL,
    sdatetimenn SMALLDATETIME,
    sintNullable SMALLINT NULL,
    sintnn SMALLINT,
    sMoneyNullable SMALLMONEY NULL,
    sMoneynn SMALLMONEY,
    tintNullable TINYINT NULL,
    tintnn TINYINT,
    uuidNullable UNIQUEIDENTIFIER NULL,
    uuidnn UNIQUEIDENTIFIER,
    varShort VARCHAR(1),
    varMedium VARCHAR(100),
    varLong VARCHAR(6000),
    xmlNullable XML NULL,
    xmlnn XML,
    smallDecimal DECIMAL(8,2),
    mediumDecimal DECIMAL(15, 3),
    hugeDecimal DECIMAL(38, 20),

    CONSTRAINT pk_alltypes1 PRIMARY KEY (bigint)
);
ALTER TABLE ALLTYPES1 ENABLE CHANGE_TRACKING WITH (TRACK_COLUMNS_UPDATED = ON)

CREATE TABLE ALLTYPES2(
    bigint BIGINT NOT NULL,
    bigintNullable BIGINT NULL,
    bitnn BIT,
    bitNullable BIT NULL,
    charNullable CHAR(10) NULL,
    charnn CHAR(10),
    charSingle CHAR(1),
    dateNullable DATE NULL,
    datenn DATE,
    datetimeNullable DATETIME NULL,
    datetimenn DATETIME,
    datetime2Nullable DATETIME2 NULL,
    datetime2nn DATETIME2,
    decimalNullable DECIMAL(12, 9) NULL,
    decimalnn DECIMAL(12, 9),
    floatNullable FLOAT NULL,
    floatnn FLOAT,
    imageNullable IMAGE NULL,
    imagenn IMAGE,
    intNullable INT NULL,
    intnn INT,
    --mediumMoneyNullable MEDIUMMONEY NULL,
    --mediumMoneynn MEDIUMMONEY,
    moneyNullable MONEY NULL,
    moneynn MONEY,
    numericNullable NUMERIC NULL,
    numericnn NUMERIC,
    nvarShort NVARCHAR(1),
    nvarMedium NVARCHAR(100),
    nvarLong NVARCHAR(4000),
    realNullable REAL NULL,
    realnn REAL,
    sdatetimeNullable SMALLDATETIME NULL,
    sdatetimenn SMALLDATETIME,
    sintNullable SMALLINT NULL,
    sintnn SMALLINT,
    sMoneyNullable SMALLMONEY NULL,
    sMoneynn SMALLMONEY,
    tintNullable TINYINT NULL,
    tintnn TINYINT,
    uuidNullable UNIQUEIDENTIFIER NULL,
    uuidnn UNIQUEIDENTIFIER,
    varShort VARCHAR(1),
    varMedium VARCHAR(100),
    varLong VARCHAR(6000),
    xmlNullable XML NULL,
    xmlnn XML,
    smallDecimal DECIMAL(8,2),
    mediumDecimal DECIMAL(15, 3),
    hugeDecimal DECIMAL(38, 20),

    CONSTRAINT pk_alltypes2 PRIMARY KEY (bigint)
);
ALTER TABLE ALLTYPES2 ENABLE CHANGE_TRACKING WITH (TRACK_COLUMNS_UPDATED = ON)


CREATE TABLE SWITCH1(
    id INTEGER NOT NULL,
    state INTEGER NOT NULL,
    CONSTRAINT pk_switch1 PRIMARY KEY (id)
);
ALTER TABLE SWITCH1 ENABLE CHANGE_TRACKING WITH (TRACK_COLUMNS_UPDATED = ON)


CREATE TABLE SWITCH2(
    id INTEGER NOT NULL,
    state INTEGER NOT NULL,
    CONSTRAINT pk_switch2 PRIMARY KEY (id)
);
ALTER TABLE SWITCH2 ENABLE CHANGE_TRACKING WITH (TRACK_COLUMNS_UPDATED = ON)


CREATE TABLE SWITCH3(
    id INTEGER NOT NULL,
    state INTEGER NOT NULL,
    CONSTRAINT pk_switch3 PRIMARY KEY (id)
);
ALTER TABLE SWITCH3 ENABLE CHANGE_TRACKING WITH (TRACK_COLUMNS_UPDATED = ON)


CREATE TABLE SWITCH4(
    id INTEGER NOT NULL,
    state INTEGER NOT NULL,
    CONSTRAINT pk_switch4 PRIMARY KEY (id)
);
ALTER TABLE SWITCH4 ENABLE CHANGE_TRACKING WITH (TRACK_COLUMNS_UPDATED = ON)

go
