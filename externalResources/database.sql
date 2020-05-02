CREATE TABLE `users` (
 	`userID` INT NOT NULL AUTO_INCREMENT,
 	`userName` VARCHAR(64) NOT NULL UNIQUE,
 	`userObject` LONGBLOB NOT NULL,

 	PRIMARY KEY (`userID`)
);

CREATE TABLE `billboards` (
  `billboardID` INT NOT NULL AUTO_INCREMENT,
  `billboardName` VARCHAR(64) NOT NULL UNIQUE,
  `billboardObject` LONGBLOB NOT NULL,

  PRIMARY KEY (`billboardID`)
);


CREATE TABLE `schedule` (
  `eventID` INT NOT NULL AUTO_INCREMENT,
  `billboardID` INT NOT NULL,
  `startTime` time NOT NULL,
  `endTime` time NOT NULL,
  `duration` time NOT NULL,
  `interval` time NOT NULL,
  `date` date NOT NULL,
  `inputDate` date NOT NULL,
  FOREIGN KEY (`billboardID`) REFERENCES `billboards` (`billboardID`),
  PRIMARY KEY (`eventID`)
);


CREATE TABLE `sessions` (
	`sessionToken` VARCHAR(64) NOT NULL,
	`userID` INT NOT NULL UNIQUE,
	FOREIGN KEY (`userID`) REFERENCES `users` (`userID`),
	PRIMARY KEY (`sessionToken`)
);

 ALTER TABLE `users` AUTO_INCREMENT = 100000;
 ALTER TABLE `billboards` AUTO_INCREMENT = 100000;
 ALTER TABLE `schedule` AUTO_INCREMENT = 100000;
