-- Created by Vertabelo (http://vertabelo.com)
-- Last modification date: 2017-12-28 17:41:22.584

-- tables
-- Table: Batalion
CREATE TABLE Batalion (
    Id_Batalion int NOT NULL,
    NazwaBat char(10) NOT NULL,
    JednostaWojskowa_Nazwa char(15) NOT NULL,
    MagazynBroni_id_magazynBroni int NOT NULL,
    Osoba_Id_Osoba int NOT NULL,
    CONSTRAINT Batalion_pk PRIMARY KEY (Id_Batalion)
);

-- Table: Bron
CREATE TABLE Bron (
    Nr_seryjny int NOT NULL,
    Nazwa char(15) NOT NULL,
    MagazynBroni_id_magazynBroni int NOT NULL,
    CONSTRAINT Bron_pk PRIMARY KEY (Nr_seryjny)
);

-- Table: Druzyna
CREATE TABLE Druzyna (
    Id_Druzyna int NOT NULL,
    NazwaDr char(10) NOT NULL,
    Pluton_Id_Pluton int NOT NULL,
    CONSTRAINT Druzyna_pk PRIMARY KEY (Id_Druzyna)
);

-- Table: JednostaWojskowa
CREATE TABLE JednostaWojskowa (
    NazwaJW char(15) NOT NULL,
    Osoba_Id_Osoba int NOT NULL,
    CONSTRAINT JednostaWojskowa_pk PRIMARY KEY (NazwaJW)
);

-- Table: Kompania
CREATE TABLE Kompania (
    Id_Kompania int NOT NULL,
    NazwaKomp char(10) NOT NULL,
    Batalion_Id_Batalion int NOT NULL,
    Osoba_Id_Osoba int NOT NULL,
    CONSTRAINT Kompania_pk PRIMARY KEY (Id_Kompania)
);

-- Table: Korpus
CREATE TABLE Korpus (
    Nazwa char(15) NOT NULL,
    CONSTRAINT Korpus_pk PRIMARY KEY (Nazwa)
);

-- Table: MagazynBroni
CREATE TABLE MagazynBroni (
    id_magazynBroni int NOT NULL,
    CONSTRAINT MagazynBroni_pk PRIMARY KEY (id_magazynBroni)
);

-- Table: MarkaPojazdu
CREATE TABLE MarkaPojazdu (
    NazwaPoj char(15) NOT NULL,
    CONSTRAINT MarkaPojazdu_pk PRIMARY KEY (NazwaPoj)
);

-- Table: Misja_Osoba
CREATE TABLE Misja_Osoba (
    id_misja_osoba int NOT NULL,
    Misje_Id_Misje int NOT NULL,
    Osoba_Id_Osoba int NOT NULL,
    CONSTRAINT Misja_Osoba_pk PRIMARY KEY (id_misja_osoba)
);

-- Table: Misje
CREATE TABLE Misje (
    Id_Misje int NOT NULL,
    Nazwa char(10) NOT NULL,
    Miejsce char(20) NOT NULL,
    CONSTRAINT Misje_pk PRIMARY KEY (Id_Misje)
);

-- Table: Opinia_sluzbowa
CREATE TABLE Opinia_sluzbowa (
    Ocena int NOT NULL,
    CONSTRAINT Opinia_sluzbowa_pk PRIMARY KEY (Ocena)
);

-- Table: Os_Wyj_Sluz
CREATE TABLE Os_Wyj_Sluz (
    id_Os_WyjSluzb int NOT NULL,
    Wy_Sluzb_Numer int NOT NULL,
    Osoba_Id_Osoba int NOT NULL,
    CONSTRAINT Os_Wyj_Sluz_pk PRIMARY KEY (id_Os_WyjSluzb)
);

-- Table: Osoba
CREATE TABLE Osoba (
    Id_Osoba int NOT NULL,
    Imie char(15) NOT NULL,
    Nazwisko char(20) NOT NULL,
    StopienWojskowy_Nazwa char(10) NOT NULL,
    Bron_Nr_seryjny int NOT NULL,
    Opinia_sluzbowa_Ocena int NOT NULL,
    id_Przelozony int NULL,
    Druzyna_Id_Druzyna int NOT NULL,
    Stan_Wyp_id_stanWyp int NOT NULL,
    CONSTRAINT Osoba_pk PRIMARY KEY (Id_Osoba)
);

-- Table: Pluton
CREATE TABLE Pluton (
    Id_Pluton int NOT NULL,
    NazwaPlut char(10) NOT NULL,
    Kompania_Id_Kompania int NOT NULL,
    Osoba_Id_Osoba int NOT NULL,
    CONSTRAINT Pluton_pk PRIMARY KEY (Id_Pluton)
);

-- Table: Pojazd
CREATE TABLE Pojazd (
    Nr_Rej int NOT NULL,
    Model char(15) NOT NULL,
    Przeglad date NOT NULL,
    Osoba_Id_Osoba int NOT NULL,
    MarkaPojazdu_NazwaPoj char(15) NOT NULL,
    CONSTRAINT Pojazd_pk PRIMARY KEY (Nr_Rej)
);

-- Table: Stan_Wyp
CREATE TABLE Stan_Wyp (
    id_stanWyp int NOT NULL,
    Stanowisko_Id_stanowisko int NOT NULL,
    Wyposazenie_Id_Wyposazenie int NOT NULL,
    CONSTRAINT Stan_Wyp_pk PRIMARY KEY (id_stanWyp)
);

-- Table: Stanowisko
CREATE TABLE Stanowisko (
    Id_stanowisko int NOT NULL,
    Nazwa char(20) NOT NULL,
    CONSTRAINT Stanowisko_pk PRIMARY KEY (Id_stanowisko)
);

-- Table: StopienWojskowy
CREATE TABLE StopienWojskowy (
    Nazwa char(10) NOT NULL,
    Korpus_Nazwa char(15) NOT NULL,
    CONSTRAINT StopienWojskowy_pk PRIMARY KEY (Nazwa)
);

-- Table: Uwagi
CREATE TABLE Uwagi (
    Id_uwagi int NOT NULL,
    Nazwa char(20) NOT NULL,
    Data date NOT NULL,
    Opis char(50) NOT NULL,
    Os_Wyj_Sluz_id_Os_WyjSluzb int NOT NULL,
    CONSTRAINT Uwagi_pk PRIMARY KEY (Id_uwagi)
);

-- Table: Wy_Sluzb
CREATE TABLE Wy_Sluzb (
    Numer int NOT NULL,
    Data_Poczatek date NOT NULL,
    Data_Koniec date NOT NULL,
    Pojazd_Nr_Rej int NOT NULL,
    CONSTRAINT Wy_Sluzb_pk PRIMARY KEY (Numer)
);

-- Table: Wyposazenie
CREATE TABLE Wyposazenie (
    Id_Wyposazenie int NOT NULL,
    Nazwa char(20) NOT NULL,
    CONSTRAINT Wyposazenie_pk PRIMARY KEY (Id_Wyposazenie)
);

-- foreign keys
-- Reference: Batalion_JednostaWojskowa (table: Batalion)
ALTER TABLE Batalion ADD CONSTRAINT Batalion_JednostaWojskowa FOREIGN KEY Batalion_JednostaWojskowa (JednostaWojskowa_Nazwa)
    REFERENCES JednostaWojskowa (NazwaJW);

-- Reference: Batalion_MagazynBroni (table: Batalion)
ALTER TABLE Batalion ADD CONSTRAINT Batalion_MagazynBroni FOREIGN KEY Batalion_MagazynBroni (MagazynBroni_id_magazynBroni)
    REFERENCES MagazynBroni (id_magazynBroni);

-- Reference: Batalion_Osoba (table: Batalion)
ALTER TABLE Batalion ADD CONSTRAINT Batalion_Osoba FOREIGN KEY Batalion_Osoba (Osoba_Id_Osoba)
    REFERENCES Osoba (Id_Osoba);

-- Reference: Bron_MagazynBroni (table: Bron)
ALTER TABLE Bron ADD CONSTRAINT Bron_MagazynBroni FOREIGN KEY Bron_MagazynBroni (MagazynBroni_id_magazynBroni)
    REFERENCES MagazynBroni (id_magazynBroni);

-- Reference: Druzyna_Pluton (table: Druzyna)
ALTER TABLE Druzyna ADD CONSTRAINT Druzyna_Pluton FOREIGN KEY Druzyna_Pluton (Pluton_Id_Pluton)
    REFERENCES Pluton (Id_Pluton);

-- Reference: JednostaWojskowa_Osoba (table: JednostaWojskowa)
ALTER TABLE JednostaWojskowa ADD CONSTRAINT JednostaWojskowa_Osoba FOREIGN KEY JednostaWojskowa_Osoba (Osoba_Id_Osoba)
    REFERENCES Osoba (Id_Osoba);

-- Reference: Kompania_Batalion (table: Kompania)
ALTER TABLE Kompania ADD CONSTRAINT Kompania_Batalion FOREIGN KEY Kompania_Batalion (Batalion_Id_Batalion)
    REFERENCES Batalion (Id_Batalion);

-- Reference: Kompania_Osoba (table: Kompania)
ALTER TABLE Kompania ADD CONSTRAINT Kompania_Osoba FOREIGN KEY Kompania_Osoba (Osoba_Id_Osoba)
    REFERENCES Osoba (Id_Osoba);

-- Reference: Misja_Osoba_Misje (table: Misja_Osoba)
ALTER TABLE Misja_Osoba ADD CONSTRAINT Misja_Osoba_Misje FOREIGN KEY Misja_Osoba_Misje (Misje_Id_Misje)
    REFERENCES Misje (Id_Misje);

-- Reference: Misja_Osoba_Osoba (table: Misja_Osoba)
ALTER TABLE Misja_Osoba ADD CONSTRAINT Misja_Osoba_Osoba FOREIGN KEY Misja_Osoba_Osoba (Osoba_Id_Osoba)
    REFERENCES Osoba (Id_Osoba);

-- Reference: Os_Wyj_Sluz_Osoba (table: Os_Wyj_Sluz)
ALTER TABLE Os_Wyj_Sluz ADD CONSTRAINT Os_Wyj_Sluz_Osoba FOREIGN KEY Os_Wyj_Sluz_Osoba (Osoba_Id_Osoba)
    REFERENCES Osoba (Id_Osoba);

-- Reference: Os_Wyj_Sluz_Wy_Sluzb (table: Os_Wyj_Sluz)
ALTER TABLE Os_Wyj_Sluz ADD CONSTRAINT Os_Wyj_Sluz_Wy_Sluzb FOREIGN KEY Os_Wyj_Sluz_Wy_Sluzb (Wy_Sluzb_Numer)
    REFERENCES Wy_Sluzb (Numer);

-- Reference: Osoba_Bron (table: Osoba)
ALTER TABLE Osoba ADD CONSTRAINT Osoba_Bron FOREIGN KEY Osoba_Bron (Bron_Nr_seryjny)
    REFERENCES Bron (Nr_seryjny);

-- Reference: Osoba_Druzyna (table: Osoba)
ALTER TABLE Osoba ADD CONSTRAINT Osoba_Druzyna FOREIGN KEY Osoba_Druzyna (Druzyna_Id_Druzyna)
    REFERENCES Druzyna (Id_Druzyna);

-- Reference: Osoba_Opinia_sluzbowa (table: Osoba)
ALTER TABLE Osoba ADD CONSTRAINT Osoba_Opinia_sluzbowa FOREIGN KEY Osoba_Opinia_sluzbowa (Opinia_sluzbowa_Ocena)
    REFERENCES Opinia_sluzbowa (Ocena);

-- Reference: Osoba_Osoba (table: Osoba)
ALTER TABLE Osoba ADD CONSTRAINT Osoba_Osoba FOREIGN KEY Osoba_Osoba (id_Przelozony)
    REFERENCES Osoba (Id_Osoba);

-- Reference: Osoba_Stan_Wyp (table: Osoba)
ALTER TABLE Osoba ADD CONSTRAINT Osoba_Stan_Wyp FOREIGN KEY Osoba_Stan_Wyp (Stan_Wyp_id_stanWyp)
    REFERENCES Stan_Wyp (id_stanWyp);

-- Reference: Osoba_StopienWojskowy (table: Osoba)
ALTER TABLE Osoba ADD CONSTRAINT Osoba_StopienWojskowy FOREIGN KEY Osoba_StopienWojskowy (StopienWojskowy_Nazwa)
    REFERENCES StopienWojskowy (Nazwa);

-- Reference: Pluton_Kompania (table: Pluton)
ALTER TABLE Pluton ADD CONSTRAINT Pluton_Kompania FOREIGN KEY Pluton_Kompania (Kompania_Id_Kompania)
    REFERENCES Kompania (Id_Kompania);

-- Reference: Pluton_Osoba (table: Pluton)
ALTER TABLE Pluton ADD CONSTRAINT Pluton_Osoba FOREIGN KEY Pluton_Osoba (Osoba_Id_Osoba)
    REFERENCES Osoba (Id_Osoba);

-- Reference: Pojazd_MarkaPojazdu (table: Pojazd)
ALTER TABLE Pojazd ADD CONSTRAINT Pojazd_MarkaPojazdu FOREIGN KEY Pojazd_MarkaPojazdu (MarkaPojazdu_NazwaPoj)
    REFERENCES MarkaPojazdu (NazwaPoj);

-- Reference: Pojazd_Osoba (table: Pojazd)
ALTER TABLE Pojazd ADD CONSTRAINT Pojazd_Osoba FOREIGN KEY Pojazd_Osoba (Osoba_Id_Osoba)
    REFERENCES Osoba (Id_Osoba);

-- Reference: Stan_Wyp_Stanowisko (table: Stan_Wyp)
ALTER TABLE Stan_Wyp ADD CONSTRAINT Stan_Wyp_Stanowisko FOREIGN KEY Stan_Wyp_Stanowisko (Stanowisko_Id_stanowisko)
    REFERENCES Stanowisko (Id_stanowisko);

-- Reference: Stan_Wyp_Wyposazenie (table: Stan_Wyp)
ALTER TABLE Stan_Wyp ADD CONSTRAINT Stan_Wyp_Wyposazenie FOREIGN KEY Stan_Wyp_Wyposazenie (Wyposazenie_Id_Wyposazenie)
    REFERENCES Wyposazenie (Id_Wyposazenie);

-- Reference: StopienWojskowy_Korpus (table: StopienWojskowy)
ALTER TABLE StopienWojskowy ADD CONSTRAINT StopienWojskowy_Korpus FOREIGN KEY StopienWojskowy_Korpus (Korpus_Nazwa)
    REFERENCES Korpus (Nazwa);

-- Reference: Uwagi_Os_Wyj_Sluz (table: Uwagi)
ALTER TABLE Uwagi ADD CONSTRAINT Uwagi_Os_Wyj_Sluz FOREIGN KEY Uwagi_Os_Wyj_Sluz (Os_Wyj_Sluz_id_Os_WyjSluzb)
    REFERENCES Os_Wyj_Sluz (id_Os_WyjSluzb);

-- Reference: Wyjazdy_Sluzbowe_Pojazd (table: Wy_Sluzb)
ALTER TABLE Wy_Sluzb ADD CONSTRAINT Wyjazdy_Sluzbowe_Pojazd FOREIGN KEY Wyjazdy_Sluzbowe_Pojazd (Pojazd_Nr_Rej)
    REFERENCES Pojazd (Nr_Rej);

-- End of file.

