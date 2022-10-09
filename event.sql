CREATE DATABASE IF NOT EXISTS nidm_1009;

USE nidm_1009;

-- create a table 
create table if not exists nidmTestIUD_1009 (
		c1_int int,
		c2_date  DATETIME NULL DEFAULT CURRENT_TIMESTAMP(),
		primary key(c1_int))
DEFAULT CHARACTER SET = UTF8;


-- Create IUD event to simulated INSERT/UPDATE/Delete

-- ATTN: INSERT may fail duo to duplication of primary Key
create event insertevent on schedule every '2' minute do
insert into nidmTestIUD_1009(c1_int) values(FLOOR( RAND() * 10000));

create event deleteevent on schedule every '3' minute 
do delete from nidmTestIUD_1009
where  mod(c1_int, floor(rand()*(100-50)+50)) = 0;

create event updateevent on schedule every '5' minute 
do update nidmTestIUD_1009 set c1_int = c1_int + 10000
where  mod(c1_int, floor(rand()*(100-50)+50)) = 0;
