CREATE TABLE `sf_user_roster` (
  `uuid` int(11) NOT NULL AUTO_INCREMENT,
  `user_jid` varchar(256) NOT NULL,
  `contact_jid` varchar(256) NOT NULL,
  `contact_name` varchar(256) NOT NULL,
  `version` int(11) NOT NULL,
  `item_status` tinyint(2) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
