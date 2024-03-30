create table portfolio
(
    id binary (16) not null
        primary key,
    expire_at        datetime(6),
    created_at       datetime(6),
    last_modified_at  datetime(6)
) engine = innodb
  default charset = utf8mb4;

create table portfolio_stock
(
    id binary (16) not null
        primary key,
    portfolio_id     binary (16) not null,
    stock_id         binary (16) not null,
    shares           integer not null,
    created_at       datetime(6),
    last_modified_at  datetime(6)
) engine = innodb
  default charset = utf8mb4;