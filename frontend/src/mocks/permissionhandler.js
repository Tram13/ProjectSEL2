import { rest } from 'msw';
import { endpoints } from '@/test-lib';

const permissions = [
  {
    id: 0,
    name: 'java 11',
    description: 'java 8 met 3 bij opgeteld in de naam',
    code: 'JDK11',
    proof: {
      href: 'https://sel2-N.ugent.be/api/files/0',
    },
    created: '2021-05-10T13:51:38.177Z',
    lastUpdated: '2021-05-10T13:51:38.177Z',
    organisationId: 2,
  },
];

export default [
  rest.get(endpoints.permissions.href, (req, res, ctx) => {
    const skip = parseInt(req.url.searchParams.get('skip'));
    const limit = parseInt(req.url.searchParams.get('limit'));
    const organisationId = parseInt(req.url.searchParams.get('organisationId'));
    let perms = (skip && limit) ? permissions.slice(skip, skip + limit) : permissions;
    if (organisationId) {
      perms = perms.filter((perm) => perm.organisationId === organisationId);
    }
    perms.forEach((perm) => {
      delete perm.organisationId;
    });
    return res(
      ctx.status(200),
      ctx.json({
        _embedded: {
          permissionList: perms,
          count: perms.length,
          total: permissions.length,
        },
        _links: {
          self: {
            href: endpoints.permissions.href,
          },
        },
      }),
    );
  }),
];
