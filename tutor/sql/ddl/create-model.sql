CREATE TABLE assig_alumne_problema (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `id_alumne` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `id_problema` int(10) UNSIGNED NOT NULL DEFAULT '0',
  PRIMARY KEY  (`id`),
  KEY `FK_assig_alumne_problema__alumne` (`id_alumne`),
  KEY `FK_assig_alumne_problema__problema` (`id_problema`)
) TYPE=InnoDB;

CREATE TABLE assig_professor_alumne (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `id_professor` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `id_alumne` int(10) UNSIGNED NOT NULL DEFAULT '0',
  PRIMARY KEY  (`id`),
  KEY `FK_assignacions_professor` (`id_professor`) REFERENCES u_professors (id),
  KEY `FK_assignacions_alumnes` (`id_alumne`) REFERENCES u_alumnes (id)
) TYPE=InnoDB;

CREATE TABLE est_arbre_problemes (
  `id_estrategia` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `element` varchar(100) NOT NULL DEFAULT '',
  `id_problema` int(10) UNSIGNED NOT NULL DEFAULT '0',
  KEY `FK_est_arbre_problemes_1` (`id_problema`)
) TYPE=InnoDB COMMENT='Arbre de problemes ';

CREATE TABLE est_estrategies (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `id_problema` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `titol` varchar(150) NOT NULL DEFAULT '',
  `fitxer_estrategia` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY  (`id`),
  KEY `FK_estrategies_problema` (`id_problema`)
) TYPE=InnoDB;

CREATE TABLE est_missatges (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `id_professor` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `missatge` varchar(255) NOT NULL DEFAULT '',
  `id_tipus` int(10) UNSIGNED NOT NULL DEFAULT '0',
  PRIMARY KEY  (`id`),
  KEY `FK_est_missatges_professor` (`id_professor`)
) TYPE=InnoDB;

CREATE TABLE est_missatges_estrategies (
  `id_estrategia` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `element` varchar(100) NOT NULL DEFAULT '',
  `id_missatge` int(10) UNSIGNED NOT NULL DEFAULT '0',
  KEY `FK_est_missatges_estrategies_1` (`id_estrategia`)
) TYPE=InnoDB;

CREATE TABLE est_missatges_tipus (
   `id`                int (10) UNSIGNED NOT NULL DEFAULT '0' ,
   `tipus`             varchar (75)NOT NULL DEFAULT '' ,
   PRIMARY KEY (`id`)
)
TYPE = InnoDB;

CREATE TABLE just_estrategies (
  `id` int(10) UNSIGNED ZEROFILL NOT NULL AUTO_INCREMENT,
  `id_justificacio` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `id_estrategia` int(10) UNSIGNED NOT NULL DEFAULT '0',
  PRIMARY KEY  (`id`)
) TYPE=InnoDB;

CREATE TABLE just_problemes (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `id_justificacio` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `id_problema` int(10) UNSIGNED NOT NULL DEFAULT '0',
  PRIMARY KEY  (`id`)
) TYPE=InnoDB;

CREATE TABLE justificacions (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `descripcio` text,
  PRIMARY KEY  (`id`)
) TYPE=InnoDB;

CREATE TABLE problemes (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `id_professor` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `titol` varchar(100) NOT NULL DEFAULT '',
  `enunciat` text NOT NULL,
  `fitxer_inicial` varchar(100) NOT NULL DEFAULT '',
  `fitxer_estrategies` varchar(100) NOT NULL DEFAULT '',
  `actiu` char(1) NOT NULL DEFAULT '1',
  `data` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `tags` text NOT NULL,
  `dificultat` varchar(100) NOT NULL DEFAULT '',
  `public` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY  (`id`),
  KEY `FK_problemes_professor` (`id_professor`)
) TYPE=InnoDB;

CREATE TABLE u_administradors (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `nom` varchar(45) NOT NULL DEFAULT '',
  `cognoms` varchar(75) NOT NULL DEFAULT '',
  `usuari` varchar(12) NOT NULL DEFAULT '',
  `password` varchar(12) NOT NULL DEFAULT '',
  `actiu` char(1) NOT NULL DEFAULT '1',
  PRIMARY KEY  (`id`)
) TYPE=InnoDB;

CREATE TABLE u_alumnes (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `nom` varchar(45) NOT NULL DEFAULT '',
  `cognoms` varchar(75) NOT NULL DEFAULT '',
  `usuari` varchar(12) NOT NULL DEFAULT '',
  `password` varchar(12) NOT NULL DEFAULT '',
  `actiu` char(1) NOT NULL DEFAULT '1',
  PRIMARY KEY  (`id`)
) TYPE=InnoDB;

CREATE TABLE u_professors (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `nom` varchar(45) NOT NULL DEFAULT '',
  `cognoms` varchar(75) NOT NULL DEFAULT '',
  `usuari` varchar(12) NOT NULL DEFAULT '',
  `password` varchar(12) NOT NULL DEFAULT '',
  `actiu` char(1) NOT NULL DEFAULT '1',
  PRIMARY KEY  (`id`)
) TYPE=InnoDB;

