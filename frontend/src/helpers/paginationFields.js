const UserFilters = [
  {
    text: 'Voornaam',
    name: 'firstName',
    type: 'text',
  },
  {
    text: 'Achternaam',
    name: 'lastName',
    type: 'text',
  },
  {
    text: 'Email',
    name: 'email',
    type: 'text',
  },
  {
    text: 'Rol',
    name: 'role',
    type: 'select',
    options: [
      {
        value: 'customer',
        text: 'customer',
      },
      {
        value: 'admin',
        text: 'admin',
      },
      {
        value: 'employee',
        text: 'employee',
      },
    ],
  },
];

const UserHeaders = [
  { name: 'Voornaam', propName: 'firstName' },
  { name: 'Achternaam', propName: 'lastName' },
  { name: 'Email', propName: 'email' },
  { name: 'Rol', propName: 'role' },
  { name: '', propName: 'links' },
];

const CertHeaders = [
  { name: 'Aanvraag', propName: 'name' },
  { name: 'Aangemaakt', propName: 'created', type: 'date' },
  { name: 'Laatst gewijzigd', propName: 'lastUpdated', type: 'date' },
  { name: 'Download', propName: 'file' },
];

const CertFilters = [
  {
    text: 'Aanvraag',
    name: 'name',
    type: 'text',
  },
  {
    text: 'Aangemaakt',
    name: 'created',
    type: 'date',
  },
  {
    text: 'Laatst gewijzigd',
    name: 'lastUpdated',
    type: 'text',
  },
];

const ProposalFilters = [
  {
    text: 'Naam',
    name: 'name',
    type: 'text',
  },
  {
    text: 'Deadline',
    name: 'deadline',
    type: 'date',
  },
  {
    text: 'Status',
    name: 'status',
    type: 'select',
    options: [
      {
        value: 'draft',
        text: 'draft',
      },
      {
        value: 'cancelled',
        text: 'cancelled',
      },
      {
        value: 'denied',
        text: 'denied',
      },
      {
        value: 'in_review',
        text: 'in_review',
      },
      {
        value: 'pending_feedback',
        text: 'pending_feedback',
      },
    ],
  },
];
const ProposalHeaders = [
  { name: 'Naam', propName: 'name' },
  { name: 'Deadline', propName: 'deadline' },
  { name: 'Status', propName: 'status' },
  { name: '', propName: 'links' },
];

const ServiceFilters = [
  {
    text: 'Naam',
    name: 'name',
    type: 'text',
  },
];

const ServiceHeaders = [
  { name: 'Naam', propName: 'name' },
  { name: '', propName: 'links' },
];

const PermissionFilters = [
  {
    text: 'Naam',
    name: 'name',
    type: 'text',
  },
  {
    text: 'Code',
    name: 'code',
    type: 'text',
  },
  {
    text: 'Link',
    name: 'link',
    type: 'text',
  },
];

const PermissionHeaders = [
  { name: 'Naam', propName: 'name' },
  { name: 'Code', propName: 'code' },
  { name: 'Link', propName: 'link' },
  { name: '', propName: 'links' },
];

const PackageFilters = [];

const PackageHeaders = [
  { name: 'Naam', propName: 'name' },
  { name: '', propName: 'links' },
];

const OrganisationFilters = [
  {
    text: 'Naam',
    name: 'name',
    type: 'text',
  },
  {
    text: 'NIS',
    name: 'nisNumber',
    type: 'text',
  },
  {
    text: 'OVO',
    name: 'ovoCode',
    type: 'text',
  },
  {
    text: 'KBO',
    name: 'kboNumber',
    type: 'text',
  },
];
const OrganisationHeaders = [
  { name: 'Naam', propName: 'organisationName' },
  { name: 'NIS', propName: 'nisNumber' },
  { name: 'OVO', propName: 'ovoCode' },
  { name: 'KBO', propName: 'kboNumber' },
  { name: '', propName: 'links' },
];

const UserOrganisationHeaders = [
  { name: 'Naam', propName: 'organisationName' },
  { name: 'Rol', propName: 'role' },
  { name: 'Status', propName: 'status' },
  { name: '', propName: 'links' },
];

const ContactHeaders = [
  { name: 'Voornaam', propName: 'firstName' },
  { name: 'Achternaam', propName: 'lastName' },
  { name: 'Email', propName: 'email' },
  { name: 'Telefoonnummer', propName: 'phoneNumber' },
  { name: '', propName: 'links' },
];

const ContactFilters = [
  {
    text: 'Voornaam',
    name: 'firstName',
    type: 'text',
  },
  {
    text: 'Achternaam',
    name: 'lastName',
    type: 'text',
  },
  {
    text: 'Email',
    name: 'email',
    type: 'text',
  },
  {
    text: 'Telefoonnummer',
    name: 'phoneNumeber',
    type: 'text',
  },
];

const MemberHeaders = [
  { name: 'Voornaam', propName: 'firstName' },
  { name: 'Achternaam', propName: 'lastName' },
  { name: 'Email', propName: 'email' },
  { name: 'Rol', propName: 'role' },
  { name: '', propName: 'links' },
];

const MemberFilters = [
  {
    text: 'Voornaam',
    name: 'firstName',
    type: 'text',
  },
  {
    text: 'Achternaam',
    name: 'lastName',
    type: 'text',
  },
  {
    text: 'Email',
    name: 'email',
    type: 'text',
  },
  {
    text: 'Rol',
    name: 'role',
    type: 'select',
    options: [
      {
        value: 'Manager',
        text: 'manager',
      },
      {
        value: 'Lid',
        text: 'member',
      },
    ],
  },
];

export {
  UserFilters,
  UserHeaders,
  CertHeaders,
  CertFilters,
  ProposalFilters,
  ProposalHeaders,
  ServiceFilters,
  ServiceHeaders,
  PermissionFilters,
  PermissionHeaders,
  PackageFilters,
  PackageHeaders,
  OrganisationFilters,
  OrganisationHeaders,
  UserOrganisationHeaders,
  ContactHeaders,
  MemberHeaders,
  ContactFilters,
  MemberFilters,
};
