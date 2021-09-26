import { rest } from 'msw';
import { endpoints } from '@/test-lib';

const certificates = [
  {
    id: 0,
    file: {
      fileLocation: 'https://link.to.static.file',
      href: 'https://sel2-N.ugent.be/api/files/0',
    },
    created: '2021-05-10T13:51:38.177Z',
    lastUpdated: '2021-05-10T13:51:38.177Z',
    organisationId: 2,
    proposal: {
      id: 0,
      name: 'saus voor in de stoemp',
      status: 'accepted',
      deadline: '2021-05-08',
      organisationId: 2,
    },
  },
];

export default [
  rest.get(endpoints.certificates.href, (req, res, ctx) => {
    const skip = parseInt(req.url.searchParams.get('skip'));
    const limit = parseInt(req.url.searchParams.get('limit'));
    const organisationId = parseInt(req.url.searchParams.get('organisationId'));
    let certs = (skip && limit) ? certificates.slice(skip, skip + limit) : certificates;
    if (organisationId) {
      certs = certs.filter((cert) => cert.organisationId === organisationId);
    }
    certs.forEach((cert) => {
      delete cert.organisationId;
    });
    return res(
      ctx.status(200),
      ctx.json({
        _embedded: {
          certificateList: certs,
          count: certs.length,
          total: certificates.length,
        },
        _links: {
          self: {
            href: endpoints.certificates.href,
          },
        },
      }),
    );
  }),
];
