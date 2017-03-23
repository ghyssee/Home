---------------------------------
-- CREATE OBJECTS FOR DATA_MGR --
-- &1 tablespace for data in DATA_MGR --     FAR_RECO_DATA
-- &2 tablespace for indexes in DATA_MGR --  FAR_RECO_INDX
-- &3 FAR_USER --                            FAR_USER
-- &4 FAR_READ --                            FAR_READ
---------------------------------
-- Create Sequences
CREATE SEQUENCE SEQ_FREEZE_${matchEngine}
START WITH 1 MINVALUE 0 NOMAXVALUE NOCYCLE CACHE 20 NOORDER;
GRANT ALTER, SELECT ON SEQ_FREEZE_${matchEngine} TO ${object3};
CREATE SEQUENCE SEQ_MATCH_${matchEngine}
START WITH 1 MINVALUE 0 NOMAXVALUE NOCYCLE CACHE 20 NOORDER;
GRANT ALTER, SELECT ON SEQ_MATCH_${matchEngine} TO ${object3};

-- Create Freeze Tables
CREATE TABLE ${matchEngine}_FREEZE (
   ID                                           VARCHAR2(36)             NOT NULL,
   AUTHORITY_ID                                 VARCHAR2(36)             NOT NULL,
   USER_COMMENT                                 VARCHAR2(256),
   FREEZE_DATE                                  DATE                     NOT NULL,
CONSTRAINT PK_${matchEngine}_FREEZE PRIMARY KEY (ID)
   USING INDEX TABLESPACE ${object2} )
TABLESPACE ${object1};
GRANT SELECT, DELETE, INSERT, UPDATE ON ${matchEngine}_FREEZE TO ${object3};
GRANT SELECT ON ${matchEngine}_FREEZE TO ${object4};
CREATE TABLE ${matchEngine}_FREEZE_ARCH (
   ID                                           VARCHAR2(36)             NOT NULL,
   AUTHORITY_ID                                 VARCHAR2(36)             NOT NULL,
   USER_COMMENT                                 VARCHAR2(256),
   FREEZE_DATE                                  DATE                     NOT NULL,
CONSTRAINT PK_${matchEngine}_FREEZE_ARCH PRIMARY KEY (ID)
   USING INDEX TABLESPACE ${object2} )
TABLESPACE ${object1};
GRANT SELECT, DELETE, INSERT, UPDATE ON ${matchEngine}_FREEZE_ARCH TO ${object3};
GRANT SELECT ON ${matchEngine}_FREEZE_ARCH TO ${object4};
-- Create the match header 
CREATE TABLE ${matchEngine}_MATCHED_HEADER (
   ID                                           VARCHAR2(36)             NOT NULL,
   AUTHORITY_ID                                 VARCHAR2(36),
   MR_ID                                        VARCHAR2(36),
   FREEZE_ID                                    VARCHAR2(36),
   MATCH_DATE                                   DATE                     NOT NULL,
   USER_COMMENT                                 VARCHAR2(256),
   PROCESS                                      VARCHAR2(9),
   VERSION_ID                                   INTEGER,
CONSTRAINT PK_${matchEngine}_MATCHED_HEADER PRIMARY KEY (ID)
   USING INDEX TABLESPACE ${object2} )
TABLESPACE ${object1};
GRANT SELECT, DELETE, INSERT, UPDATE ON ${matchEngine}_MATCHED_HEADER TO ${object3};
GRANT SELECT ON ${matchEngine}_MATCHED_HEADER TO ${object4};
CREATE TABLE ${matchEngine}_MATCH_HEADER_ARCH (
   ID                                           VARCHAR2(36)             NOT NULL,
   AUTHORITY_ID                                 VARCHAR2(36),
   MR_ID                                        VARCHAR2(36),
   FREEZE_ID                                    VARCHAR2(36),
   MATCH_DATE                                   DATE                     NOT NULL,
   USER_COMMENT                                 VARCHAR2(256),
   PROCESS                                      VARCHAR2(9),
CONSTRAINT PK_${matchEngine}_MA_HDR_ARCH PRIMARY KEY (ID)
   USING INDEX TABLESPACE ${object2} )
TABLESPACE ${object1};
GRANT SELECT, DELETE, INSERT, UPDATE ON ${matchEngine}_MATCH_HEADER_ARCH TO ${object3};
GRANT SELECT ON ${matchEngine}_MATCH_HEADER_ARCH TO ${object4};
ALTER TABLE ${matchEngine}_FREEZE
   ADD CONSTRAINT FK_${matchEngine}_USR FOREIGN KEY (AUTHORITY_ID)
   REFERENCES USR_USERS (ID);
ALTER TABLE ${matchEngine}_FREEZE_ARCH
   ADD CONSTRAINT FK_${matchEngine}FRZA_USR FOREIGN KEY (AUTHORITY_ID)
   REFERENCES USR_USERS (ID);
ALTER TABLE ${matchEngine}_MATCHED_HEADER
   ADD CONSTRAINT FK_${matchEngine}_MH_USR FOREIGN KEY (AUTHORITY_ID)
   REFERENCES USR_USERS (ID);
ALTER TABLE ${matchEngine}_MATCHED_HEADER
   ADD CONSTRAINT FK_${matchEngine}_MH_FRZ FOREIGN KEY (FREEZE_ID)
   REFERENCES ${matchEngine}_FREEZE (ID);
ALTER TABLE ${matchEngine}_MATCHED_HEADER
   ADD CONSTRAINT FK_${matchEngine}_MH_MR FOREIGN KEY (MR_ID)
   REFERENCES MATCH_RUN (MR_ID);
ALTER TABLE ${matchEngine}_MATCH_HEADER_ARCH
   ADD CONSTRAINT FK_${matchEngine}_MHA_USR FOREIGN KEY (AUTHORITY_ID)
   REFERENCES USR_USERS (ID);
ALTER TABLE ${matchEngine}_MATCH_HEADER_ARCH
   ADD CONSTRAINT FK_${matchEngine}_MHA_FRZ FOREIGN KEY (FREEZE_ID)
   REFERENCES ${matchEngine}_FREEZE_ARCH (ID);
ALTER TABLE ${matchEngine}_MATCH_HEADER_ARCH
   ADD CONSTRAINT FK_${matchEngine}_MHA_MR FOREIGN KEY (MR_ID)
   REFERENCES MATCH_RUN (MR_ID);
CREATE INDEX FK_${matchEngine}_MH_MR_IDX ON ${matchEngine}_MATCHED_HEADER ( MR_ID ) TABLESPACE ${object2}
PARALLEL ( DEGREE 4 INSTANCES 1);
CREATE INDEX FK_${matchEngine}_MH_FRZ_IDX ON ${matchEngine}_MATCHED_HEADER ( FREEZE_ID ) TABLESPACE ${object2}
PARALLEL ( DEGREE 4 INSTANCES 1);
CREATE INDEX FK_${matchEngine}_MATCH_DATE_IDX ON ${matchEngine}_MATCHED_HEADER ( TRUNC(MATCH_DATE) ) TABLESPACE ${object2}
PARALLEL ( DEGREE 4 INSTANCES 1);
CREATE BITMAP INDEX FK_${matchEngine}_PROCESS_IDX ON ${matchEngine}_MATCHED_HEADER ( PROCESS ) TABLESPACE ${object2}
PARALLEL ( DEGREE 4 INSTANCES 1);
