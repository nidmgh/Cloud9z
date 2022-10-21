CREATE DATABASE IF NOT EXISTS nidm_1012;

USE nidm_1012;

-- create a table 
create table if not exists nidmTestIUD (
		c1_int int,
		c2_date  DATETIME NULL DEFAULT CURRENT_TIMESTAMP(),
		primary key(c1_int))
DEFAULT CHARACTER SET = UTF8;


-- Create IUD event to simulated INSERT/UPDATE/Delete

-- ATTN: INSERT may fail duo to duplication of primary Key
create event insertevent on schedule every '2' minute do
insert into nidmTestIUD(c1_int) values(
	FLOOR( RAND() * (10000-1)+1)
);

create event deleteevent on schedule every '3' minute 
do delete from nidmTestIUD
where  mod(c1_int, floor(rand()*(100-50)+50)) = 0;

create event updateevent on schedule every '5' minute 
do update nidmTestIUD set c1_int = c1_int + 10000
where  mod(c1_int, floor(rand()*(100-50)+50)) = 0;


-- 
alter table nidmTestIUD add column c3_op varchar(8);
INSERT INTO nidmTestIUD  (c1_int, c3_op) values(0, 'DELETE');


CREATE TRIGGER setinsert BEFORE INSERT ON nidmTestIUD
       FOR EACH ROW SET NEW.c3_op = "INSERT";


CREATE TRIGGER setupdate BEFORE UPDATE ON nidmTestIUD
       FOR EACH ROW SET NEW.c3_op = "UPDATE";       

DELIMITER $$
CREATE TRIGGER setdelete AFTER DELETE ON nidmTestIUD
       FOR EACH ROW 
BEGIN 
   UPDATE nidmTestIUD set C1_INT=C1_INT-1 where  c3_op = "DELETE";
END $$
DELIMITER ;

CREATE VIEW IUDVIEW 
AS SELECT * FROM  nidmTestIUD WHERE c3_op IN ("INSERT", "UPDATE","DELETE");


INSERT INTO nidmTestIUD  (c1_int, c3_op, c4_json) 
values(999, 'test','{"c1_int": "999", "name": "nidm_json"}' );
