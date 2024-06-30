create table if not exists car_assembly_checklist
(
    id         uuid not null,
    created_on timestamp,
    version    int,
    primary key (id)
);

create table if not exists car_assembly_task
(
    id                        uuid not null,
    task_index                int not null,
    description               text,
    completed_on              timestamp,
    completed_by              varchar(100),
    car_assembly_checklist_id uuid,
    version                   int,
    primary key (id)
);
