import { rest } from 'msw';
import { endpoints } from '@/test-lib';

const organisations = [
  {
    id: 0,
    organisationName: 'Isengard',
    kboNumber: '0549763157',
    ovoCode: 'OVO002949',
    nisNumber: '71066',
    serviceProvider: 'Sauron',
    approved: false,
  },
  {
    id: 1,
    organisationName: 'Mordor',
    kboNumber: '1613218954',
    ovoCode: 'OVO638456',
    nisNumber: '41002',
    serviceProvider: 'Sauron',
    approved: false,
  },
  {
    id: 2,
    organisationName: 'Patatten Met Stoemp',
    kboNumber: '0999999999',
    ovoCode: 'OVO653581',
    nisNumber: '99999',
    serviceProvider: 'Moeskopperij',
    approved: true,
  },
];

const organisation = {
  id: 2,
  organisationName: 'Patatten Met Stoemp',
  kboNumber: '0999999999',
  ovoCode: 'OVO653581',
  nisNumber: '99999',
  serviceProvider: 'Moeskopperij',
  approved: true,
  created: '2021-05-06T15:59:30.797Z',
  lastUpdated: '2021-05-06T15:59:30.797Z',
  _links: {
    self: {
      href: 'https://sel2-N.ugent.be/api/organisations/2',
    },
    organisations: {
      href: 'https://sel2-N.ugent.be/api/organisations',
    },
    members: {
      href: 'https://sel2-N.ugent.be/api/organisations/2/members',
    },
  },
};

const members = [
  {
    id: 0,
    role: 'manager',
    user: {
      id: 11,
      firstName: 'Mike',
      lastName: 'Oxlong',
      email: 'mike.oxlong@gmail.com',
      role: 'customer',
    },
  },
  {
    id: 1,
    role: 'member',
    user: {
      id: 5,
      firstName: 'William',
      lastName: 'Wallace',
      email: 'william.wallace@hotmail.com',
      role: 'customer',
    },
  },
  {
    id: 2,
    role: 'member',
    user: {
      id: 1,
      firstName: 'German',
      lastName: 'Bratwurst',
      email: 'german.bratwurst@gmail.com',
      role: 'customer',
      password: 'Kart0ffelk0pf', // NOSONAR
    },
  },
];

const contacts = [
  {
    id: 0,
    firstName: 'Mike',
    lastName: 'Oxlong',
    email: 'mike.oxlong@gmail.com',
    phoneNumber: '053555555',
    created: '2021-05-09T20:24:49.339Z',
    lastUpdated: '2021-05-09T20:24:49.339Z',
    _links: {
      self: {
        href: 'https://sel2-N.ugent.be/api/organisations/2/contacts/0',
      },
      contacts: {
        href: 'https://sel2-N.ugent.be/api/organisations/2/contacts',
      },
    },
  },
  {
    id: 1,
    firstName: 'German',
    lastName: 'Bratwurst',
    email: 'german.bratwurst@gmail.com',
    phoneNumber: '053666666',
    created: '2021-05-09T20:24:49.339Z',
    lastUpdated: '2021-05-09T20:24:49.339Z',
    _links: {
      self: {
        href: 'https://sel2-N.ugent.be/api/organisations/2/contacts/1',
      },
      contacts: {
        href: 'https://sel2-N.ugent.be/api/organisations/2/contacts',
      },
    },
  },
];

const contact = {
  id: 0,
  firstName: 'Mike',
  lastName: 'Oxlong',
  email: 'mike.oxlong@gmail.com',
  phoneNumber: '053555555',
  created: '2021-05-09T20:24:49.339Z',
  lastUpdated: '2021-05-09T20:24:49.339Z',
  _links: {
    self: {
      href: 'https://sel2-N.ugent.be/api/organisations/2/contacts/0',
    },
    contacts: {
      href: 'https://sel2-N.ugent.be/api/organisations/2/contacts',
    },
  },
};

export default [
  rest.get(endpoints.organisations.href, (req, res, ctx) => {
    const skip = parseInt(req.url.searchParams.get('skip'));
    const limit = parseInt(req.url.searchParams.get('limit'));
    const approved = req.url.searchParams.get('approved');
    let orgs = (skip && limit) ? organisations.slice(skip, skip + limit) : organisations;
    if (approved) {
      orgs = orgs.filter((org) => org.approved === (approved === 'true'));
    }
    return res(
      ctx.status(200),
      ctx.json({
        _embedded: {
          organisationList: orgs,
          count: orgs.length,
          total: organisations.length,
        },
        _links: {
          self: {
            href: endpoints.organisations.href,
          },
        },
      }),
    );
  }),
  rest.get(/\/organisations\/[^\/]+$/, (req, res, ctx) => res(
    ctx.status(200),
    ctx.json(organisation),
  )),
  rest.get(/\/organisations\/[^\/]+\/members/, (req, res, ctx) => {
    const skip = parseInt(req.url.searchParams.get('skip'));
    const limit = parseInt(req.url.searchParams.get('limit'));
    const mems = (skip && limit) ? members.slice(skip, skip + limit) : members;
    return res(
      ctx.status(200),
      ctx.json({
        _embedded: {
          memberList: mems,
          count: mems.length,
          total: members.length,
        },
        _links: {
          self: {
            href: 'https://sel2-N.ugent.be/api/organisations/2/members',
          },
        },
      }),
    );
  }),
  rest.get(/\/organisations\/[^\/]+\/contacts$/, (req, res, ctx) => {
    const skip = parseInt(req.url.searchParams.get('skip'));
    const limit = parseInt(req.url.searchParams.get('limit'));
    const cnts = (skip && limit) ? contacts.slice(skip, skip + limit) : contacts;
    return res(
      ctx.status(200),
      ctx.json({
        _embedded: {
          contactList: cnts,
          count: cnts.length,
          total: contacts.length,
        },
        _links: {
          self: {
            href: 'https://sel2-N.ugent.be/api/organisations/2/contacts',
          },
        },
      }),
    );
  }),
  rest.get(/\/organisations\/[^\/]+\/contacts\/0$/, (req, res, ctx) => res(
    ctx.status(200),
    ctx.json(contacts[0]),
  )),
  rest.get(/\/organisations\/[^\/]+\/contacts\/1$/, (req, res, ctx) => res(
    ctx.status(200),
    ctx.json(contacts[1]),
  )),
  rest.get(/\/organisations\/[^\/]+\/contacts\/[^\/]+$/, (req, res, ctx) => res(
    ctx.status(200),
    ctx.json(contact),
  )),
];
