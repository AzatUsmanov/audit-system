CREATE TABLE IF NOT EXISTS public.users
(
    id serial,
    password character varying(255) COLLATE pg_catalog."default" NOT NULL,
    role character varying(255) COLLATE pg_catalog."default" NOT NULL,
    username character varying(255) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT users_username_key UNIQUE (username)
);

CREATE TABLE IF NOT EXISTS public.audits
(
    audit_completion_date date NOT NULL,
    audit_plan_acceptance boolean NOT NULL,
    audit_report_acceptance boolean NOT NULL,
    audit_start_date date NOT NULL,
    id serial,
    contact_details character varying(2100) COLLATE pg_catalog."default" NOT NULL,
    audit_name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    audit_type character varying(255) COLLATE pg_catalog."default" NOT NULL,
    certification_body character varying(255) COLLATE pg_catalog."default" NOT NULL,
    office character varying(255) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT audits_pkey PRIMARY KEY (id),
    CONSTRAINT audits_audit_id_key UNIQUE (audit_name)
);

CREATE TABLE IF NOT EXISTS public.findings
(
    audit_id integer,
    corrective_actions_acceptance date NOT NULL,
    corrective_actions_implementation_deadline date NOT NULL,
    corrective_actions_implementation_factual_date date NOT NULL,
    finding_closure_confirmation date NOT NULL,
    id serial,
    root_cause_analysis_deadline date NOT NULL,
    corrective_actions_implementation_information text COLLATE pg_catalog."default" NOT NULL,
    finding_gradation character varying(255) COLLATE pg_catalog."default" NOT NULL,
    finding_name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    finding_text text COLLATE pg_catalog."default" NOT NULL,
    proposed_corrective_actions text COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT findings_pkey PRIMARY KEY (id),
    CONSTRAINT findings_finding_id_key UNIQUE (finding_name),
    CONSTRAINT fkcvqjggbp2req3ywvqow15cqdg FOREIGN KEY (audit_id)
        REFERENCES public.audits (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);