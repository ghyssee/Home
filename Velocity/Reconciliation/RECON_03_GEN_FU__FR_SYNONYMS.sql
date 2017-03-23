---------------------------------
-- CREATE OBJECTS FOR FAR_USER and FAR_READ --
-- $1. DATA_MGR --
---------------------------------
CREATE OR REPLACE SYNONYM SEQ_${dataSource1}_${dataType} FOR ${object1}.SEQ_${dataSource1}_${dataType};
CREATE OR REPLACE SYNONYM SEQ_FREEZE_${matchEngine} FOR ${object1}.SEQ_FREEZE_${matchEngine};
CREATE OR REPLACE SYNONYM SEQ_MATCH_${matchEngine} FOR ${object1}.SEQ_MATCH_${matchEngine};
CREATE OR REPLACE SYNONYM SEQ_${dataSource2}_${dataType} FOR ${object1}.SEQ_${dataSource2}_${dataType};
CREATE OR REPLACE SYNONYM ${matchEngine}_FREEZE FOR ${object1}.${matchEngine}_FREEZE;
CREATE OR REPLACE SYNONYM ${matchEngine}_FREEZE_ARCH FOR ${object1}.${matchEngine}_FREEZE_ARCH;
CREATE OR REPLACE SYNONYM ${matchEngine}_MATCHED_HEADER FOR ${object1}.${matchEngine}_MATCHED_HEADER;
CREATE OR REPLACE SYNONYM ${matchEngine}_MATCH_HEADER_ARCH FOR ${object1}.${matchEngine}_MATCH_HEADER_ARCH;
CREATE OR REPLACE SYNONYM ${dataSource1}_${dataType}_ACCEPTOLD FOR ${object1}.${dataSource1}_${dataType}_ACCEPTOLD;
CREATE OR REPLACE SYNONYM ${dataSource1}_${dataType}_ARCHIVE FOR ${object1}.${dataSource1}_${dataType}_ARCHIVE;
CREATE OR REPLACE SYNONYM ${dataSource1}_${dataType}_ERRONEOUS FOR ${object1}.${dataSource1}_${dataType}_ERRONEOUS;
CREATE OR REPLACE SYNONYM ${dataSource1}_${dataType}_MATCHED FOR ${object1}.${dataSource1}_${dataType}_MATCHED;
CREATE OR REPLACE SYNONYM ${dataSource1}_${dataType}_STAGING FOR ${object1}.${dataSource1}_${dataType}_STAGING;
CREATE OR REPLACE SYNONYM ${dataSource1}_${dataType}_UNMATCHED FOR ${object1}.${dataSource1}_${dataType}_UNMATCHED;
CREATE OR REPLACE SYNONYM ${dataSource2}_${dataType}_ACCEPTOLD FOR ${object1}.${dataSource2}_${dataType}_ACCEPTOLD;
CREATE OR REPLACE SYNONYM ${dataSource2}_${dataType}_ARCHIVE FOR ${object1}.${dataSource2}_${dataType}_ARCHIVE;
CREATE OR REPLACE SYNONYM ${dataSource2}_${dataType}_ERRONEOUS FOR ${object1}.${dataSource2}_${dataType}_ERRONEOUS;
CREATE OR REPLACE SYNONYM ${dataSource2}_${dataType}_MATCHED FOR ${object1}.${dataSource2}_${dataType}_MATCHED;
CREATE OR REPLACE SYNONYM ${dataSource2}_${dataType}_STAGING FOR ${object1}.${dataSource2}_${dataType}_STAGING;
CREATE OR REPLACE SYNONYM ${dataSource2}_${dataType}_UNMATCHED FOR ${object1}.${dataSource2}_${dataType}_UNMATCHED;
