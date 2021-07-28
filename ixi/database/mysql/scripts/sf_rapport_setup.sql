
INSERT INTO sf_user (jabber_id, user_name, email, contact_number, password, profile_id, status) 
select concat(u.user_id, '@alterbasics.com') as jid, u.user_name, u.email, u.contact_number, md5('1234'), 0, 1 from user u where status = 1;


INSERT INTO sf_user_roster_version select u.user_id, 1 from sf_user u;

INSERT INTO sf_presence_subscription SELECT u1.user_id, u2.user_id FROM sf_user u1 INNER JOIN sf_user u2 ON u1.user_id != u2.user_id;

INSERT INTO sf_user_roster
SELECT u1.user_id, u2.user_id, u2.user_name, 1, 1, current_timestamp() FROM sf_user u1 INNER JOIN sf_user u2 ON u1.user_id != u2.user_id;