/* eslint no-undef: 0, no-await-in-loop: 0 */
import '@testing-library/jest-dom';
import { render, waitFor } from '@testing-library/vue';
import Package from '@/views/package/Package.vue';
import InspectPackage from '@/views/package/InspectPackage.vue';
import { setupServer } from 'msw/node';
import {
  authUser, buildStore, buildTable, checkTable, table2string,
} from '@/test-lib';
import { dateFormat } from '@/helpers/regexhelper';

// mws handlers
import packageHandlers from '@/mocks/packagehandler';

const server = setupServer(...packageHandlers);

beforeAll(() => server.listen());
afterAll(() => server.close());
afterEach(() => {
  server.resetHandlers();
});

test('inspect package (admin)', async () => {
  const store = buildStore({ user: authUser.admin });
  const {
    findByText,
    getAllByText,
    getByRole,
  } = render(InspectPackage, {
    global: {
      plugins: [store],
    },
  });
  expect(await findByText('Basispakket LED')).toBeInTheDocument();
  try {
    expect(getAllByText(dateFormat).length > 0).toBe(true);
  } catch (err) {
    err.message = 'dates not found or wrong date format\n';
    throw err;
  }
  // wait for request to be mocked
  await waitFor(() => {
    expect(store.state.packagestore.package.loading).toBe(false);
  });
  const table = buildTable(getByRole('table'));
  try {
    // check if the table contains valid entries
    expect(checkTable(table, 'serviceVerbose')).toBe(true);
  } catch (err) {
    err.message = `invalid table entries:\n${table2string(table)}`;
    throw err;
  }
});

test('packages page (admin)', async () => {
  const store = buildStore({ user: authUser.admin });
  const {
    findByText,
    getByRole,
  } = render(Package, {
    global: {
      plugins: [store],
    },
  });
  expect(await findByText('Pakket')).toBeInTheDocument();
  expect(await findByText(/pakket aanmaken/i)).toBeInTheDocument();
  // wait for request to be mocked
  await waitFor(() => {
    expect(store.state.packagestore.packages.loading).toBe(false);
  });
  const table = buildTable(getByRole('table'));
  try {
    // check if the table contains valid entries
    expect(checkTable(table, 'package')).toBe(true);
  } catch (err) {
    err.message = `invalid table entries:\n${table2string(table)}`;
    throw err;
  }
});
