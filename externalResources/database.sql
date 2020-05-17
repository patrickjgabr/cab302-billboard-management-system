CREATE TABLE `users` (
 	`userID` INT NOT NULL AUTO_INCREMENT,
 	`userName` VARCHAR(64) NOT NULL UNIQUE,
 	`userObject` LONGBLOB NOT NULL,

 	PRIMARY KEY (`userID`)
);

CREATE TABLE `billboards` (
  `billboardID` INT NOT NULL AUTO_INCREMENT,
  `creatorID` INT NOT NULL,
  `billboardName` VARCHAR(64) NOT NULL UNIQUE,
  `billboardObject` LONGBLOB NOT NULL,
  `scheduled` INT NOT NULL DEFAULT 0,

  FOREIGN KEY (`creatorID`) REFERENCES `users` (`userID`),
  PRIMARY KEY (`billboardID`)
);


CREATE TABLE `schedule` (
  `scheduleID` INT NOT NULL AUTO_INCREMENT,
  `creatorID` INT NOT NULL,
  `scheduleObject` LONGBLOB NOT NULL,
  `inputDate` date NOT NULL,
  FOREIGN KEY (`creatorID`) REFERENCES `users` (`userID`),
  PRIMARY KEY (`scheduleID`)
);


CREATE TABLE `sessions` (
	`sessionToken` VARCHAR(64) NOT NULL,
	`userID` INT NOT NULL UNIQUE,
	`expireDate` DATE NOT NULL,
	`expireTime` TIME NOT NULL,
	FOREIGN KEY (`userID`) REFERENCES `users` (`userID`),
	PRIMARY KEY (`sessionToken`)
);

 ALTER TABLE `users` AUTO_INCREMENT = 100000;
 ALTER TABLE `billboards` AUTO_INCREMENT = 100000;
 ALTER TABLE `schedule` AUTO_INCREMENT = 100000;
