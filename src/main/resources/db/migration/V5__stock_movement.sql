create table stock_movement (
    id                    uuid        primary key,
    tenant_id             uuid        not null references tenant(id),
    product_id            uuid        not null references product(id),
    type                  varchar(20) not null,
    quantity              integer     not null,
    note                  varchar(500),
    operator_id           uuid        not null references app_user(id),
    cancelled             boolean     not null default false,
    cancelled_movement_id uuid                 references stock_movement(id),
    created_at            timestamptz not null default now()
);

create index idx_stock_movement_tenant_product on stock_movement (tenant_id, product_id);
create index idx_stock_movement_product_created on stock_movement (tenant_id, product_id, created_at desc);