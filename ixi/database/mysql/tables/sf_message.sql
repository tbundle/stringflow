CREATE TABLE `sf_message` (
  `UUID` int(11) NOT NULL AUTO_INCREMENT,
  `message` text NOT NULL,
  `sender_jid` varchar(256) NOT NULL,
  `receiver_jid` varchar(256) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `delivery_time` timestamp NULL DEFAULT NULL,
  `delivered` tinyint(2) DEFAULT '1',
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;