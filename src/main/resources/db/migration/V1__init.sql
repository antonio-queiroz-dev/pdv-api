create table tenant (
    id          uuid         primary key,
    name        varchar(120) not null,
    segment     varchar(60),
    plan        varchar(20)  not null default 'FREE',
    active      boolean      not null default true,
    created_at  timestamptz  not null default now(),
    updated_at  timestamptz  not null default now()
);

create table app_user (
    id          uuid         primary key,
    tenant_id   uuid         not null references tenant(id),
    code        integer      not null,
    name        varchar(120) not null,
    email       varchar(160) not null unique,
    password    varchar(120) not null,
    role        varchar(20)  not null,
    active      boolean      not null default true,
    created_at  timestamptz  not null default now(),
    updated_at  timestamptz  not null default now(),
    constraint uq_app_user_tenant_code unique (tenant_id, code)
);

create index idx_app_user_tenant on app_user (tenant_id);
