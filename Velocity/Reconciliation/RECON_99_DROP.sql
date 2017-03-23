DROP SEQUENCE SEQ_FREEZE_${matchEngine.code};
DROP SEQUENCE SEQ_MATCH_${matchEngine.code};
DROP TABLE ${matchEngine.code}_FREEZE;
DROP TABLE ${matchEngine.code}_FREEZE_ARCH;
DROP TABLE ${matchEngine.code}_MATCHED_HEADER;
DROP TABLE ${matchEngine.code}_MATCH_HEADER_ARCH;
DROP INDEX FK_${matchEngine.code}_MH_MR_IDX;
DROP INDEX FK_${matchEngine.code}_MH_FRZ_IDX;
DROP INDEX FK_${matchEngine.code}_MATCH_DATE_IDX;
DROP INDEX FK_${matchEngine.code}_PROCESS_IDX;

DROP SEQUENCE SEQ_${dataSource1}_${dataType.code}; 
DROP TABLE ${dataSource1}_${dataType.code}_ACCEPTOLD;
DROP TABLE ${dataSource1}_${dataType.code}_ARCHIVE;
DROP TABLE ${dataSource1}_${dataType.code}_ERRONEOUS;
DROP TABLE ${dataSource1}_${dataType.code}_MATCHED;
DROP TABLE ${dataSource1}_${dataType.code}_STAGING;
DROP TABLE ${dataSource1}_${dataType.code}_UNMATCHED;

DROP SEQUENCE SEQ_${dataSource2}_${dataType.code}; 
DROP TABLE ${dataSource2}_${dataType.code}_ACCEPTOLD;
DROP TABLE ${dataSource2}_${dataType.code}_ARCHIVE;
DROP TABLE ${dataSource2}_${dataType.code}_ERRONEOUS;
DROP TABLE ${dataSource2}_${dataType.code}_MATCHED;
DROP TABLE ${dataSource2}_${dataType.code}_STAGING;
DROP TABLE ${dataSource2}_${dataType.code}_UNMATCHED;

#foreach( $stream in $streams)
DELETE FROM report_field
WHERE stream_id = (select stream_id from stream where fr = '${stream.description}');
#foreach( $fileType in $stream.fileTypes)

DELETE FROM FILE_SRC_TYP 
WHERE stream_id = (select stream_id from STREAM where FR = '${stream.description}')
AND ft_id = (select ft_id from FILE_TYPE where code = '${fileType.code}');
#end
#end

#foreach( $fileType in $fileTypes)
#foreach( $fileRuleSet in $fileType.listOfRules)
 DELETE FROM FILE_RULE_SET
 WHERE ft_id = (select ft_id from file_type where code = '${fileType.code}')
 AND qc_id = (select qc_id from quality_check where code = '${fileRuleSet.code}');
 
#end
DELETE FROM FILE_TYPE WHERE code = '${fileType.code}';
#end

#foreach( $matchAlgorithm in $matchEngine.matchAlgorithms)
#foreach( $matchPredicate in $matchAlgorithm.matchPredicates)
DELETE FROM MATCH_PREDICATE WHERE MA_ID = (select MA_ID from MATCH_ALGORITHM where code = '${matchAlgorithm.code}');

#end
DELETE FROM MATCH_ALGORITHM
WHERE ME_ID = (select ME_ID from MATCH_ENGINE where code = '${matchEngine.code}')
AND CODE = '${matchAlgorithm.code}';
#end

DELETE FROM MATCH_ENGINE WHERE CODE = '${matchEngine.code}';

#foreach( $stream in $streams)
DELETE FROM STREAM
WHERE DT_ID = (select dt_id from data_type where code = '${dataType.code}'), 
AND DS_ID = (select ds_id from data_source where code = '${stream.datasource.code}');

#end
