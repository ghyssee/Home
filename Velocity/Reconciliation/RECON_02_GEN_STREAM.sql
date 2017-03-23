#macro (display $column)
${column.name}			${column.type}(${column.length} ${column.type2})#if ($column.required) NOT NULL#end,
#end
---------------------------------
-- CREATE OBJECTS FOR DATA_MGR --
-- &1 tablespace for data in DATA_MGR --     FAR_RECO_DATA
-- &2 tablespace for indexes in DATA_MGR --  FAR_RECO_INDX
-- &3 FAR_USER --                            FAR_USER
-- &4 FAR_READ --                            FAR_READ
---------------------------------
-- Create sequences
CREATE SEQUENCE SEQ_${dataSource}_${dataType}
START WITH 1 MINVALUE 0 NOMAXVALUE NOCYCLE CACHE 20 NOORDER;
GRANT ALTER, SELECT ON SEQ_${dataSource}_${dataType} TO ${object3};

-- Important remark!!!:
-- If you have the notion of a transaction date in your stream define a stream specific column TXDATE DATE
-- This column is used to check the boundary columns VALID_FROM_TX and VALID_TILL_TX on the stream table.
CREATE TABLE ${dataSource}_${dataType}_ACCEPTOLD (
INTERNALTXID		VARCHAR2(36 CHAR) 	NOT NULL,
IF_ID				VARCHAR2(9 CHAR) 	NOT NULL,
#foreach( $column in $columns )
#display($column)
#end
EXTERNALID			VARCHAR2(256 CHAR) NOT NULL,
CONSTRAINT PK_${dataSource}_${dataType}_ACCEPTOLD PRIMARY KEY (INTERNALTXID)
   USING INDEX TABLESPACE ${object2} )
TABLESPACE ${object1};
GRANT SELECT, DELETE, INSERT, UPDATE ON ${dataSource}_${dataType}_ACCEPTOLD TO ${object3};
GRANT SELECT ON ${dataSource}_${dataType}_ACCEPTOLD TO ${object4};
CREATE TABLE ${dataSource}_${dataType}_ARCHIVE (
INTERNALTXID		VARCHAR2(36 CHAR) NOT NULL,
DET_INTERNALTXID	VARCHAR2(36 CHAR),
IF_ID				VARCHAR2(9 CHAR) 	NOT NULL,
#foreach( $column in $columns )
#display($column)
#end
EXTERNALID			VARCHAR2(256 CHAR) NOT NULL	,
UNIQUE_ID			VARCHAR2(19 CHAR),
CONSTRAINT PK_${dataSource}_${dataType}_ARCHIVE PRIMARY KEY (INTERNALTXID)
   USING INDEX TABLESPACE ${object2} )
TABLESPACE ${object1};
GRANT SELECT, DELETE, INSERT, UPDATE ON ${dataSource}_${dataType}_ARCHIVE TO ${object3};
GRANT SELECT ON ${dataSource}_${dataType}_ARCHIVE TO ${object4};
CREATE TABLE ${dataSource}_${dataType}_ERRONEOUS (
INTERNALTXID		VARCHAR2(36 CHAR) NOT NULL,
IF_ID				VARCHAR2(9 CHAR) 	NOT NULL,
#foreach( $column in $columns )
#display($column)
#end
EXTERNALID			VARCHAR2(256 CHAR) NOT NULL,
UNIQUE_ID			VARCHAR2(19 CHAR),
CONSTRAINT PK_${dataSource}_${dataType}_ERRONEOUS PRIMARY KEY (INTERNALTXID)
   USING INDEX TABLESPACE ${object2} )
TABLESPACE ${object1};
GRANT SELECT, DELETE, INSERT, UPDATE ON ${dataSource}_${dataType}_ERRONEOUS TO ${object3};
GRANT SELECT ON ${dataSource}_${dataType}_ERRONEOUS TO ${object4};
CREATE TABLE ${dataSource}_${dataType}_MATCHED (
INTERNALTXID		VARCHAR2(36 CHAR) NOT NULL,
IF_ID				VARCHAR2(9 CHAR) NOT NULL,
MATCH_ID 			VARCHAR2(36 CHAR) NOT NULL,
IF_ID				VARCHAR2(9 CHAR) 	NOT NULL,
#foreach( $column in $columns )
#display($column)
#end
EXTERNALID			VARCHAR2(256 CHAR) NOT NULL,
UNIQUE_ID			VARCHAR2(19 CHAR),
CONSTRAINT PK_${dataSource}_${dataType}_MATCHED PRIMARY KEY (INTERNALTXID)
   USING INDEX TABLESPACE ${object2} )
TABLESPACE ${object1};
GRANT SELECT, DELETE, INSERT, UPDATE ON ${dataSource}_${dataType}_MATCHED TO ${object3};
GRANT SELECT ON ${dataSource}_${dataType}_MATCHED TO ${object4};
-- Important Remark:
-- The staging table can contain some extra processing fields.
-- For these fields you have to write a customization for the code to process them.
CREATE TABLE ${dataSource}_${dataType}_STAGING (
IF_ID				VARCHAR2(9 CHAR) 	NOT NULL,
#foreach( $column in $columns )
${column.name}			${column.type}(${column.length} ${column.type2})#if ($column.required) NOT NULL#end,
#end
TABLESPACE ${object1};
GRANT SELECT, DELETE, INSERT, UPDATE ON ${dataSource}_${dataType}_STAGING TO ${object3};
GRANT SELECT ON ${dataSource}_${dataType}_STAGING TO ${object4};
CREATE TABLE ${dataSource}_${dataType}_UNMATCHED (
INTERNALTXID		VARCHAR2(36 CHAR) NOT NULL,
IF_ID				VARCHAR2(9 CHAR) NOT NULL,
#foreach( $column in $columns )
#display($column)
#end
EXTERNALID			VARCHAR2(256 CHAR) NOT NULL,
UNIQUE_ID			VARCHAR2(19 CHAR),
CONSTRAINT PK_${dataSource}_${dataType}_UNMATCHED PRIMARY KEY (INTERNALTXID)
   USING INDEX TABLESPACE ${object2} )
TABLESPACE ${object1};
GRANT SELECT, DELETE, INSERT, UPDATE ON ${dataSource}_${dataType}_UNMATCHED TO ${object3};
GRANT SELECT ON ${dataSource}_${dataType}_UNMATCHED TO ${object4};
