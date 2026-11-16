USE aulajava;
GO

IF OBJECT_ID('tbfuncs',  'U') IS NOT NULL DROP TABLE tbfuncs;
IF OBJECT_ID('tbcargos', 'U') IS NOT NULL DROP TABLE tbcargos;
GO

CREATE TABLE tbcargos (
    cd_cargo  INT          NOT NULL PRIMARY KEY,
    ds_cargo  VARCHAR(50)  NOT NULL
);
GO

CREATE TABLE tbfuncs (
    cod_func  INT           NOT NULL PRIMARY KEY IDENTITY(1,1),
    nome_func VARCHAR(80)   NOT NULL,
    sal_func  DECIMAL(10,2) NOT NULL,
    cod_cargo INT           NOT NULL,
    CONSTRAINT fk_cargo FOREIGN KEY (cod_cargo) REFERENCES tbcargos(cd_cargo)
);
GO

INSERT INTO tbcargos (cd_cargo, ds_cargo) VALUES
(1, 'Analista de Sistemas'),
(2, 'Desenvolvedor Java'),
(3, 'DBA'),
(4, 'Gerente de TI'),
(5, 'Suporte Tecnico');
GO

INSERT INTO tbfuncs (nome_func, sal_func, cod_cargo) VALUES
('Ana Paula Silva',   6500.00, 1),
('Bruno Lima',        4800.00, 2),
('Carlos Eduardo',    7200.00, 3),
('Daniela Souza',     9000.00, 4),
('Eduardo Martins',   3500.00, 5),
('Fernanda Costa',    6800.00, 1),
('Gabriel Oliveira',  5100.00, 2),
('Helena Ferreira',   4300.00, 5),
('Igor Rodrigues',    7800.00, 3),
('Juliana Pereira',   8500.00, 4);
GO

SELECT f.nome_func, f.sal_func, c.ds_cargo
FROM tbfuncs f
JOIN tbcargos c ON f.cod_cargo = c.cd_cargo
ORDER BY f.nome_func;
GO
