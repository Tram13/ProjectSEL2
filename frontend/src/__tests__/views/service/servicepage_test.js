/* eslint no-undef: 0, no-await-in-loop: 0 */
import '@testing-library/jest-dom';
import { render, waitFor } from '@testing-library/vue';
import Service from '@/views/service/Service.vue';
import InspectService from '@/views/service/InspectService.vue';
import { setupServer } from 'msw/node';
import {
  authUser, buildStore, buildTable, checkTable, table2string,
} from '@/test-lib';

// mws handlers
import serviceHandlers from '@/mocks/servicehandler';

const server = setupServer(...serviceHandlers);

beforeAll(() => server.listen());
afterAll(() => server.close());
afterEach(() => {
  server.resetHandlers();
});

test('inspect service (admin)', async () => {
  const store = buildStore({ user: authUser.admin });
  const {
    findByText,
    getByRole,
  } = render(InspectService, {
    global: {
      plugins: [store],
    },
  });
  expect(await findByText('Energie.GeefEPC')).toBeInTheDocument();
  expect(await findByText('Energie')).toBeInTheDocument();
  expect(await findByText(/FAMIFED/)).toBeInTheDocument();
  expect(await findByText(/KBO/)).toBeInTheDocument();
  expect(await findByText(/PUB/)).toBeInTheDocument();
  expect(await findByText(/MO/)).toBeInTheDocument();
  expect(await findByText('Dit document beschrijft het gebruik en de werking van de webservice GeefEpc zoals deze door Agentschap Informatie Vlaanderen op het MAGDA platform aangeboden wordt.')).toBeInTheDocument();
});

test('services page (admin)', async () => {
  const store = buildStore({ user: authUser.admin });
  const {
    findByText,
    getByRole,
  } = render(Service, {
    global: {
      plugins: [store],
    },
  });
  expect(await findByText('Dienst')).toBeInTheDocument();
  expect(await findByText(/dienst aanmaken/i)).toBeInTheDocument();
  // wait for request to be mocked
  await waitFor(() => {
    expect(store.state.servicestore.services.loading).toBe(false);
  });
  const table = buildTable(getByRole('table'));
  try {
    // check if the table contains valid entries
    expect(checkTable(table, 'service')).toBe(true);
  } catch (err) {
    err.message = `invalid table entries:\n${table2string(table)}`;
    throw err;
  }
});
