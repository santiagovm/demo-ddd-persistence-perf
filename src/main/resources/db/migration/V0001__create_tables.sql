create table if not exists car_assembly_checklist
(
    id         uuid not null,
    created_on timestamp,
    version    int,
    tasks      jsonb,
    primary key (id)
);
