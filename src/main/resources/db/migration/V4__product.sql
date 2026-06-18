create table product (
    id             uuid           primary key,
    tenant_id      uuid           not null references tenant(id),
    code           integer        not null,
    name           varchar(120)   not null,
    description    varchar(500),
    barcode        varchar(50),
    selling_price  numeric(15, 2) not null check (selling_price > 0),
    cost_price     numeric(15, 2)               check (cost_price    > 0),
    category_id    uuid                         references category(id),
    unit_id        uuid                         references unit(id),
    active         boolean        not null default true,
    created_at     timestamptz    not null default now(),
    updated_at     timestamptz    not null default now(),
    constraint uq_product_tenant_code unique (tenant_id, code)
);

create unique index uq_product_tenant_barcode_active
    on product (tenant_id, barcode)
    where active = true and barcode is not null;

create index idx_product_tenant on product (tenant_id);
create index idx_product_tenant_name on product (tenant_id, lower(name));
