CREATE TABLE IF NOT EXISTS public.audit_document_files
(
    audit_id integer,
    id serial,
    file_size bigint,
    document_file_type character varying(255) COLLATE pg_catalog."default" NOT NULL,
    content_type character varying(255) COLLATE pg_catalog."default" NOT NULL,
    name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    original_file_name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    content oid,
    CONSTRAINT accommodation_files_pkey PRIMARY KEY (id),
    CONSTRAINT fk8mco7f82tghe2ydnw83j8rhmq FOREIGN KEY (audit_id)
        REFERENCES public.audits (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.finding_document_files
(
    finding_id integer,
    id serial,
    file_size bigint,
    document_file_type character varying(255) COLLATE pg_catalog."default" NOT NULL,
    content_type character varying(255) COLLATE pg_catalog."default" NOT NULL,
    name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    original_file_name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    content oid,
    CONSTRAINT corrective_actions_files_pkey PRIMARY KEY (id),
    CONSTRAINT fk1la8lrliiyvkfydwhklhm7e40 FOREIGN KEY (finding_id)
        REFERENCES public.findings (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

