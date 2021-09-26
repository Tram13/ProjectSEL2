
--
-- TABLE: Proposal
-- 
--  

CREATE TABLE Proposal (
  id bigserial NOT NULL ,
  name character varying (1792) NOT NULL ,
  status character varying (20) NOT NULL  DEFAULT 'draft',
  deadline timestamp with time zone,
  legalDeadline timestamp with time zone,
  tiDeadline timestamp with time zone,
  explanationDeadline character varying (28672),
  businessContext character varying (28672),
  legalContext character varying (28672),
  functionalSetup character varying (28672),
  technicalSetup character varying (28672),
  requiresPersonalData character varying (20) NOT NULL  DEFAULT 'NOT_SET',
  purposeRequestedData character varying (28672),
  extensionPreviousProposal character varying (20) NOT NULL  DEFAULT 'NOT_SET',
  originalTanNumber character varying (1792),
  originalUri character varying (1792),
  originalLegalContext character varying (28672),
  ftpAccount character varying (1792),
  requestsAreSpread character varying (20) NOT NULL  DEFAULT 'NOT_SET',
  estimatedNumberOfRequests character varying (20),
  feedback character varying (114688),
  inspection character varying (20) NOT NULL  DEFAULT 'NOT_SET',
  architectureVisualizationExplanation character varying (28672),
  architectureVisualizationId bigint,
  organisationId bigserial NOT NULL ,
  authorizationPersonalDataId bigint,
  ftp character varying (20) NOT NULL  DEFAULT 'NOT_SET',
  externIpTest character varying (1792),
  externIpProd character varying (1792),
  externIpRangeTest character varying (1792),
  externIpRangeProd character varying (1792),
  sshKeyId bigint,
  reuseUri character varying (20) NOT NULL  DEFAULT 'NOT_SET',
  reuseLegalContext character varying (20) NOT NULL  DEFAULT 'NOT_SET',
  reuseFtpAccount character varying (20) NOT NULL  DEFAULT 'NOT_SET',
  reusePreviousProposal character varying (20) NOT NULL  DEFAULT 'NOT_SET',
  cnPersonalData character varying (1792),
  cnRepertorium character varying (1792),
  cnOther character varying (1792),
  magdaMessages character varying (20) NOT NULL  DEFAULT 'NOT_SET',
  onlineOption character varying (30),
  cooperationAgreementId bigint,
  processingAgreementId bigint,
  peaks bigint,
  created timestamp with time zone NOT NULL  DEFAULT NOW(),
  lastUpdated timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE Proposal ADD CONSTRAINT proposal_primary_key PRIMARY KEY (id);

-- 
ALTER TABLE Proposal ADD CONSTRAINT foreignOrganisation FOREIGN KEY (organisationId) REFERENCES Organisation(id) ON UPDATE CASCADE ON DELETE CASCADE;

-- 
ALTER TABLE Proposal ADD CONSTRAINT foreignFileMeta FOREIGN KEY (architectureVisualizationId) REFERENCES FileMeta(id) ON UPDATE CASCADE ON DELETE CASCADE;
