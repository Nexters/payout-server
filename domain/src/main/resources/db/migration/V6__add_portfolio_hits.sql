alter table portfolio
    add hits int not null;

alter table portfolio
    add version bigint not null default 1;