-- create Report Fields for flow 1
#foreach( $column in $columns1)
INSERT INTO REPORT_FIELD
    ( rf_id, f_id,stream_id, default_value, widget, column_name, nl,fr,en, created_on, created_by, last_updated_on, last_updated_by) VALUES 
	( seq_report_field.NEXTVAL,null,(select stream_id from stream where fr = '${streamDescription1}'),NULL
     , 'SINGLE', '${column.name}', '${column.description}', '${column.description}', '${column.description}', SYSDATE, ${userId}, SYSDATE, ${userId});
#end
-- create Report Fields for flow 2
#foreach( $column in $columns2)
INSERT INTO REPORT_FIELD
    ( rf_id, f_id,stream_id, default_value, widget, column_name, nl,fr,en, created_on, created_by, last_updated_on, last_updated_by) VALUES 
	( seq_report_field.NEXTVAL,null,(select stream_id from stream where fr = '${streamDescription2}'),NULL
     , 'SINGLE', '${column.name}', '${column.description}', '${column.description}', '${column.description}', SYSDATE, ${userId}, SYSDATE, ${userId});
#end
