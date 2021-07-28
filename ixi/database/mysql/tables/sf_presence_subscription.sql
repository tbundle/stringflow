CREATE TABLE `sf_presence_subscription` (
  `user_jid` varchar(256) NOT NULL,
  `subscriber_jid` varchar(256) NOT NULL,
  PRIMARY KEY (`user_jid`,`subscriber_jid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
