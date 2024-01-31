BEGIN WORK;
SET TRANSACTION READ WRITE;

SET datestyle = YMD;

-- Esborra taules si existien
DROP TABLE match_statistics;
DROP TABLE match;
DROP TABLE player;
DROP TABLE team;

-- Creació de taules
CREATE TABLE team
  (
    name       VARCHAR(50),
    type       VARCHAR(20) NOT NULL,
    country    VARCHAR(50) NOT NULL,
    city       VARCHAR(50) ,
    court_name VARCHAR(50) ,
    CONSTRAINT pk_team PRIMARY KEY (name),
    CONSTRAINT ck_type CHECK (type IN ('Club', 'National Team')),
    CONSTRAINT ck_nulls CHECK (type = 'National Team'
  OR (city                         IS NOT NULL
  AND court_name                   IS NOT NULL) )
  );

CREATE TABLE player
  (
    federation_license_code VARCHAR(20) ,
    first_name              VARCHAR(50) NOT NULL ,
    last_name               VARCHAR(50) NOT NULL ,
    birth_date              DATE NOT NULL,
    gender                  CHAR(1) NOT NULL,
    height                  INTEGER NOT NULL,
    team_name            VARCHAR(50),
    mvp_total		   INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT player_pk PRIMARY KEY (federation_license_code),
    CONSTRAINT player_team_fk FOREIGN KEY (team_name) REFERENCES team(name),
    CONSTRAINT ck_gender CHECK (gender IN ('M', 'F')),
    CONSTRAINT ck_height CHECK (height  > 0),
    CONSTRAINT ck_birth_date CHECK (birth_date < CURRENT_DATE)
  );

CREATE TABLE match
  (
    home_team    VARCHAR(50) ,
    visitor_team VARCHAR(50) ,
    match_date   DATE,
    attendance   INTEGER,
    mvp_player   VARCHAR(20) DEFAULT NULL,
    CONSTRAINT pk_match_pk PRIMARY KEY (home_team, visitor_team, match_date),
    CONSTRAINT fk_match_home_team FOREIGN KEY (home_team) REFERENCES team(name),
    CONSTRAINT fk_match_visitor_team FOREIGN KEY (visitor_team) REFERENCES team(name),
    CONSTRAINT fk_match_player FOREIGN KEY (mvp_player) REFERENCES player(federation_license_code),
    CONSTRAINT ck_home_visitor_team CHECK (home_team <> visitor_team),
    CONSTRAINT ck_attendance CHECK (attendance >= 0)
  );

CREATE TABLE match_statistics
  (
    home_team            VARCHAR(50) ,
    visitor_team         VARCHAR(50) ,
    match_date           DATE,
    player               VARCHAR(20) ,
    minutes_played       INTEGER NOT NULL ,
    points               INTEGER NOT NULL ,
    offensive_rebounds   INTEGER NOT NULL ,
    defensive_rebounds   INTEGER NOT NULL ,
    assists              INTEGER NOT NULL ,
    committed_fouls      INTEGER NOT NULL ,
    received_fouls       INTEGER NOT NULL ,
    free_throw_attempts  INTEGER NOT NULL ,
    free_throw_made      INTEGER NOT NULL ,
    two_point_attempts   INTEGER NOT NULL ,
    two_point_made       INTEGER NOT NULL ,
    three_point_attempts INTEGER NOT NULL ,
    three_point_made     INTEGER NOT NULL ,
    blocks               INTEGER NOT NULL ,
    blocks_against       INTEGER NOT NULL ,
    steals               INTEGER NOT NULL ,
    turnovers            INTEGER NOT NULL ,
    Mvp_score		 INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT pk_match_statistics PRIMARY KEY (home_team, visitor_team, match_date, player),
    CONSTRAINT fk_stat_match FOREIGN KEY (home_team, visitor_team, match_date) REFERENCES MATCH(home_team, visitor_team, match_date),
    CONSTRAINT fk_stat_player FOREIGN KEY (player) REFERENCES player(federation_license_code),
    CONSTRAINT ck_ft_att_made CHECK (free_throw_attempts           >= free_throw_made),
    CONSTRAINT ck_2p_att_made CHECK (two_point_attempts            >= two_point_made),
    CONSTRAINT ck_3p_att_made CHECK (three_point_attempts          >= three_point_made),
    CONSTRAINT ck_MINUTES_PLAYED CHECK (MINUTES_PLAYED             >= 0),
    CONSTRAINT ck_POINTS CHECK (POINTS                             >= 0),
    CONSTRAINT ck_OFFENSIVE_REBOUNDS CHECK (OFFENSIVE_REBOUNDS     >= 0),
    CONSTRAINT ck_DEFENSIVE_REBOUNDS CHECK (DEFENSIVE_REBOUNDS     >= 0),
    CONSTRAINT ck_ASSISTS CHECK (ASSISTS                           >= 0),
    CONSTRAINT ck_COMMITTED_FOULS CHECK (COMMITTED_FOULS           >= 0),
    CONSTRAINT ck_RECEIVED_FOULS CHECK (RECEIVED_FOULS             >= 0),
    CONSTRAINT ck_FREE_THROW_ATTEMPTS CHECK (FREE_THROW_ATTEMPTS   >= 0),
    CONSTRAINT ck_FREE_THROW_MADE CHECK (FREE_THROW_MADE           >= 0),
    CONSTRAINT ck_TWO_POINT_ATTEMPTS CHECK (TWO_POINT_ATTEMPTS     >= 0),
    CONSTRAINT ck_TWO_POINT_MADE CHECK (TWO_POINT_MADE             >= 0),
    CONSTRAINT ck_THREE_POINT_ATTEMPTS CHECK (THREE_POINT_ATTEMPTS >= 0),
    CONSTRAINT ck_THREE_POINT_MADE CHECK (THREE_POINT_MADE         >= 0),
    CONSTRAINT ck_BLOCKS CHECK (BLOCKS                             >= 0),
    CONSTRAINT ck_BLOCKS_AGAINST CHECK (BLOCKS_AGAINST             >= 0),
    CONSTRAINT ck_STEALS CHECK (STEALS                             >= 0),
    CONSTRAINT Ck_Turnovers CHECK (Turnovers                       >= 0)
  );

COMMIT;

    
    
