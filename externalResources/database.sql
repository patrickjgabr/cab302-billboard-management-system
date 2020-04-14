 DROP DATABASE IF EXISTS applicationDatabase;
 CREATE DATABASE testDatabase;
 USE testDatabase;
 
 CREATE TABLE `users` (
 	`userID` INT NOT NULL AUTO_INCREMENT,
 	`userName` VARCHAR(64) NOT NULL UNIQUE,
 	`userPassword` VARCHAR(64) NOT NULL,
 	
 	PRIMARY KEY (`userID`)
 );
 
CREATE TABLE `billboards` (
  `billboardID` INT NOT NULL AUTO_INCREMENT,
  `creatorName` VARCHAR(64) NOT NULL,
  `name` VARCHAR(64) NOT NULL,
  `imageUrl` VARCHAR(64),
  `messageText` VARCHAR(64),
  `messageTextColour` VARCHAR(64),
  `backgroundColour` VARCHAR(64),
  `informationText` VARCHAR(64),
  `informationTextColour` VARCHAR(64),
  
  PRIMARY KEY (`billboardID`)
);
 
 CREATE TABLE `permissions` (
  `userID` INT NOT NULL,
  `createUser` INT NOT NULL,
  `editUser` INT NOT NULL,
  `deleteUser` INT NOT NULL,
  `changePassword` INT NOT NULL,
  `assignRole` INT NOT NULL,
  `createBillboard` INT NOT NULL,
  `editBillboard` INT NOT NULL,
  `viewBillboard` INT NOT NULL,
  `viewSchedule` INT NOT NULL,
  `editSchedule` INT NOT NULL,
  
  FOREIGN KEY (`userID`) REFERENCES `users` (`userID`),
  PRIMARY KEY (`userID`)
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
	`sessionToken` INT NOT NULL,
	`userID` INT NOT NULL UNIQUE,
	
	FOREIGN KEY (`userID`) REFERENCES `users` (`userID`),
	PRIMARY KEY (`sessionToken`)
);


 ALTER TABLE `users` AUTO_INCREMENT = 100000;
 ALTER TABLE `billboards` AUTO_INCREMENT = 100000;
 ALTER TABLE `schedule` AUTO_INCREMENT = 100000;
 
 INSERT INTO `users` (`userName`, `userPassword`)
 VALUES ('root', 'cf9f67e405c1fbe7dab153d0d1b03fc8ad75ab5de4fc39178358de9e2c95c386');
 
 INSERT INTO `permissions` (`userID`, `createUser`, `editUser`, `deleteUser`, `changePassword`, `assignRole`, `createBillboard`, `editBillboard`, `viewBillboard`, `viewSchedule`, `editSchedule`)
 VALUES (100000, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
