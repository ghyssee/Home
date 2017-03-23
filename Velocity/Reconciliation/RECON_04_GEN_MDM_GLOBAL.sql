--------------------------------------------------------------------------------;
-- SetUp for Reconciliation
--------------------------------------------------------------------------------;
#foreach( $datasource in $datasources)
INSERT into DATA_SOURCE (DS_ID ,CODE ,NL ,FR ,EN)
values (SEQ_DATA_SOURCE.NEXTVAL,'${datasource.code}','${datasource.description}','${datasource.description}','${datasource.description}');

#end
INSERT into DATA_TYPE (DT_ID, CODE, NL, FR, EN)
values (SEQ_DATA_TYPE.NEXTVAL,''${datatype.code}'','${datatype.description}','${datatype.description}','${datatype.description}');

#foreach( $stream in $streams)
INSERT into STREAM (STREAM_ID, DT_ID, DS_ID, STATUS_ID,VALID_FROM_TX, VALID_TILL_TX, FIRST_UPLOAD ,          
NL ,FR ,EN, CREATED_ON, CREATED_BY, LAST_UPDATED_ON, LAST_UPDATED_BY, INPUT_TYPE, LAUNCH_MATCH_ENGINE )
values 
(SEQ_STREAM.NEXTVAL, 
(select dt_id from data_type where code = '${datatype.code}'), 
(select ds_id from data_source where code = '${stream.datasource.code}'), 
(select status_id from STATUS where code = 'ACTIVE' and type = 'STREAM'),
sysdate, to_date('31/12/2050','DD/MM/YYYY'), null ,
'${stream.description}' ,'${stream.description}' ,'${stream.description}', sysdate, ${userId}, sysdate, ${userId}, '${stream.inputType}', '${stream.launchMatchEngine}' );

#foreach( $fileType in $stream.fileTypes)
INSERT into FILE_SRC_TYP (FST_ID, STREAM_ID, FT_ID, VALID_FROM, VALID_TILL, CREATED_ON, CREATED_BY, LAST_UPDATED_ON, LAST_UPDATED_BY)
values  (SEQ_FILE_SRC_TYP.NEXTVAL,
 (select stream_id from STREAM where FR = '${stream.description}'),
 (select ft_id from FILE_TYPE where code = '${datatype.code}'), 
 sysdate , to_date('31/12/2050','DD/MM/YYYY'), sysdate, ${userId}, sysdate, ${userId});

 #end

#end
