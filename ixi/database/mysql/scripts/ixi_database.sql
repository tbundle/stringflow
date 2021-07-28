Drop TABLE IF EXISTS `sf_chat_room`;
SELECT 'CREATE TABLE sf_chat_room'  AS '---------------------------------------------';
SOURCE ../tables/sf_chat_room.sql;

Drop TABLE IF EXISTS `sf_chat_room_members`;
SELECT 'CREATE TABLE sf_chat_room_members'  AS '---------------------------------------------';
SOURCE ../tables/sf_chat_room_members.sql;

Drop TABLE IF EXISTS `sf_media_cache`;
SELECT 'CREATE TABLE sf_media_cache'  AS '---------------------------------------------';
SOURCE ../tables/sf_media_cache.sql;

Drop TABLE IF EXISTS `sf_message`;
SELECT 'CREATE TABLE sf_message'  AS '---------------------------------------------';
SOURCE ../tables/sf_message.sql;

Drop TABLE IF EXISTS `sf_presence_subscription`;
SELECT 'CREATE TABLE sf_presence_subscription'  AS '---------------------------------------------';
SOURCE ../tables/sf_presence_subscription.sql;

Drop TABLE IF EXISTS `sf_profile_media`;
SELECT 'CREATE TABLE sf_profile_media'  AS '---------------------------------------------';
SOURCE ../tables/sf_profile_media.sql;

Drop TABLE IF EXISTS `sf_session_log`;
SELECT 'CREATE TABLE sf_session_log'  AS '---------------------------------------------';
SOURCE ../tables/sf_session_log.sql;

Drop TABLE IF EXISTS `sf_undelieverd_message`;
SELECT 'CREATE TABLE sf_undelieverd_message'  AS '---------------------------------------------';
SOURCE ../tables/sf_undelieverd_message.sql;

Drop TABLE IF EXISTS `sf_user`;
SELECT 'CREATE TABLE sf_user'  AS '---------------------------------------------';
SOURCE ../tables/sf_user.sql;

Drop TABLE IF EXISTS `sf_user_roster`;
SELECT 'CREATE TABLE sf_user_roster'  AS '---------------------------------------------';
SOURCE ../tables/sf_user_roster.sql;
