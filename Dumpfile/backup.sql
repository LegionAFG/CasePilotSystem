-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: localhost    Database: klientenverwaltung
-- ------------------------------------------------------
-- Server version	8.0.38

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `appointment`
--

DROP TABLE IF EXISTS `appointment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appointment` (
  `appointmentId` int NOT NULL AUTO_INCREMENT,
  `date` date DEFAULT NULL,
  `time` time DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `institution` varchar(100) DEFAULT NULL,
  `priority` enum('Niedrig','Mittel','Hoch') DEFAULT NULL,
  `status` enum('Offen','Erledigt') DEFAULT NULL,
  `clientIfaNumber` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`appointmentId`),
  KEY `klientIfaNummer` (`clientIfaNumber`),
  CONSTRAINT `appointment_ibfk_1` FOREIGN KEY (`clientIfaNumber`) REFERENCES `client` (`ifaNumber`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appointment`
--

LOCK TABLES `appointment` WRITE;
/*!40000 ALTER TABLE `appointment` DISABLE KEYS */;
INSERT INTO `appointment` VALUES (1,'2024-11-15','16:43:00','Parkweg 3','Beratungszentrum','Hoch','Erledigt','123456'),(15,'2024-10-23','13:30:00','tahlham 80','Thalham','Hoch','Offen','374309');
/*!40000 ALTER TABLE `appointment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client`
--

DROP TABLE IF EXISTS `client`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `client` (
  `ifaNumber` varchar(50) NOT NULL,
  `lastName` varchar(100) DEFAULT NULL,
  `firstName` varchar(100) DEFAULT NULL,
  `birthDate` date DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `nationality` varchar(50) DEFAULT NULL,
  `relationshipStatus` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`ifaNumber`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client`
--

LOCK TABLES `client` WRITE;
/*!40000 ALTER TABLE `client` DISABLE KEYS */;
INSERT INTO `client` VALUES ('123456','Müller','Maxi','1985-05-15','Männlich','Deutschland','Ledig'),('123457','Neumann','Sarah','1984-09-21','Weiblich','Schweiz','Ledig'),('123458','Weiss','Theresa','1996-03-10','Weiblich','Schweiz','Ledig'),('123459','Brandt','Emil','1989-04-14','Männlich','Deutschland','Ledig'),('123460','Stein','Emma','1992-02-14','Weiblich','Österreich','Ledig'),('123461','Haas','Jakob','1979-03-06','Männlich','Schweiz','Geschieden'),('123462','Scholz','Elena','1984-12-25','Weiblich','Schweiz','Ledig'),('123463','Hein','Maya','1981-12-09','Weiblich','Deutschland','Verheiratet'),('148406','Katsumi','Yamamoto','2024-09-30','Männlich','Japaner','Ledig'),('234567','Schmidt','Julia','1987-07-20','Weiblich','Österreich','Verheiratet'),('234568','Klein','Daniel','1996-03-09','Männlich','Österreich','Ledig'),('234569','Köhler','David','1997-06-18','Männlich','Schweiz','Verheiratet'),('234570','Krause','Lucas','1983-11-05','Männlich','Deutschland','Verheiratet'),('234571','Maurer','Clara','1986-12-22','Weiblich','Österreich','Ledig'),('234572','Böhm','Alexander','1990-03-23','Männlich','Deutschland','Verwitwet'),('234573','Otto','Sophie','1996-07-19','Weiblich','Deutschland','Verheiratet'),('234574','Groß','Luis','1989-11-04','Männlich','Deutschland','Verheiratet'),('234575','Hanke','Erik','1990-08-02','Männlich','Schweiz','Ledig'),('345678','Bauer','Anna','1991-09-11','Weiblich','Schweiz','Ledig'),('345679','Wolf','Petra','1979-12-25','Weiblich','Deutschland','Verwitwet'),('345680','Schulz','Nina','1991-03-04','Weiblich','Deutschland','Ledig'),('345681','Peters','Sophia','1991-08-18','Weiblich','Österreich','Geschieden'),('345682','Schuster','Maximilian','1994-07-17','Männlich','Schweiz','Verwitwet'),('345683','Schwarz','Isabella','1988-08-11','Weiblich','Schweiz','Verheiratet'),('345684','Hartmann','Moritz','1989-05-10','Männlich','Österreich','Ledig'),('345685','Vogt','Greta','1987-09-15','Weiblich','Österreich','Geschieden'),('345686','Hofmann','Ella','1996-01-06','Weiblich','Deutschland','Verheiratet'),('374309','Emil','Musterman','1996-10-02','Männlich','Afghanistan','Ledig'),('456789','Wagner','Stefan','1982-11-08','Männlich','Deutschland','Geschieden'),('456790','Zimmermann','Johannes','1992-07-07','Männlich','Deutschland','Verheiratet'),('456791','Becker','Jonas','1982-11-25','Männlich','Österreich','Verheiratet'),('456792','Jung','Patrick','1977-09-14','Männlich','Deutschland','Ledig'),('456793','Bergmann','Emily','1995-08-04','Weiblich','Deutschland','Geschieden'),('456794','Meister','Benjamin','1975-10-26','Männlich','Deutschland','Geschieden'),('456795','Schreiber','Leonie','1986-01-17','Weiblich','Schweiz','Verheiratet'),('456796','Werner','Tim','1983-03-21','Männlich','Schweiz','Ledig'),('456797','Lang','Noah','1983-06-16','Männlich','Österreich','Ledig'),('526728','Steve','Fox','2024-10-01','Männlich','Usa','Verheiratet'),('567890','Richter','Michael','1990-03-12','Männlich','Deutschland','Verheiratet'),('567891','Schröder','Lena','1986-04-03','Weiblich','Österreich','Geschieden'),('567892','Hoffmann','Marie','1995-04-13','Weiblich','Deutschland','Ledig'),('567893','Vogel','Mia','1993-12-19','Weiblich','Schweiz','Verheiratet'),('567894','Sauer','Felix','1981-02-18','Männlich','Deutschland','Verheiratet'),('567895','Seidel','Charlotte','1983-11-19','Weiblich','Österreich','Ledig'),('567896','Franke','Oskar','1993-09-22','Männlich','Deutschland','Ledig'),('567897','Lenz','Sofia','1991-05-12','Weiblich','Deutschland','Verheiratet'),('567898','Dietrich','Nina','1992-02-18','Weiblich','Deutschland','Verwitwet'),('678901','Weber','Sabine','1995-01-19','Weiblich','Schweiz','Verwitwet'),('678902','Braun','Paul','1994-08-13','Männlich','Schweiz','Ledig'),('678903','Mayer','Florian','1979-09-30','Männlich','Schweiz','Geschieden'),('678904','Götz','Tobias','1984-10-20','Männlich','Österreich','Geschieden'),('678905','Winkler','Lea','1993-05-11','Weiblich','Österreich','Verheiratet'),('678906','Engel','Samuel','1987-07-05','Männlich','Schweiz','Ledig'),('678907','Albrecht','Emily','1992-12-13','Weiblich','Österreich','Verwitwet'),('678908','Jäger','Elias','1997-06-17','Männlich','Österreich','Ledig'),('678909','Pohl','Mats','1994-10-03','Männlich','Schweiz','Geschieden'),('780165','Said','Badr','1990-12-01','Männlich','Usa','Verheiratet'),('789012','Fischer','Robert','1975-06-23','Männlich','Deutschland','Ledig'),('789013','Krüger','Lisa','1998-11-29','Weiblich','Deutschland','Verheiratet'),('789014','Koch','Hannah','1990-05-21','Weiblich','Deutschland','Verheiratet'),('789015','Arnold','Amelie','1992-05-25','Weiblich','Deutschland','Ledig'),('789016','Simon','Noah','1984-11-29','Männlich','Deutschland','Ledig'),('789017','Reuter','Anna','1991-09-02','Weiblich','Deutschland','Verheiratet'),('789018','Roth','Henri','1981-04-05','Männlich','Deutschland','Ledig'),('789019','Baumann','Jana','1985-01-08','Weiblich','Schweiz','Verwitwet'),('789020','Ziegler','Lilly','1990-11-26','Weiblich','Deutschland','Ledig'),('890123','Hoffmann','Sandra','1989-10-16','Weiblich','Österreich','Geschieden'),('890124','Meier','Thomas','1973-02-14','Männlich','Deutschland','Verwitwet'),('890125','Fuchs','Johanna','1987-12-07','Weiblich','Österreich','Ledig'),('890126','Heinrich','Leon','1988-06-30','Männlich','Schweiz','Verheiratet'),('890127','Schmitt','Lina','1996-06-09','Weiblich','Schweiz','Verheiratet'),('890128','Lorenz','Maxim','1994-06-22','Männlich','Österreich','Verheiratet'),('890129','Kuhn','Marie','1990-06-01','Weiblich','Österreich','Geschieden'),('890130','König','Lara','1994-07-20','Weiblich','Deutschland','Ledig'),('901234','Schneider','Lukas','1993-12-04','Männlich','Deutschland','Verheiratet'),('901235','Lehmann','Laura','1983-01-09','Weiblich','Österreich','Ledig'),('901236','Lang','Simon','1985-06-15','Männlich','Deutschland','Geschieden'),('901237','Haas','Sophia','1997-09-28','Weiblich','Deutschland','Verheiratet'),('901238','Ziegler','Luca','1980-01-12','Männlich','Deutschland','Geschieden'),('901239','Bayer','Nina','1982-10-15','Weiblich','Deutschland','Ledig'),('901240','Kramer','Anton','1995-02-08','Männlich','Deutschland','Verheiratet'),('901241','Möller','David','1988-03-29','Männlich','Österreich','Geschieden');
/*!40000 ALTER TABLE `client` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `document`
--

DROP TABLE IF EXISTS `document`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `document` (
  `documentId` int NOT NULL AUTO_INCREMENT,
  `fileName` varchar(255) DEFAULT NULL,
  `fileType` varchar(50) DEFAULT NULL,
  `uploadDate` date DEFAULT NULL,
  `filePath` varchar(255) DEFAULT NULL,
  `clientIfaNumber` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`documentId`),
  KEY `klientIfaNummer` (`clientIfaNumber`),
  CONSTRAINT `document_ibfk_1` FOREIGN KEY (`clientIfaNumber`) REFERENCES `client` (`ifaNumber`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `document`
--

LOCK TABLES `document` WRITE;
/*!40000 ALTER TABLE `document` DISABLE KEYS */;
INSERT INTO `document` VALUES (9,'Max Musterman Personalausweis.jpg','image/jpeg','2024-11-12','C:\\Users\\badrs\\OneDrive\\Desktop\\WIFI\\Prufung\\CP_Project\\uploads\\Max Musterman Personalausweis.jpg','123460'),(10,'Max Musterman Personalausweis.jpg','image/jpeg','2024-11-12','C:\\Users\\badrs\\OneDrive\\Desktop\\WIFI\\Prufung\\CP_Project\\uploads\\Max Musterman Personalausweis.jpg','123456');
/*!40000 ALTER TABLE `document` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `documentation`
--

DROP TABLE IF EXISTS `documentation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `documentation` (
  `documentationId` int NOT NULL AUTO_INCREMENT,
  `date` date DEFAULT NULL,
  `time` time DEFAULT NULL,
  `description` text,
  `clientIfaNumber` varchar(50) DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`documentationId`),
  KEY `klientIfaNummer` (`clientIfaNumber`),
  CONSTRAINT `documentation_ibfk_1` FOREIGN KEY (`clientIfaNumber`) REFERENCES `client` (`ifaNumber`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `documentation`
--

LOCK TABLES `documentation` WRITE;
/*!40000 ALTER TABLE `documentation` DISABLE KEYS */;
INSERT INTO `documentation` VALUES (13,'2024-11-12','12:00:00','1. Zweck des Besuchs:\nDer Besuch wurde durchgeführt, um [kurze Beschreibung des Zwecks, z.B. die allgemeine Situation des Klienten zu überprüfen, eine geplante Unterstützung zu leisten, den Gesundheitszustand zu bewerten oder Dokumente zu überreichen].\n\n2. Beschreibung des Besuchs:\nDer Klient wurde in seiner [Wohnung/Zuhause/etc.] besucht. Die Begrüßung war [freundlich/neutrale/widerwillig], und die Atmosphäre während des Gesprächs war [ruhig/angespannt/offen]. Der Klient zeigte [Verhaltensbeschreibung, z.B. Anzeichen von Erschöpfung, Freude über den Besuch oder Besorgnis].\n\n3. Besprochene Themen:\n\nAktueller Gesundheitszustand: Der Klient berichtete über [Beschreibung des Gesundheitszustands, z.B. anhaltende Schmerzen, positive Veränderungen, neue Beschwerden].\nLebensumstände: Der Klient erwähnte [Beschreibung der aktuellen Lebenssituation, z.B. Wohnsituation, soziale Kontakte, Unterstützung durch Familie oder Freunde].\nBedarfe und Anliegen: Der Klient äußerte [besondere Wünsche oder Anliegen, z.B. Hilfe bei Behördengängen, Bedarf an Pflegeleistungen, Unterstützung bei der Haushaltsführung].\nMaßnahmen zur Unterstützung: Es wurde besprochen, dass [Maßnahmen, z.B. regelmäßige Besuche, Kontaktaufnahme mit medizinischen Fachkräften, Bereitstellung von Informationsmaterial] unternommen werden.\n4. Beobachtungen des Betreuers:\nDer Betreuer stellte fest, dass der Klient [körperliche oder emotionale Beobachtungen, z.B. in gutem Zustand, Anzeichen von Stress oder Unsicherheit] war. Der allgemeine Zustand des Klienten schien [adäquat, verbessert, verschlechtert].','123456','Dokumentation des Besuchs beim Klienten');
/*!40000 ALTER TABLE `documentation` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-11-22 21:12:50
