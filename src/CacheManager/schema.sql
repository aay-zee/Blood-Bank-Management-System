CREATE DATABASE BloodBankSystem;
USE BloodBankSystem;


CREATE TABLE Donor (
    DonorID INT PRIMARY KEY,
    Cnic_D BIGINT,
    BloodGroup VARCHAR(2),
	Cnic_D BIGINT(13) NOT NULL,
    BloodGroup VARCHAR(5),
    RhFactor VARCHAR(10),
    Name VARCHAR(30),
    LastDonation DATE,
    Contact VARCHAR(20),
    Address VARCHAR(60),
    LastDonation DATE,
    Age INT);

    CREATE TABLE BloodInventory (
                    SampleID INT PRIMARY KEY, 
                    BloodGroup VARCHAR(2),
                    RhFactor VARCHAR(10), 
                    Expiration DATE, 
                    DonorID INT, 
                    FOREIGN KEY (DonorID) REFERENCES Donor(DonorID) ON UPDATE CASCADE ON DELETE CASCADE);

CREATE TABLE SignUp (
                UserID INT PRIMARY KEY,
                Name VARCHAR(30),
                Cnic_A BIGINT(13) NOT NULL, 
                Email VARCHAR(30), 
                Password VARCHAR(30), 
                Contact VARCHAR(20), 
                Address VARCHAR(60), 
                UNIQUE(Cnic_A));

CREATE TABLE Recipient (
                    RecipientID INT PRIMARY KEY, 
                    Name VARCHAR(30), 
                    Cnic_R BIGINT(13) NOT NULL, 
                    BloodGroup VARCHAR(5), 
                    Contact VARCHAR(20), 
                    Address VARCHAR(60), 
                    PriorityLevel INT(1), 
                    UNIQUE (Cnic_R));


CREATE TABLE CrossMatch (
                    CrossID INT PRIMARY KEY, 
                    DonorID INT, 
                    RecipientID INT, 
                    BloodGroup VARCHAR(5), 
                    FOREIGN KEY (DonorID) REFERENCES Donor(DonorID) ON UPDATE CASCADE ON DELETE CASCADE, 
                    FOREIGN KEY (RecipientID) REFERENCES Recipient(RecipientID) ON UPDATE CASCADE ON DELETE CASCADE);
