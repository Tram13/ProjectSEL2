/* eslint no-undef: 0, no-await-in-loop: 0 */
import '@testing-library/jest-dom';
import { render, fireEvent, waitFor } from '@testing-library/vue';
import ProposalForm from '@/components/proposal/ProposalNew.vue';
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
  phoneNumber: '053111111',
};

const errorMessages = {
  missing: {
    name: /naam ontbreekt/i,
    phoneNumber: /telefoonnummer ontbreekt/i,
    organisation: /selecteer.+organisatie/i,
  },
  invalid: {
    organisation: /organisatie.+niet goedgekeurd/i,
    phoneNumber: /ongeldig telefoonnummer/i,
  },
};

const events = {
  submit: 'proposal_form_close',
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
  } = render(ProposalForm, {
    global: {
      plugins: [store],
    },
  });
  expect(await findByText(errorMessages.missing.organisation));
  store.dispatch('get_account_organisations');
  await waitFor(() => {
    expect(store.state.authenticationstore.selectedOrganisation === null).toBe(false);
  });
  const submitButton = getByTestId('submit-button');
  expect(submitButton).toHaveAttribute('type', 'submit');
  await fireEvent.click(submitButton);
  expect(emitted()).not.toHaveProperty(events.submit);
  const nameInput = getByTestId('proposal-name-input');
  const phoneInput = getByTestId('contact-phone-input');
  // valid input
  // name
  await fireEvent.click(submitButton);
  expect(emitted()).not.toHaveProperty(events.submit);
  expect(await findByText(errorMessages.missing.name));
  await fireEvent.update(nameInput, proposal.name);
  expect(await queryByText(errorMessages.missing.name)).not.toBeInTheDocument();
  // phoneNumber
  await fireEvent.click(submitButton);
  expect(emitted()).not.toHaveProperty(events.submit);
  expect(await findByText(errorMessages.missing.phoneNumber));
  await fireEvent.update(phoneInput, proposal.phoneNumber);
  expect(await queryByText(errorMessages.missing.phoneNumber)).not.toBeInTheDocument();
  expect(await queryByText(errorMessages.invalid.phoneNumber)).not.toBeInTheDocument();
  // submit
  await fireEvent.click(submitButton);
  await waitFor(() => {
    expect(emitted()).toHaveProperty(events.submit);
  });
  delete emitted()[events.submit];
  // clear input
  await fireEvent.update(nameInput, '');
  await fireEvent.update(phoneInput, '');
  expect(await queryByText(errorMessages.missing.name)).toBeInTheDocument();
  expect(await findByText(errorMessages.missing.name)).toBeInTheDocument();
  expect(await findByText(errorMessages.missing.phoneNumber)).toBeInTheDocument();
  // try submit again
  await fireEvent.click(submitButton);
  expect(emitted()).not.toHaveProperty(events.submit);
  // invalid input
  await fireEvent.update(nameInput, proposal.name);
  await fireEvent.update(phoneInput, '651651651651651651');
  // try to submit
  await fireEvent.click(submitButton);
  expect(emitted()).not.toHaveProperty(events.submit);
  expect(await findByText(errorMessages.invalid.phoneNumber)).toBeInTheDocument();
});
