
    alter table CODE_LIST 
        drop constraint if exists FK_27ybb62u2rx0rnkx0dl4er0rd;

    alter table ENUMERATED_ITEM 
        drop constraint if exists FK_7br565mywfik570xp5vkdrn7;

    drop table CODE_LIST cascade;

    drop table CONTROL_TERMINOLOGY cascade;

    drop table ENUMERATED_ITEM cascade;

    drop table META_DATA_VERSION cascade;

    create table CODE_LIST (
        ID  bigserial not null,
        CREATOR varchar(255) not null,
        DATE_ADDED timestamp not null,
        DATE_LAST_MODIFIED timestamp not null,
        STATUS varchar(32) not null,
        UPDATED_BY varchar(255) not null,
        CDISC_Submission_Value varchar(255) not null,
        CDISC_Synonym varchar(255) not null,
        Preferred_Term varchar(255) not null,
        extensible varchar(255) not null,
        data_type varchar(255) not null,
        description varchar(4096) not null,
        ext_code_id varchar(255) not null,
        name varchar(255) not null,
        oid varchar(255) not null,
        META_DATA_VERSION_ID int8 not null,
        primary key (ID)
    );

    create table CONTROL_TERMINOLOGY (
        ID  bigserial not null,
        CREATOR varchar(255) not null,
        DATE_ADDED timestamp not null,
        DATE_LAST_MODIFIED timestamp not null,
        STATUS varchar(32) not null,
        UPDATED_BY varchar(255) not null,
        description varchar(4096) not null,
        name varchar(255) not null,
        primary key (ID)
    );

    create table ENUMERATED_ITEM (
        ID  bigserial not null,
        CREATOR varchar(255) not null,
        DATE_ADDED timestamp not null,
        DATE_LAST_MODIFIED timestamp not null,
        STATUS varchar(32) not null,
        UPDATED_BY varchar(255) not null,
        CDISC_Definition varchar(4096),
        CDISC_Synonym varchar(4096),
        Preferred_Term varchar(255),
        coded_value varchar(255) not null,
        ext_code_id varchar(255) not null,
        CODE_LIST_ID int8 not null,
        primary key (ID)
    );

    create table META_DATA_VERSION (
        ID  bigserial not null,
        CREATOR varchar(255) not null,
        DATE_ADDED timestamp not null,
        DATE_LAST_MODIFIED timestamp not null,
        STATUS varchar(32) not null,
        UPDATED_BY varchar(255) not null,
        description varchar(255) not null,
        name varchar(255) not null,
        oid varchar(255) not null,
        primary key (ID)
    );

    alter table CONTROL_TERMINOLOGY 
        add constraint UK_mfmqnu7x1ulpadxqop7evj8up unique (name);

    alter table META_DATA_VERSION 
        add constraint UK_f97rtb93f3b0jwnqbb6364tip unique (oid);

    alter table CODE_LIST 
        add constraint FK_27ybb62u2rx0rnkx0dl4er0rd 
        foreign key (META_DATA_VERSION_ID) 
        references META_DATA_VERSION;

    alter table ENUMERATED_ITEM 
        add constraint FK_7br565mywfik570xp5vkdrn7 
        foreign key (CODE_LIST_ID) 
        references CODE_LIST;
