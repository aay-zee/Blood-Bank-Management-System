CREATE DATABASE BloodBankSystem;
USE BloodBankSystem;
-- CREATE TABLE Admin (
--                         UserID INT AUTO_INCREMENT PRIMARY KEY,
--                         Name VARCHAR(30),
--                         Cnic_A BIGINT(13) NOT NULL,
--                         Email VARCHAR(30),
--                         Password VARCHAR(30),
--                         Contact VARCHAR(20),
--                         Address VARCHAR(60),
--                         UNIQUE(Cnic_A)
-- );
-- CREATE TABLE Donor (
--                        DonorID INT PRIMARY KEY,
--                        Cnic_D BIGINT(13) NOT NULL,
--                        BloodGroup VARCHAR(2),
--                        RhFactor VARCHAR(1),
--                        Name VARCHAR(30),
--                        LastDonation DATE,
--                        Contact VARCHAR(20),
--                        Address VARCHAR(60),
--                        Age INT,
--                        UNIQUE (Cnic_D) -- Assuming CNIC is unique for each donor
-- );
-- CREATE TABLE Recipient (
--                            RecipientID INT AUTO_INCREMENT PRIMARY KEY,
--                            Cnic_R BIGINT(13) NOT NULL,
--                            BloodGroup VARCHAR(5),
--                            RhFactor VARCHAR(1),
--                            Name VARCHAR(30),
--                            Contact VARCHAR(20),
--                            Address VARCHAR(60),
--                            PriorityLevel INT(1),
--                            UNIQUE (Cnic_R)
-- );
-- CREATE TABLE BloodInventory (
--                                 SampleID INT AUTO_INCREMENT PRIMARY KEY,
--                                 BloodGroup VARCHAR(5),
--                                 RhFactor VARCHAR(1),
--                                 Expiration DATE,
--                                 DonorID INT,
--                                 FOREIGN KEY (DonorID) REFERENCES Donor(DonorID)
--                                     ON UPDATE CASCADE
--                                     ON DELETE CASCADE
-- );
CREATE TABLE CrossMatch (
    CrossID INT AUTO_INCREMENT PRIMARY KEY,
    DonorID INT,
    RecipientID INT,
    BloodGroup VARCHAR(2),
    RhFactor VARCHAR(1),
    FOREIGN KEY (DonorID) REFERENCES Donor(DonorID) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (RecipientID) REFERENCES Recipient(RecipientID) ON UPDATE CASCADE ON DELETE CASCADE
);