INSERT INTO USR_ROLES(ID, NAME, CREATED_BY, LAST_UPDATED_BY, CREATED_ON, LAST_UPDATED_ON)
values (role_sequence.nextval, '${role}', 1 , 1, SYSDATE, SYSDATE);

INSERT INTO USR_GROUPS(ID, NAME, CREATED_BY, LAST_UPDATED_BY, CREATED_ON, LAST_UPDATED_ON)
values (group_sequence.nextval, '${role}', 1, 1, SYSDATE, SYSDATE);

insert into usr_rel_groups_roles(id, group_id, role_id) 
values (GROUP_ROLE_SEQUENCE.nextval,
        (select id from usr_groups where name = '${role}'),
        (select id from usr_roles where name = '${role}'));
        
INSERT INTO SECURITY_LEVELS(SL_ID, ROLE_ID, ME_ID)
values(seq_security_levels.nextval, (select id from usr_roles where name = '${role}'), (select me_id from match_engine where code = '${matchEngine}'));
