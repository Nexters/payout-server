alter table stock
    modify exchange varchar (100) null;

alter table stock
    modify ticker varchar (100) null;

alter table stock
    modify name varchar (255) null;

alter table dividend
    modify dividend double null;

alter table dividend
    modify declaration_date datetime(6) null;

alter table dividend
    modify payment_date datetime(6) null;

alter table dividend
    modify ex_dividend_date datetime(6) null;




