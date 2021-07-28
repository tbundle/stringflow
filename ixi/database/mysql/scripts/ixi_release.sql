ALTER TABLE `sf_user_session` 
DROP COLUMN `token_status`,
CHANGE COLUMN `login_status` `status` TINYINT(2) NOT NULL DEFAULT '1' ;


Drop TABLE IF EXISTS `sf_poll`;
SELECT 'CREATE TABLE sf_poll'  AS '---------------------------------------------';
SOURCE ../tables/sf_poll.sql;

Drop TABLE IF EXISTS `sf_poll_response`;
SELECT 'CREATE TABLE sf_poll_response'  AS '---------------------------------------------';
SOURCE ../tables/sf_poll_response.sql;