--------------------------------------------------------------------------------;
-- SetUp for Reconciliation 'IDSMONEYS';
--------------------------------------------------------------------------------;
#foreach( $datasource in $datasources)
INSERT into DATA_SOURCE (DS_ID ,CODE ,NL ,FR ,EN)
values (SEQ_DATA_SOURCE.NEXTVAL,'${datasource.code}','${datasource.description}','${datasource.description}','${datasource.description}');

#end
INSERT into DATA_TYPE (DT_ID, CODE, NL, FR, EN)
values (SEQ_DATA_TYPE.NEXTVAL,''${datatype.code}'','${datatype.description}','${datatype.description}','${datatype.description}');

#foreach( $fileType in $fileTypes)
-- create file type
INSERT into FILE_TYPE (FT_ID, CODE, MASK, RELATIVE_PATH, VALID_FROM, VALID_TILL, NL, FR, EN, CREATED_ON ,CREATED_BY ,   
LAST_UPDATED_ON, LAST_UPDATED_BY ,MULTIPLE_TYPES_FLAG, XSD_SCHEMA, POPULATE_UNMATCHED_METHOD, ROOT_ELEMENT)
values
(SEQ_FILE_TYPE.NEXTVAL, '${fileType.code}', '${fileType.mask}', '${fileType.relativePath}',
 sysdate, to_date('31/12/2050','DD/MM/YYYY'), '${fileType.description}', '${fileType.description}', '${fileType.description}',
 sysdate ,363 , sysdate, 363 ,'${fileType.multipleTypes}', null, '${fileType.unmatchedMethod}', null);
-- add rules
#foreach( $fileRuleSet in $fileType.listOfRules)
INSERT into FILE_RULE_SET (FRS_ID, FT_ID, QC_ID, SEQUENCE, VALID_FROM, VALID_TILL, CREATED_ON, CREATED_BY, LAST_UPDATED_ON, LAST_UPDATED_BY)
 values
 (SEQ_FILE_RULE_SET.NEXTVAL, 
 (select ft_id from file_type where code = '${fileType.code}'),
 (select qc_id from quality_check where code = '${fileRuleSet.code}'), 
 1, sysdate, to_date('31/12/2050','DD/MM/YYYY'), sysdate, ${userId}, to_date('31/12/2050','DD/MM/YYYY'), ${userId});
#end
#end
