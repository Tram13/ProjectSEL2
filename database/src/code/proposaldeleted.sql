
--
-- TABLE: ProposalDeleted
-- 
--  

CREATE TABLE ProposalDeleted (
  id bigint NOT NULL ,
  name character varying (1792) NOT NULL ,
  status character varying (20) NOT NULL ,
  deadline timestamp with time zone,
  legalDeadline timestamp with time zone,
  tiDeadline timestamp with time zone,
  explanationDeadline character varying (28672),
  businessContext character varying (28672),
  legalContext character varying (28672),
  functionalSetup character varying (28672),
  technicalSetup character varying (28672),
  requiresPersonalData character varying (20),
  purposeRequestedData character varying (28672),
  extensionPreviousProposal character varying (20),
  originalTanNumber character varying (1792),
  originalUri character varying (1792),
  originalLegalContext character varying (28672),
  ftpAccount character varying (1792),
  requestsAreSpread character varying (20),
  estimatedNumberOfRequests character varying (20),
  feedback character varying (114688),
  inspection character varying (20) NOT NULL ,
  architectureVisualizationExplanation character varying (28672),
  architectureVisualizationId bigint,
  organisationId bigint NOT NULL ,
  authorizationPersonalDataId bigint,
  ftp character varying (20) NOT NULL ,
  externIpTest character varying (1792),
  externIpProd character varying (1792),
  externIpRangeTest character varying (1792),
  externIpRangeProd character varying (1792),
  sshKeyId bigint,
  reuseUri character varying (20) NOT NULL ,
  reuseLegalContext character varying (20) NOT NULL ,
  reuseFtpAccount character varying (20) NOT NULL ,
  reusePreviousProposal character varying (20) NOT NULL ,
  cnPersonalData character varying (1792),
  cnRepertorium character varying (1792),
  cnOther character varying (1792),
  magdaMessages character varying (20) NOT NULL ,
  onlineOption character varying (30),
  cooperationAgreementId bigint,
  processingAgreementId bigint,
  peaks bigint,
  created timestamp with time zone NOT NULL ,
  deletedOn timestamp with time zone NOT NULL  DEFAULT NOW()
);

-- 
ALTER TABLE ProposalDeleted ADD CONSTRAINT proposaldeleted_primary_key PRIMARY KEY (id);
