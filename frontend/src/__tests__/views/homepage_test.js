/* eslint no-undef: 0 */
import '@testing-library/jest-dom';
import { render, waitFor } from '@testing-library/vue';
import UserHome from '@/views/homes/UserHome.vue';
import MagdaHome from '@/views/homes/MagdaHome.vue';
import { setupServer } from 'msw/node';
import { authUser, buildStore, buildTable, checkTable, table2string } from '@/test-lib.js';

// mws handlers
import organisationHandlers from '@/mocks/organisationhandler.js';
import proposalHandlers from '@/mocks/proposalhandler.js';

const server = setupServer(...organisationHandlers, ...proposalHandlers);

beforeAll(() => server.listen());
afterAll(() => server.close());
afterEach(() => {
  server.resetHandlers();
});

test("render user home", async () => {
  const { findByText } = render(UserHome, {
    global: {
      plugins: [buildStore({ user: authUser.customer })],
    },
  });
  expect(await findByText(/home/i)).toBeInTheDocument();
});

test("render MAGDA home", async () => {
  const store = buildStore({ user: authUser.admin });
  const { findByText, queryByText, getAllByRole } = render(MagdaHome, {
    global: {
      plugins: [store],
    },
  });
  expect(await findByText(/home/i)).toBeInTheDocument();
  expect(await findByText(/organisaties/i)).toBeInTheDocument();
  expect(await findByText(/aanvragen/i)).toBeInTheDocument();
  // wait for request to be mocked
  await waitFor(() => {
    expect(store.state.organisationstore.organisations.loading).toBe(false);
    expect(store.state.proposalstore.proposals.loading).toBe(false);
  });
  const [organisationTable, proposalTable, ...tables] = getAllByRole('table').map(
    (_) => buildTable(_)
  );
  try {
    // check if the table contains valid entries
    expect(checkTable(organisationTable, 'organisation')).toBe(true);
  } catch (err) {
    err.message = `invalid table entries:\n${table2string(organisationTable)}`;
    throw err;
  }
  // check if the organisations are yet to be approved
  // TODO: improve reliability
  expect(await findByText('Mordor')).toBeInTheDocument();
  expect(await queryByText('Patatten Met Stoemp')).not.toBeInTheDocument();
  try {
    // check if the table contains valid entries
    expect(checkTable(proposalTable, 'proposal')).toBe(true);
  } catch (err) {
    err.message = `invalid table entries:\n${table2string(proposalTable)}`;
    throw err;
  }
  // check if the proposals are yet to be processed
  expect(proposalTable.every(
    (_) => (/^(ingediend)|(in review)|(wachtend op feedback)$/i).test(_.Status)
  )).toBe(true);
});
