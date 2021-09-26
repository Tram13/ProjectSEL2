/* eslint no-undef: 0, no-await-in-loop: 0 */
import '@testing-library/jest-dom';
import { render, fireEvent, waitFor } from '@testing-library/vue';
import ProposalContact from '@/views/proposal/proposalPages/ProposalContact.vue';
import { setupServer } from 'msw/node';
import {
  authUser, buildStore, buildTable, checkTable, table2string,
} from '@/test-lib';

// mws handlers
import organisationHandlers from '@/mocks/organisationhandler';
import proposalHandlers from '@/mocks/proposalhandler';
import userHandlers from '@/mocks/userhandler';

const proposal = {
  name: 'boontjes voor bij de gebakken patatten',
};

const errorMessages = {
  missing: {
    submitter: /^indiener ontbreekt$/i,
  },
};

const server = setupServer(
  ...organisationHandlers,
  ...proposalHandlers,
  ...userHandlers,
);

beforeAll(() => server.listen());
afterAll(() => server.close());
afterEach(() => {
  server.resetHandlers();
});

test('input validation', async () => {
  const store = buildStore({ user: authUser.customer });
  const {
    getByTestId,
    findByText,
    queryByText,
    getByRole,
    findByRole,
    getAllByRole,
    emitted,
  } = render(ProposalContact, {
    global: {
      plugins: [store],
    },
  });
  store.dispatch('get_account_organisations');
  store.dispatch('fetch_proposal', undefined);
  await waitFor(() => {
    expect(store.state.authenticationstore.selectedOrganisation === null).toBe(false);
  });
  await waitFor(() => {
    expect(store.state.proposalstore.proposalContacts.loading).toBe(true);
  });
  await waitFor(() => {
    expect(store.state.proposalstore.proposalContacts.loading).toBe(false);
  });
  await waitFor(() => {
    // TODO: delete this
    expect(store.state.proposalstore.proposal.value.contacts[0].firstName.length > 0).toBe(true);
  });
  // check if contacts are displayed
  let table = buildTable(getByRole('table'));
  try {
    // check if the table contains valid entries
    expect(checkTable(table, 'contact')).toBe(true);
  } catch (err) {
    err.message = `invalid table entries:\n${table2string(table)}`;
    throw err;
  }
  expect(await queryByText(errorMessages.missing.submitter)).not.toBeInTheDocument();
  const editButton = getByRole('button', { name: /bewerk/i });
  await fireEvent.click(editButton);
  const addContactButton = getByRole('button', { name: /contact toevoegen/i });
  // add a contact
  await fireEvent.click(addContactButton);
  await waitFor(() => {
    expect(store.state.contactstore.contacts.loading).toBe(true);
  });
  await waitFor(() => {
    expect(store.state.contactstore.contacts.loading).toBe(false);
  });
  await fireEvent.click(getAllByRole('button', { name: /^voeg toe$/i })[1]);
  await fireEvent.update(getByTestId('contact-role-select'), 'technical');
  await fireEvent.click(getByRole('button', { name: /^toevoegen$/i }));
  // add same contact with another role
  await fireEvent.click(addContactButton);
  await waitFor(() => {
    expect(store.state.contactstore.contacts.loading).toBe(true);
  });
  await waitFor(() => {
    expect(store.state.contactstore.contacts.loading).toBe(false);
  });
  await fireEvent.click(getAllByRole('button', { name: /^voeg toe$/i })[1]);
  await fireEvent.update(getByTestId('contact-role-select'), 'technical_backup');
  await fireEvent.click(getByRole('button', { name: /^toevoegen$/i }));
  await waitFor(() => {
    expect(store.state.proposalstore.proposalContacts.loading).toBe(false);
  });
  // check if contacts are displayed
  table = buildTable(getByRole('table'));
  try {
    // check if the table contains valid entries
    expect(checkTable(table, 'contact')).toBe(true);
  } catch (err) {
    err.message = `invalid table entries:\n${table2string(table)}`;
    throw err;
  }
  // clear contacts
  const deleteButtons = getAllByRole('button', { name: /^verwijder$/i });
  for (let i = deleteButtons.length - 1; i > 0; i--) {
    await fireEvent.click(deleteButtons[i]);
  }
  expect(await findByText(errorMessages.missing.submitter)).toBeInTheDocument();
  // add contact not as submitter
  await fireEvent.click(addContactButton);
  await waitFor(() => {
    expect(store.state.contactstore.contacts.loading).toBe(true);
  });
  await waitFor(() => {
    expect(store.state.contactstore.contacts.loading).toBe(false);
  });
  await fireEvent.click(getAllByRole('button', { name: /^voeg toe$/i })[0]);
  await fireEvent.update(getByTestId('contact-role-select'), 'technical');
  await fireEvent.click(getByRole('button', { name: /^toevoegen$/i }));
  expect(await findByText(errorMessages.missing.submitter)).toBeInTheDocument();
  // add wrong submitter
  await fireEvent.click(addContactButton);
  await waitFor(() => {
    expect(store.state.contactstore.contacts.loading).toBe(true);
  });
  await waitFor(() => {
    expect(store.state.contactstore.contacts.loading).toBe(false);
  });
  await fireEvent.click(getAllByRole('button', { name: /^voeg toe$/i })[1]);
  await fireEvent.update(getByTestId('contact-role-select'), 'submitter');
  await fireEvent.click(getByRole('button', { name: /^toevoegen$/i }));
  expect(await findByText(errorMessages.missing.submitter)).toBeInTheDocument();
  // add submitter
  await fireEvent.click(addContactButton);
  await waitFor(() => {
    expect(store.state.contactstore.contacts.loading).toBe(true);
  });
  await waitFor(() => {
    expect(store.state.contactstore.contacts.loading).toBe(false);
  });
  await fireEvent.click(getAllByRole('button', { name: /^voeg toe$/i })[0]);
  await fireEvent.update(getByTestId('contact-role-select'), 'submitter');
  expect(await findByRole('button', { name: /^toevoegen$/i })).not.toBeDisabled();
  await fireEvent.click(getByRole('button', { name: /^toevoegen$/i }));
  expect(await queryByText(errorMessages.missing.submitter)).not.toBeInTheDocument();
});

