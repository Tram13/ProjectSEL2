import { rest } from 'msw';
import { endpoints } from '@/test-lib';

const proposals = [
  {
    id: 0,
    name: 'saus voor in de stoemp',
    status: 'accepted',
    deadline: '2021-05-08',
    organisationId: 2,
  },
  {
    id: 1,
    name: 'frikandellen',
    status: 'draft',
    deadline: '2021-09-26',
    organisationId: 2,
  },
  {
    id: 2,
    name: 'gebakken patatten',
    status: 'in_review',
    deadline: '2021-12-05',
    organisationId: 2,
  },
  {
    id: 3,
    name: 'Gust zijn zangtalent',
    status: 'denied',
    deadline: '2021-01-17',
    organisationId: 1,
  },
  {
    id: 4,
    name: 'meer frontend tests',
    status: 'completed',
    deadline: '2100-01-01',
    organisationId: 0,
  },
];

const proposal = {
  id: 0,
  name: 'saus voor in de stoemp',
  status: 'draft',
  deadline: '2021-05-08',
  legalDeadline: '2021-05-08',
  businessContext: 'totaal arbitrair',
  legalContext: 'totaal arbitrair',
  functionalSetup: 'totaal arbitrair',
  technicalSetup: 'totaal arbitrair',
  requiresPersonalData: true,
  purposeRequestedData: 'verkopen aan een derde partij voor zotte monnies',
  extensionPreviousProposal: true,
  originalTanNumber: 'string',
  originalUri: 'string',
  originalLegalContext: 'totaal arbitrair',
  ftpAccount: 'totaal arbitrair',
  architectureVisualizationExplanation: 'totaal arbitrair',
  requestsAreSpread: true,
  estimatedNumberOfRequests: '<100K',
  tiDeadline: '2021-05-08',
  explanationDeadline: 'totaal arbitrair',
  feedback: JSON.stringify({
    introduction: '',
    description: '',
    contact: '',
    context: '',
    packages: '',
    services: '',
    functional: '',
    supplement: '',
  }),
  architectureVisualization: {
    href: 'https://sel2-N.ugent.be/api/files/0',
  },
  contacts: [
    {
      contactId: 0,
      role: 'submitter',
      href: 'https://sel2-N.ugent.be/api/organisations/2/contacts/0',
    },
    {
      contactId: 0,
      role: 'submitter',
      href: 'https://sel2-N.ugent.be/api/organisations/2/contacts/0',
    },
  ],
  services: [
    {
      permissions: [
        {
          href: 'https://sel2-N.ugent.be/api/permissions/0',
        },
      ],
      source: 'KSZ',
      deliveryMethods: [
        'FTP',
      ],
      href: 'https://sel2-N.ugent.be/api/services/0',
    },
  ],
  packages: [
    {
      permissions: [
        {
          href: 'https://sel2-N.ugent.be/api/permissions/0',
        },
      ],
      href: 'https://sel2-N.ugent.be/api/packages/0',
    },
  ],
  created: '2021-05-08T14:23:01.806Z',
  lastUpdated: '2021-05-08T14:23:01.806Z',
  _links: {
    self: {
      href: 'https://sel2-N.ugent.be/api/proposals/0',
    },
    organisation: {
      href: 'https://sel2-N.ugent.be/api/organisations/2',
    },
    proposals: {
      href: 'https://sel2-N.ugent.be/api/proposals',
    },
  },
};

export default [
  rest.get(endpoints.proposals.href, (req, res, ctx) => {
    const skip = parseInt(req.url.searchParams.get('skip'));
    const limit = parseInt(req.url.searchParams.get('limit'));
    let status = req.url.searchParams.get('status');
    if (status) {
      status = status.split(', ');
    }
    let props = (skip && limit) ? proposals.slice(skip, skip + limit) : proposals;
    if (status) {
      props = props.filter((prop) => status.includes(prop.status));
    }
    const organisationId = parseInt(req.url.searchParams.get('organisationId'));
    if (organisationId) {
      props = props.filter((prop) => prop.organisationId === organisationId);
    }
    props.forEach((prop) => {
      delete prop.organisationId;
    });
    return res(
      ctx.status(200),
      ctx.json({
        _embedded: {
          proposalList: props,
          count: props.length,
          total: proposals.length,
        },
        _links: {
          self: {
            href: endpoints.proposals.href,
          },
        },
      }),
    );
  }),
  rest.get(/\/proposals\/[^\/]+$/, (req, res, ctx) => res(
    ctx.status(200),
    ctx.json(proposal),
  )),
];
