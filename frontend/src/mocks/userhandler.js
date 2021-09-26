import { rest } from 'msw';
import { endpoints } from '@/test-lib';

const users = [
  {
    id: 0,
    firstName: 'Bilbo',
    lastName: 'Baggins',
    email: 'bilbo.baggins@gmail.com',
    role: 'admin',
    password: '1ring2ruleMall', // NOSONAR
  },
  {
    id: 1,
    firstName: 'German',
    lastName: 'Bratwurst',
    email: 'german.bratwurst@gmail.com',
    role: 'customer',
    password: 'Kart0ffelk0pf', // NOSONAR
  },
  {
    id: 2,
    firstName: 'Peter',
    lastName: 'Selie',
    email: 'peter.selie@gmail.com',
    role: 'employee',
    password: 'Pjotr65465', // NOSONAR
  },
  {
    id: 3,
    firstName: 'Paus',
    lastName: 'Urbanus',
    email: 'paus.urbanus@gmail.com',
    role: 'customer',
    password: '8H_dkL2', // NOSONAR
  },
  {
    id: 4,
    firstName: 'Walter',
    lastName: 'White',
    email: 'walter.white@hotmail.com',
    role: 'customer',
    password: 'IMD1whoknocks', // NOSONAR
  },
  {
    id: 5,
    firstName: 'William',
    lastName: 'Wallace',
    email: 'william.wallace@hotmail.com',
    role: 'customer',
    password: '_FR33D0M', // NOSONAR
  },
  {
    id: 6,
    firstName: 'William',
    lastName: 'Shatner',
    email: 'william.shatner@hotmail.com',
    role: 'customer',
    password: 'am5qsdf98sdv', // NOSONAR
  },
  {
    id: 7,
    firstName: 'William',
    lastName: 'Shakespeare',
    email: 'william.shakespear@hotmail.com',
    role: 'employee',
    password: 'P_sd4qs75m', // NOSONAR
  },
  {
    id: 8,
    firstName: 'Gandalf',
    lastName: 'The Grey',
    email: 'gandalf.grey@hotmail.com',
    role: 'employee',
    password: '6qsd5fqsdf5', // NOSONAR
  },
  {
    id: 9,
    firstName: 'Gandalf',
    lastName: 'The White',
    email: 'gandalf.white@gmail.com',
    role: 'admin',
    password: 'sfdv6sq9_PsPM', // NOSONAR
  },
  {
    id: 10,
    firstName: 'Saruman',
    lastName: 'The White',
    email: 'saruman.white@gmail.com',
    role: 'admin',
    password: 'g7P56_pOm', // NOSONAR
  },
  {
    id: 11,
    firstName: 'Mike',
    lastName: 'Oxlong',
    email: 'mike.oxlong@gmail.com',
    role: 'customer',
    password: 'A1aaaaaa', // NOSONAR
  },
  {
    id: 12,
    firstName: 'Peter',
    lastName: 'File',
    email: 'peter.file@gmail.com',
    role: 'admin',
    password: 'A1aaaaaa', // NOSONAR
  },
];

const user = {
  id: 11,
  firstName: 'Mike',
  lastName: 'Oxlong',
  email: 'mike.oxlong@gmail.com',
  role: 'customer',
  password: 'A1aaaaaa', // NOSONAR
  _links: {
    self: {
      href: 'https://sel2-N.ugent.be/api/users/11',
    },
    users: {
      href: 'https://sel2-N.ugent.be/api/users',
    },
    organisations: {
      href: 'https://sel2-N.ugent.be/api/users/11/organisations',
    },
  },
};

const organisations = [
  {
    id: 0,
    role: 'manager',
    organisation: {
      id: 2,
      organisationName: 'Patatten Met Stoemp',
      kboNumber: '0999999999',
      ovoCode: 'OVO653581',
      nisNumber: '99999',
      serviceProvider: 'Moeskopperij',
      approved: true,
    },
  },
];

export default [
  rest.get(endpoints.users.href, (req, res, ctx) => {
    const skip = parseInt(req.url.searchParams.get('skip'));
    const limit = parseInt(req.url.searchParams.get('limit'));
    const role = req.url.searchParams.get('role');
    let usrs = users;
    if (role) {
      usrs = usrs.filter((_) => _.role === role);
    }
    if (skip && limit) {
      usrs = usrs.slice(skip, skip + limit);
    }
    return res(
      ctx.status(200),
      ctx.json({
        _embedded: {
          userList: usrs,
          count: usrs.length,
          total: users.length,
        },
        _links: {
          self: {
            href: endpoints.users.href,
          },
        },
      }),
    );
  }),
  rest.get(/\/users\/[^\/]+$/, (req, res, ctx) => res(
    ctx.status(200),
    ctx.json(user),
  )),
  rest.get(/\/users\/[^\/]+\/organisations/, (req, res, ctx) => {
    const skip = parseInt(req.url.searchParams.get('skip'));
    const limit = parseInt(req.url.searchParams.get('limit'));
    const orgs = (skip && limit) ? organisations.slice(skip, skip + limit) : organisations;
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
            href: 'https://sel2-N.ugent.be/api/users/11/organisations',
          },
        },
      }),
    );
  }),
];
