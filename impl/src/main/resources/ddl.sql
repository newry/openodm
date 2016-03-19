    create table CODE_LIST (
        ID  bigserial not null,
        CREATOR varchar(255) not null,
        DATE_ADDED timestamp not null,
        DATE_LAST_MODIFIED timestamp not null,
        STATUS varchar(32) not null,
        UPDATED_BY varchar(255) not null,
        CDISC_Submission_Value varchar(255) not null,
        CDISC_Synonym varchar(255) not null,
        extensible varchar(255) not null,
        data_type varchar(255) not null,
        description varchar(4096) not null,
        ext_code_id varchar(255) not null,
        name varchar(255) not null,
        oid varchar(255) not null,
        Preferred_Term varchar(255) not null,
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
        description varchar(4096),
        name varchar(255) not null,
        primary key (ID)
    );

    create table CONTROL_TERMINOLOGY_CODE_LIST_XREF (
        CONTROL_TERMINOLOGY_ID int8 not null,
        CODE_LIST_ID int8 not null
    );

    create table CONTROL_TERMINOLOGY_CUST_CODE_LIST_XREF (
        CONTROL_TERMINOLOGY_ID int8 not null,
        CODE_LIST_ID int8 not null
    );

    create table CUSTOMIZED_CODE_LIST (
        ID  bigserial not null,
        CREATOR varchar(255) not null,
        DATE_ADDED timestamp not null,
        DATE_LAST_MODIFIED timestamp not null,
        STATUS varchar(32) not null,
        UPDATED_BY varchar(255) not null,
        CDISC_Submission_Value varchar(255),
        CDISC_Synonym varchar(255),
        extensible varchar(255),
        data_type varchar(255) not null,
        description varchar(4096),
        ext_code_id varchar(255),
        name varchar(255) not null,
        oid varchar(255),
        Preferred_Term varchar(255),
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
    
    create table CUSTOMIZED_ENUMERATED_ITEM (
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
        CUSTOMIZED_CODE_LIST_ID int8 not null,
        primary key (ID)
    );
    

    alter table CONTROL_TERMINOLOGY 
        add constraint UK_mfmqnu7x1ulpadxqop7evj8up unique (name);

    alter table META_DATA_VERSION 
        add constraint UK_f97rtb93f3b0jwnqbb6364tip unique (oid);

    alter table CUSTOMIZED_CODE_LIST 
        add constraint UK_bh6mjs7wd8x5oql9h4axgh66w unique (name);

    alter table CODE_LIST 
        add constraint FK_27ybb62u2rx0rnkx0dl4er0rd 
        foreign key (META_DATA_VERSION_ID) 
        references META_DATA_VERSION;

    alter table CONTROL_TERMINOLOGY_CODE_LIST_XREF 
        add constraint FK_2ubl81ycugnic61ry8odkexr0 
        foreign key (CODE_LIST_ID) 
        references CODE_LIST;

    alter table CONTROL_TERMINOLOGY_CODE_LIST_XREF 
        add constraint FK_f936maicyib34rjl0vn97p8tc 
        foreign key (CONTROL_TERMINOLOGY_ID) 
        references CONTROL_TERMINOLOGY;

    alter table CONTROL_TERMINOLOGY_CUST_CODE_LIST_XREF 
        add constraint FK_ffk5m7bokir8ipng4w4kwwjap 
        foreign key (CODE_LIST_ID) 
        references CODE_LIST;

    alter table CONTROL_TERMINOLOGY_CUST_CODE_LIST_XREF 
        add constraint FK_9lxyv71ejly9574p1gk949wag 
        foreign key (CONTROL_TERMINOLOGY_ID) 
        references CONTROL_TERMINOLOGY;

    alter table ENUMERATED_ITEM 
        add constraint FK_7br565mywfik570xp5vkdrn7 
        foreign key (CODE_LIST_ID) 
        references CODE_LIST;
        
    alter table CUSTOMIZED_ENUMERATED_ITEM 
        add constraint FK_rxc4qjmjm7gk6tt2xnvyiyhrl 
        foreign key (CUSTOMIZED_CODE_LIST_ID) 
        references CUSTOMIZED_CODE_LIST;
        
    CREATE UNIQUE INDEX code_list_oid_idx ON CODE_LIST (oid, META_DATA_VERSION_ID);

	CREATE UNIQUE INDEX enumerated_item_coded_value_idx ON ENUMERATED_ITEM (coded_value, CODE_LIST_ID);

	CREATE UNIQUE INDEX customized_enumerated_item_coded_value_idx ON CUSTOMIZED_ENUMERATED_ITEM (coded_value, CUSTOMIZED_CODE_LIST_ID);
