create table category (
    id          uuid         primary key,
    tenant_id   uuid         not null references tenant(id),
    code        integer      not null,
    name        varchar(120) not null,
    active      boolean      not null default true,
    created_at  timestamptz  not null default now(),
    updated_at  timestamptz  not null default now(),
    constraint uq_category_tenant_code unique (tenant_id, code)
);

create unique index uq_category_tenant_name_active on category (tenant_id, lower(name)) where active = true;
create index idx_category_tenant on category (tenant_id);
