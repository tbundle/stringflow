CREATE TABLE `sf_chat_room_members` (
  `uuid` int(11) NOT NULL AUTO_INCREMENT,
  `room_jid` varchar(256) NOT NULL,
  `user_jid` varchar(256) NOT NULL,
  `nick_name` varchar(256) NOT NULL,
  `affiliation` varchar(126) NOT NULL,
  `role` varchar(126) NOT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
