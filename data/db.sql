CREATE table Pedidos (
  codigo         int                        not null
, codigo_cliente int                       not null
, valor_total    numeric(6,2) default 0     not null
, numero_cartao  varchar(16)
, data           date         default now() not null
)
;

alter table Pedidos
  add constraint pk_pedidos_codigo
  primary key(codigo)
;



insert into pedidos values (1, 801, 100, '1234567812345678', now());




CREATE table ItensPedidos (
  codigo         int                       not null
, codigo_produto int                       not null
, quantidade     numeric(6,2) default 0    not null
, nome_produto   varchar(100)
, valor_unitario numeric(6,2) default 0    not null
, valor          numeric(6,2) default 0    not null
)
;

alter table ItensPedidos
  add constraint pk_itenspedidos_codigo
  primary key(codigo)
;

insert into ItensPedidos values (1, 1, 50, 'PASTA DE DENTE', 2.00, 100.00);


CREATE TABLE Clientes (
  codigo         int           not null
, nome           varchar(100)
)
;

alter table Clientes
  add constraint pk_clientes_codigo
  primary key(codigo)
;

insert into Clientes values (803, 'MARCIO COELHO');
insert into Clientes values (567, 'JOAO DA SILVA');
insert into Clientes values (9786, 'CATHERINE COSTA');
insert into Clientes values (1000, 'MARCIA TONDELLO');





CREATE TABLE Log (
  codigo         SERIAL PRIMARY KEY
, data_hora      time  not null
, mensagem       text
)
;
