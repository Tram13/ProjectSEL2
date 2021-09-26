/* eslint no-undef: 0, no-await-in-loop: 0 */
import '@testing-library/jest-dom';
import { render, waitFor, fireEvent } from '@testing-library/vue';
import Organisation from '@/views/organisation/Organisation.vue';
import InspectOrganisation from '@/views/organisation/InspectOrganisation.vue';
import { setupServer } from 'msw/node';
import {
  authUser, buildStore, buildTable, checkTable, table2string,
} from '@/test-lib.js';

// mws handlers
import organisationHandlers from '@/mocks/organisationhandler';
import proposalHandlers from '@/mocks/proposalhandler';
import permissionHandlers from '@/mocks/permissionhandler';
import certHandlers from '@/mocks/certhandler';

const server = setupServer(
  ...organisationHandlers,
  ...proposalHandlers,
  ...permissionHandlers,
  ...certHandlers,
);

beforeAll(() => server.listen());
afterAll(() => server.close());
afterEach(() => {
  server.resetHandlers();
});

test('inspect organisation (customer & organisation manager)', async () => {
  const store = buildStore({ user: authUser.customer });
  const {
    findByText,
    getByRole,
  } = render(InspectOrganisation, {
    global: {
      plugins: [store],
    },
  });
  expect(await findByText('Patatten Met Stoemp')).toBeInTheDocument();
  expect(await findByText('0999999999')).toBeInTheDocument();
  expect(await findByText('OVO653581')).toBeInTheDocument();
  expect(await findByText('99999')).toBeInTheDocument();
  expect(await findByText('Moeskopperij')).toBeInTheDocument();
  // wait for request to be mocked
  await waitFor(() => {
    expect(store.state.organisationstore.organisation.loading).toBe(false);
    expect(store.state.organisationstore.organisation.members.loading).toBe(false);
  });
  let table = buildTable(getByRole('table'));
  try {
    // check if the table contains valid entries
    expect(checkTable(table, 'member')).toBe(true);
  } catch (err) {
    err.message = `invalid table entries:\n${table2string(table)}`;
    throw err;
  }
  // proposals
  await fireEvent.click(getByRole('button', { name: /^aanvragen$/i }));
  // wait for loading screen to show
  await waitFor(() => {
    expect(store.state.proposalstore.proposals.loading).toBe(true);
  });
  // wait for request to be handled
  await waitFor(() => {
    expect(store.state.proposalstore.proposals.loading).toBe(false);
  });
  table = buildTable(getByRole('table'));
  try {
    // check if the table contains valid entries
    expect(checkTable(table, 'proposal')).toBe(true);
  } catch (err) {
    err.message = `invalid table entries:\n${table2string(table)}`;
    throw err;
  }
  // contacts
  await fireEvent.click(getByRole('button', { name: /^contacten$/i }));
  // wait for loading screen to show
  await waitFor(() => {
    expect(store.state.contactstore.contacts.loading).toBe(true);
  });
  // wait for request to be handled
  await waitFor(() => {
    expect(store.state.contactstore.contacts.loading).toBe(false);
  });
  table = buildTable(getByRole('table'));
  try {
    // check if the table contains valid entries
    expect(checkTable(table, 'contact')).toBe(true);
  } catch (err) {
    err.message = `invalid table entries:\n${table2string(table)}`;
    throw err;
  }
  // certificates
  await fireEvent.click(getByRole('button', { name: /^certificaten$/i }));
  // wait for loading screen to show
  await waitFor(() => {
    expect(store.state.certstore.certs.loading).toBe(true);
  });
  // wait for request to be handled
  await waitFor(() => {
    expect(store.state.certstore.certs.loading).toBe(false);
  });
  table = buildTable(getByRole('table'));
  try {
    // check if the table contains valid entries
    expect(checkTable(table, 'certificate')).toBe(true);
  } catch (err) {
    err.message = `invalid table entries:\n${table2string(table)}`;
    throw err;
  }
  // permissions
  await fireEvent.click(getByRole('button', { name: /^machtigingen$/i }));
  // wait for loading screen to show
  await waitFor(() => {
    expect(store.state.permissionstore.permissions.loading).toBe(true);
  });
  // wait for request to be handled
  await waitFor(() => {
    expect(store.state.permissionstore.permissions.loading).toBe(false);
  });
  table = buildTable(getByRole('table'));
  try {
    // check if the table contains valid entries
    expect(checkTable(table, 'permission')).toBe(true);
  } catch (err) {
    err.message = `invalid table entries:\n${table2string(table)}`;
    throw err;
  }
  // members
  await fireEvent.click(getByRole('button', { name: /^leden$/i }));
  // wait for loading screen to show
  await waitFor(() => {
    expect(store.state.organisationstore.organisation.members.loading).toBe(true);
  });
  // wait for request to be handled
  await waitFor(() => {
    expect(store.state.organisationstore.organisation.members.loading).toBe(false);
  });
  table = buildTable(getByRole('table'));
  try {
    // check if the table contains valid entries
    expect(checkTable(table, 'member')).toBe(true);
  } catch (err) {
    err.message = `invalid table entries:\n${table2string(table)}`;
    throw err;
  }
});

test('organisations page (admin)', async () => {
  const store = buildStore({ user: authUser.admin });
  const {
    findByText,
    getByRole,
  } = render(Organisation, {
    global: {
      plugins: [store],
    },
  });
  expect(await findByText('Organisatie')).toBeInTheDocument();
  expect(await findByText(/organisatie aanmaken/i)).toBeInTheDocument();
  // wait for loading screen to show
  await waitFor(() => {
    expect(store.state.organisationstore.organisations.loading).toBe(true);
  });
  // wait for request to be handled
  await waitFor(() => {
    expect(store.state.organisationstore.organisations.loading).toBe(false);
  });
  const table = buildTable(getByRole('table'));
  try {
    // check if the table contains valid entries
    expect(checkTable(table, 'organisation')).toBe(true);
  } catch (err) {
    err.message = `invalid table entries:\n${table2string(table)}`;
    throw err;
  }
});
