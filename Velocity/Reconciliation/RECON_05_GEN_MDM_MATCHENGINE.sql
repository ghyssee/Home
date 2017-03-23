-- create match engine
INSERT INTO MATCH_ENGINE (ME_ID, CODE, RIGHT_STREAM_ID, LEFT_STREAM_ID, VALID_TILL, VALID_FROM,            
NL ,FR ,EN, CREATED_ON, CREATED_BY ,LAST_UPDATED_ON, LAST_UPDATED_BY )
VALUES
 (SEQ_MATCH_ENGINE.NEXTVAL, '${matchEngine.code}', 
 (select stream_id from stream where fr = '${matchEngine.leftStream.description}'),
 (select stream_id from stream where fr = '${matchEngine.rightStream.description}'),
 to_date('31/12/2050','DD/MM/YYYY'), sysdate,            
 '${matchEngine.description}','${matchEngine.description}' ,'${matchEngine.description}', sysdate, ${userId} ,sysdate, ${userId});

 -- create match algorithm
#foreach( $matchAlgorithm in $matchEngine.matchAlgorithms)
INSERT INTO MATCH_ALGORITHM (MA_ID, ME_ID, CODE, LEFT_CARD, RIGHT_CARD, SEQUENCE, PROCESS, VALID_FROM,VALID_TILL,            
                             NL, FR, EN, CREATED_ON, CREATED_BY, LAST_UPDATED_ON, LAST_UPDATED_BY )
VALUES
 (SEQ_MATCH_ALGORITHM.NEXTVAL,
 (select ME_ID from MATCH_ENGINE where code = '${matchEngine.code}'),
 '${matchAlgorithm.code}', '${matchAlgorithm.leftCard}', '${matchAlgorithm.rightCard}', ${matchAlgorithm.sequence}, '${matchAlgorithm.process}', 
  sysdate ,to_date('31/12/2050','DD/MM/YYYY'),         
 '${matchAlgorithm.description}', '${matchAlgorithm.description}', '${matchAlgorithm.description}',
 sysdate, ${userId}, sysdate, ${userId} );

#foreach( $matchPredicate in $matchAlgorithm.matchPredicates)
INSERT INTO MATCH_PREDICATE (MP_ID, LEFT_COLUMN_NAME, FIELD_OPERATOR, RIGHT_COLUMN_NAME, REL_OPERATOR, RESULT_VALUE,
RIGHT_FUNCTION_ID, MA_ID, LEFT_FUNCTION_ID, NL, FR, EN, WARNING,      
CREATED_ON, CREATED_BY, LAST_UPDATED_ON, LAST_UPDATED_BY) values
(SEQ_MATCH_PREDICATE.NEXTVAL, '${matchPredicate.leftColumnName}', $mu.getOracleStringValue($matchPredicate.fieldOperator), '${matchPredicate.rightColumnName}', '${matchPredicate.relOperator}', 
$mu.getOracleStringValue($matchPredicate.resultValue), null, 
(select MA_ID from MATCH_ALGORITHM where code = '${matchAlgorithm.code}'),
null, '${matchPredicate.description}', '${matchPredicate.description}', '${matchPredicate.description}', '${matchPredicate.warning}',      
sysdate, ${userId}, sysdate, ${userId});
#end
#end
 