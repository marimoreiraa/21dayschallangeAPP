CREATE TABLE IF NOT EXISTS `CHALLENGES` (
  `id` int(11) NOT NULL,
  `user` int(11) NOT NULL,
  `title` varchar(64) NOT NULL,
  `description` varchar(256) NOT NULL,
  PRIMARY KEY (`id`)
)