create table service_capability_requirements (
    id uuid primary key,
    service_code varchar(80) not null,
    service_version integer not null check (service_version > 0),
    capability_code varchar(100) not null,
    active boolean not null,
    updated_at timestamp with time zone not null,
    updated_by varchar(120) not null,
    row_version bigint not null default 0
);
create index ix_capability_lookup on service_capability_requirements
    (service_code, service_version, active);
create index ix_service_capability_history on service_capability_requirements
    (service_code, service_version, capability_code, active);
