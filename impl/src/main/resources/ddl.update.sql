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
        CDISC_Submission_Value varchar(255) not null,
        CDISC_Synonym varchar(255) not null,
        extensible varchar(255) not null,
        data_type varchar(255) not null,
        description varchar(4096) not null,
        ext_code_id varchar(255) not null,
        name varchar(255) not null,
        oid varchar(255) not null,
        Preferred_Term varchar(255) not null,
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
        
    CREATE UNIQUE INDEX code_list_oid_idx ON CODE_LIST (oid, META_DATA_VERSION_ID);

	CREATE UNIQUE INDEX enumerated_item_coded_value_idx ON ENUMERATED_ITEM (coded_value, CODE_LIST_ID);

	
	create table SDTM_VERSION (
        ID  bigserial not null,
        CREATOR varchar(255) not null,
        DATE_ADDED timestamp not null,
        DATE_LAST_MODIFIED timestamp not null,
        STATUS varchar(32) not null,
        UPDATED_BY varchar(255) not null,
        define_version varchar(255) not null,
        description varchar(4096),
        name varchar(255) not null,
        oid varchar(255) not null,
        standard_name varchar(255) not null,
        standard_version varchar(255) not null,
        control_terminology_id int8 not null,
        primary key (ID)
    );
    
    alter table SDTM_VERSION 
        add constraint UK_td8agsah4xwrpu8iew4n5n7q4 unique (oid, control_terminology_id);
        
    alter table SDTM_VERSION 
        add constraint FK_ggx4a78pbuwi7sqxu3ye9qu8q 
        foreign key (control_terminology_id) 
        references CONTROL_TERMINOLOGY;
        
    
    create table SDTM_DOMAIN (
        ID  bigserial not null,
        CREATOR varchar(255) not null,
        DATE_ADDED timestamp not null,
        DATE_LAST_MODIFIED timestamp not null,
        STATUS varchar(32) not null,
        UPDATED_BY varchar(255) not null,
        def_class varchar(255) not null,
        description varchar(4096),
        domain varchar(255) not null,
        name varchar(255) not null,
        oid varchar(255) not null,
        purpose varchar(255) not null,
        repeating varchar(255) not null,
        structure varchar(255) not null,
        SDTM_VERSION_ID int8 not null,
        primary key (ID)
    );
  
      alter table SDTM_DOMAIN 
        add constraint UK_qvx6n707wfx7tef4fdf1wsc1m unique (oid,SDTM_VERSION_ID);
 
     alter table SDTM_DOMAIN 
        add constraint FK_232fv17351d4xwgwka66jmmjv 
        foreign key (SDTM_VERSION_ID) 
        references SDTM_VERSION;
 
     create table SDTM_VARIABLE (
        ID  bigserial not null,
        CREATOR varchar(255) not null,
        DATE_ADDED timestamp not null,
        DATE_LAST_MODIFIED timestamp not null,
        STATUS varchar(32) not null,
        UPDATED_BY varchar(255) not null,
        data_type varchar(32),
        sas_data_type varchar(32) not null,
        label varchar(255) not null,
        description varchar(4096),
        length int4,
        name varchar(255) not null,
        oid varchar(255) not null,
        code_list_id int8,
        customized_code_list_id int8,
        SDTM_DOMAIN_ID int8 not null,
        primary key (ID)
    );

    create table SDTM_VARIABLE_ENUM_ITEM_XREF (
        SDTM_VARIABLE_ID int8 not null,
        ENUMERATED_ITEM_ID int8 not null
    );

    create table SDTM_VARIABLE_REF (
        ID  bigserial not null,
        CREATOR varchar(255) not null,
        DATE_ADDED timestamp not null,
        DATE_LAST_MODIFIED timestamp not null,
        STATUS varchar(32) not null,
        UPDATED_BY varchar(255) not null,
        core varchar(32),
        mandatory varchar(32) not null,
        order_number int4 not null,
        role varchar(255),
        type int4 not null default 0,
        SDTM_DOMAIN_ID int8 not null,
        SDTM_VARIABLE_ID int8 not null,
        primary key (ID)
    );
    create table SDTM_PROJECT (
        ID  bigserial not null,
        CREATOR varchar(255) not null,
        DATE_ADDED timestamp not null,
        DATE_LAST_MODIFIED timestamp not null,
        STATUS varchar(32) not null,
        UPDATED_BY varchar(255) not null,
        description varchar(4096),
        name varchar(255) not null,
        SDTM_VERSION_ID int8 not null,
        primary key (ID)
    );

    create table SDTM_PROJECT_VARIABLE_XREF (
        ID  bigserial not null,
        CREATOR varchar(255) not null,
        DATE_ADDED timestamp not null,
        DATE_LAST_MODIFIED timestamp not null,
        STATUS varchar(32) not null,
        UPDATED_BY varchar(255) not null,
        core varchar(32),
        order_number int4 not null,
        role varchar(255),
        SDTM_PROJECT_ID int8 not null,
        SDTM_VARIABLE_ID int8 not null,
        primary key (ID)
    );    
    alter table SDTM_VARIABLE 
        add constraint FK_4lr68uya05iv6w0w93uxyj5nm 
        foreign key (code_list_id) 
        references CODE_LIST;

    alter table SDTM_VARIABLE 
        add constraint FK_2i9618krdkgl8db4mmojw52bt 
        foreign key (customized_code_list_id) 
        references CUSTOMIZED_CODE_LIST;

    alter table SDTM_VARIABLE 
        add constraint FK_80583afj8djw8mqxswwfdidp0 
        foreign key (SDTM_DOMAIN_ID) 
        references SDTM_DOMAIN;

    alter table SDTM_VARIABLE_ENUM_ITEM_XREF 
        add constraint FK_g6sv789vumlh3kqvvo8puxjsg 
        foreign key (ENUMERATED_ITEM_ID) 
        references ENUMERATED_ITEM;

    alter table SDTM_VARIABLE_ENUM_ITEM_XREF 
        add constraint FK_lcawnnlwory071a8ppnxmkl36 
        foreign key (SDTM_VARIABLE_ID) 
        references SDTM_VARIABLE;

    alter table SDTM_VARIABLE_REF 
        add constraint FK_9vw9b58ieph30etyfrtr87xma 
        foreign key (SDTM_DOMAIN_ID) 
        references SDTM_DOMAIN;

    alter table SDTM_VARIABLE_REF 
        add constraint FK_1p6u9nqfpm5gjc0980kas9f92 
        foreign key (SDTM_VARIABLE_ID) 
        references SDTM_VARIABLE;
    
    CREATE UNIQUE INDEX SDTM_VARIABLE_REF_OID_IDX ON SDTM_VARIABLE_REF (SDTM_DOMAIN_ID, SDTM_VARIABLE_ID);

    CREATE UNIQUE INDEX SDTM_VARIABLE_OID_IDX ON SDTM_VARIABLE (SDTM_DOMAIN_ID, oid);
    
    CREATE UNIQUE INDEX SDTM_VARIABLE_NAME_IDX ON SDTM_VARIABLE (SDTM_DOMAIN_ID, name);

    CREATE UNIQUE INDEX SDTM_PROJECT_VARIABLE_XREF_IDX ON SDTM_PROJECT_VARIABLE_XREF (SDTM_PROJECT_ID, SDTM_VARIABLE_ID);
    
    
    alter table SDTM_PROJECT 
        add constraint FK_bm3rww03plnrpy13inpn7kcv 
        foreign key (SDTM_VERSION_ID) 
        references SDTM_VERSION;

    alter table SDTM_PROJECT_VARIABLE_XREF 
        add constraint FK_8c80nhuocnc9klcohohxjorn3 
        foreign key (SDTM_PROJECT_ID) 
        references SDTM_PROJECT;

    alter table SDTM_PROJECT_VARIABLE_XREF 
        add constraint FK_r3tsmdmls9j81xw1g3ffw9a1y 
        foreign key (SDTM_VARIABLE_ID) 
        references SDTM_VARIABLE;
    
    
    create table SDTM_PROJECT_DOMAIN_XREF (
        ID  bigserial not null,
        CREATOR varchar(255) not null,
        DATE_ADDED timestamp not null,
        DATE_LAST_MODIFIED timestamp not null,
        STATUS varchar(32) not null,
        UPDATED_BY varchar(255) not null,
        mapping_action varchar(4096),
        mapping_rule varchar(4096),
        order_number int4 not null,
        SDTM_DOMAIN_ID int8 not null,
        SDTM_PROJECT_ID int8 not null,
        primary key (ID)
    );
    alter table SDTM_PROJECT_DOMAIN_XREF 
        add constraint FK_egjw4fypx4but6dd58cw1eykr 
        foreign key (SDTM_DOMAIN_ID) 
        references SDTM_DOMAIN;

    alter table SDTM_PROJECT_DOMAIN_XREF 
        add constraint FK_8f9abk9rd3wnkcjk4tfrsmgs7 
        foreign key (SDTM_PROJECT_ID) 
        references SDTM_PROJECT;
    
    create table SDTM_PROJECT_LIBRARY (
        ID  bigserial not null,
        CREATOR varchar(255) not null,
        DATE_ADDED timestamp not null,
        DATE_LAST_MODIFIED timestamp not null,
        STATUS varchar(32) not null,
        UPDATED_BY varchar(255) not null,
        name varchar(24) not null,
        path varchar(4096),
        SDTM_PROJECT_ID int8 not null,
        primary key (ID)
    );

    alter table SDTM_PROJECT_LIBRARY 
        add constraint FK_f417w8gw2l9dvo6pmy4gs67dw 
        foreign key (SDTM_PROJECT_ID) 
        references SDTM_PROJECT;
