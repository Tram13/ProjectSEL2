import { rest } from 'msw';
import { endpoints } from '@/test-lib';

const file = {
  id: 0,
  fileLocation: 'https://link.to.static.file',
  created: '2021-05-10T13:51:38.177Z',
};

export default [
  rest.get(endpoints.files.href, (req, res, ctx) => res(
    ctx.status(200),
    ctx.json(file),
  )),
];
