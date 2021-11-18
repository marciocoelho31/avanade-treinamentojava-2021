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