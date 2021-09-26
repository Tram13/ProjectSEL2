/* eslint no-undef: 0, no-await-in-loop: 0 */
import '@testing-library/jest-dom';
import { render, waitFor, fireEvent } from '@testing-library/vue';
import Proposal from '@/views/proposal/Proposal.vue';
import { setupServer } from 'msw/node';
import {
  authUser, buildStore, buildTable, checkTable, table2string,
} from '@/test-lib.js';

// mws handlers
import proposalHandlers from '@/mocks/proposalhandler';

const server = setupServer(...proposalHandlers);

beforeAll(() => server.listen());
afterAll(() => server.close());
afterEach(() => {
  server.resetHandlers();
});

test('proposals page (admin)', async () => {
  const store = buildStore({ user: authUser.admin });
  const {
    findByText,
    getByRole,
    getAllByRole,
  } = render(Proposal, {
    global: {
      plugins: [store],
    },
  });
  expect(await findByText('Aanvraag')).toBeInTheDocument();
  // wait for loading screen to show
  await waitFor(() => {
    expect(store.state.proposalstore.proposals.loading).toBe(true);
  });
  // wait for request to be handled
  await waitFor(() => {
    expect(store.state.proposalstore.proposals.loading).toBe(false);
  });
  let table = buildTable(getByRole('table'));
  try {
    // check if the table contains valid entries
    expect(checkTable(table, 'proposal')).toBe(true);
  } catch (err) {
    err.message = `invalid table entries:\n${table2string(table)}`;
    throw err;
  }
  // completed only
  let button = getByRole('button', { name: /^ingediend$/i });
  await fireEvent.click(button);
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
    expect(checkTable(table, 'completed')).toBe(true);
  } catch (err) {
    err.message = `invalid table entries:\n${table2string(table)}`;
    throw err;
  }
  // in_review only
  button = getByRole('button', { name: /^in review$/i });
  await fireEvent.click(button);
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
    expect(checkTable(table, 'inReview')).toBe(true);
  } catch (err) {
    err.message = `invalid table entries:\n${table2string(table)}`;
    throw err;
  }
  // accepted only
  button = getByRole('button', { name: /^geaccepteerd$/i });
  await fireEvent.click(button);
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
    expect(checkTable(table, 'accepted')).toBe(true);
  } catch (err) {
    err.message = `invalid table entries:\n${table2string(table)}`;
    throw err;
  }
  // denied only
  button = getByRole('button', { name: /^geweigerd$/i });
  await fireEvent.click(button);
  // wait for request to be mocked
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
    expect(checkTable(table, 'denied')).toBe(true);
  } catch (err) {
    err.message = `invalid table entries:\n${table2string(table)}`;
    throw err;
  }
  // all proposals
  button = getByRole('button', { name: /^alle$/i });
  await fireEvent.click(button);
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
});
