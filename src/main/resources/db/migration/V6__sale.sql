create table sale (
    id             uuid          primary key,
    tenant_id      uuid          not null references tenant(id),
    code           integer       not null,
    total_amount   numeric(15,2) not null,
    discount       numeric(15,2) not null default 0,
    final_amount   numeric(15,2) not null,
    status         varchar(20)   not null,
    operator_id    uuid          not null references app_user(id),
    cancelled_at   timestamptz,
    created_at     timestamptz   not null default now()
);

create index idx_sale_tenant on sale (tenant_id);
create index idx_sale_tenant_created on sale (tenant_id, created_at desc);

create table sale_item (
    id           uuid          primary key,
    sale_id      uuid          not null references sale(id),
    product_id   uuid          not null references product(id),
    product_name varchar(120)  not null,
    quantity     integer       not null,
    unit_price   numeric(15,2) not null,
    subtotal     numeric(15,2) not null
);

create index idx_sale_item_sale on sale_item (sale_id);

create table sale_payment (
    id              uuid          primary key,
    sale_id         uuid          not null references sale(id),
    payment_method  varchar(20)   not null,
    amount          numeric(15,2) not null,
    amount_tendered numeric(15,2)
);

create index idx_sale_payment_sale on sale_payment (sale_id);
