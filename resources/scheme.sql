BEGIN WORK;
SET TRANSACTION READ WRITE;

SET datestyle = YMD;

-- Esborra taules si existien
DROP TABLE Region CASCADE;
DROP TABLE Campeon CASCADE;
DROP TABLE Habilidad CASCADE;

-- Creació de taules
CREATE TABLE Region (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(255),
    descripcion TEXT,
    historiasRelacionada INT
);

CREATE TABLE Campeon (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(255),
    apodo VARCHAR(255),
    campeonesConRelacion INT,
    biografia TEXT,
    aparicionEnCinematicas BOOLEAN,
    numRelatosCortos INT,
    rol VARCHAR(255),
    raza VARCHAR(255),
    region VARCHAR(255),
    numDeAspectos INT,
    dificultad VARCHAR(255),
    region_id INT,
    FOREIGN KEY (region_id) REFERENCES Region(id)
);

CREATE TABLE Habilidad (
    id SERIAL PRIMARY KEY,
    campeon VARCHAR(255),
    nombre VARCHAR(255),
    pasiva BOOLEAN,
    asignacionDeTecla CHAR,
    descripcion TEXT,
    linkVideo VARCHAR(255),
    campeon_id INT,
    FOREIGN KEY (campeon_id) REFERENCES Campeon(id)
);
